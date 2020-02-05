package mapp.com.sg.bookhub.loginsignupui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import mapp.com.sg.bookhub.MainActivity;
import mapp.com.sg.bookhub.Models.User;
import mapp.com.sg.bookhub.R;

public class SignUpFragment extends Fragment implements View.OnClickListener{

    private Button registerButton;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextAccount;
    private EditText editTextSchoolCourse;
    private EditText editTextBio;

    private TextView uploadtxt;

    private CircleImageView profilePic;
    private Dialog errorDialog;
    private ImageButton cross;
    private FirebaseFirestore db;
    int TAKE_IMAGE_CODE =  10001;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    public static final String TAG = "Sign up";
    private Bitmap picture;

    private static final int PICK_IMAGE_REQUEST = 1;

    private ProgressDialog progessDialog;

    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.signupfragment, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uploadtxt = (TextView) rootView.findViewById(R.id.uploadtext);
        editTextEmail = (EditText) rootView.findViewById(R.id.email_input);
        editTextPassword = (EditText) rootView.findViewById(R.id.password_input);
        editTextAccount = (EditText) rootView.findViewById(R.id.account_input);
        editTextSchoolCourse = (EditText) rootView.findViewById(R.id.schoolcourse_input);
        editTextBio = (EditText) rootView.findViewById(R.id.bio_input);
        registerButton =(Button)rootView.findViewById(R.id.signup_btn);
        profilePic = (CircleImageView) rootView.findViewById(R.id.uploadProfilepic_ImgBtn);
        errorDialog = new Dialog(getContext());
        errorDialog.setContentView(R.layout.errorpopup);
        cross = errorDialog.findViewById(R.id.exit_btn);

        progessDialog = new ProgressDialog(getContext());

        uploadtxt.setOnClickListener(this);
        cross.setOnClickListener(this);
        registerButton.setOnClickListener(this);
        profilePic.setOnClickListener(this);
        return rootView;
    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String account = editTextAccount.getText().toString().trim();
        final String schoolcourse = editTextSchoolCourse.getText().toString().trim();
        final String bio = editTextBio.getText().toString().trim();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(account) || TextUtils.isEmpty(schoolcourse) || TextUtils.isEmpty(bio)){
            //Email is empty
            showPopup();
            return;
        }

        if(TextUtils.isEmpty(password)){
            //Password is empty
            Toast.makeText(getContext(),"Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        progessDialog.setMessage("Registering user...");
        progessDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progessDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                            User user = new User(account, schoolcourse, bio);
                            String id = task.getResult().getUser().getUid();
                            db.collection("users").document(id).set(user);
                            if(picture != null)
                            handleUpload(picture);
                            startActivity(new Intent(getContext(), MainActivity.class));
                        }
                        else{
                            Toast.makeText(getContext(), "Unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadPicture(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getContext().getPackageManager()) != null){
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( requestCode == TAKE_IMAGE_CODE){
            switch(resultCode){
                case Activity.RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    profilePic.setImageBitmap(bitmap);
                    setBitmap(bitmap);
            }
        }
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            setBitmap(bitmap);
            profilePic.setImageURI(data.getData());
        }
    }

    private void setBitmap(Bitmap bitmap){
        picture = bitmap;
    }

    private void handleUpload(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profileImages")
                .child(uid+".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        getDownloadUrl(reference);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"onFailure", e.getCause());
                    }
                });
    }

    private void getDownloadUrl(StorageReference reference){
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: "+uri);
                        setUserProfileUrl(uri);
                    }
                });
    }

    private void setUserProfileUrl(Uri uri){
        FirebaseUser user = firebaseAuth.getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Profile picture uploaded", Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Profile picture upload failed...", Toast.LENGTH_SHORT);
                    }
                });
    }


    private void showPopup(){
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        errorDialog.show();
    }

    private void startGallery(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                pickImageFromGallery();
        }
    }

    private void pickImageFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }


    @Override
    public void onClick(View view){
        if(view == registerButton){
            registerUser();
        }
        if(view == profilePic){
            uploadPicture();
        }
        if(view == cross){
            errorDialog.dismiss();
        }
        if(view == uploadtxt){
            startGallery();
        }
    }
}
