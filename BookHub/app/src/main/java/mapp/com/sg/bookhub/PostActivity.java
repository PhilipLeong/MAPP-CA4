package mapp.com.sg.bookhub;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import mapp.com.sg.bookhub.Models.Post;


public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView pageTitle;
    private ImageButton backBtn;
    private Button postBtn;

    //error pop-up
    private Dialog errorDialog;
    private ImageButton errorCross;
    private TextView errorTV;

    //success pop-up
    private Dialog successDialog;
    private ImageButton successCross;
    private TextView successTV;

    private ProgressDialog progessDialog;

    private EditText title;
    private EditText condition;
    private EditText mass;
    private EditText price;
    private EditText location;
    private EditText schedule;
    private RadioGroup radioGroup1;
    private RadioGroup radioGroup2;
    private RadioGroup radioGroup3;
    private RadioButton radioSelected;
    private CheckBox chkpaynow;
    private CheckBox chkcash;
    private CheckBox chkpaylah;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private List<String> payments;

    private ImageButton add1;
    private ImageButton add2;
    private ImageButton add3;

    private FirebaseFirestore db;
    private static final String TAG = "PostActivity";
    int TAKE_IMAGE_CODE =  10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postfragment);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        pageTitle = (TextView) findViewById(R.id.pageTitle_TV);
        pageTitle.setText("Post");

        backBtn = (ImageButton) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);

        postBtn = (Button) findViewById(R.id.post_Btn);
        postBtn.setOnClickListener(this);

        title = (EditText) findViewById(R.id.book_Input);
        condition = (EditText) findViewById(R.id.itemcond_Input);
        mass = (EditText) findViewById(R.id.mass_Input);
        price = (EditText) findViewById(R.id.price_Input);
        location = (EditText) findViewById(R.id.location_Input);
        schedule = (EditText) findViewById(R.id.schedule_Input);

        radioGroup1 = (RadioGroup) findViewById(R.id.rbGrp1);
        radioGroup2 = (RadioGroup) findViewById(R.id.rbGrp2);
        radioGroup3 = (RadioGroup) findViewById(R.id.rbGrp3);

        chkpaynow = (CheckBox) findViewById(R.id.paynow_Chkbox);
        chkpaylah = (CheckBox) findViewById(R.id.paylah_Chkbox);
        chkcash = (CheckBox) findViewById(R.id.cash_Chkbox);

        payments = new ArrayList<>();

        add1 = (ImageButton) findViewById(R.id.addImgBtn1);
        add2 = (ImageButton) findViewById(R.id.addImgBtn2);
        add3 = (ImageButton) findViewById(R.id.addImgBtn3);
        add1.setOnClickListener(this);
        add2.setOnClickListener(this);
        add3.setOnClickListener(this);

        errorDialog = new Dialog(this);
        errorDialog.setContentView(R.layout.custompopup);
        errorCross = errorDialog.findViewById(R.id.exit_btn);
        errorCross.setOnClickListener(this);
        errorTV = errorDialog.findViewById(R.id.error_TV);

        progessDialog = new ProgressDialog(this);
    }


    private void backToMainPage(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }



    private void makepost(){
        Double massFinal = 0.0;
        Double priceFinal = 0.0;
        String schoolInput;
        //Get and validate radio button, (School input)
        int selectedId = radioGroup1.getCheckedRadioButtonId();
        if(selectedId != -1){
            radioSelected = (RadioButton) findViewById(radioGroup1.getCheckedRadioButtonId());
            schoolInput = radioSelected.getText().toString();
        }
        else {
            selectedId = radioGroup2.getCheckedRadioButtonId();
            if (selectedId != -1){
                radioSelected = (RadioButton) findViewById(radioGroup2.getCheckedRadioButtonId());
                schoolInput = radioSelected.getText().toString();
            }
            else{
                selectedId = radioGroup3.getCheckedRadioButtonId();
                if (selectedId != -1){
                    radioSelected = (RadioButton) findViewById(radioGroup3.getCheckedRadioButtonId());
                    schoolInput = radioSelected.getText().toString();
                }
                else{
                    showErrorPopup("Please select the school :).");
                    return;
                }
            }
        }


        //Get checkbox
        payments.clear();
        if (chkcash.isChecked()){
            payments.add(chkcash.getText().toString());
        }
        if (chkpaylah.isChecked()){
            payments.add(chkpaylah.getText().toString());
        }
        if (chkpaynow.isChecked()){
            payments.add(chkpaynow.getText().toString());
        }
        if (payments.size() == 0){
            showErrorPopup("Please check at least one payment method.");
            return;
        }

        String titleInput = title.getText().toString().trim();
        String conditionInput = condition.getText().toString().trim();
        String massInput = mass.getText().toString().trim();
        String priceInput = price.getText().toString().trim();
        String locationInput = location.getText().toString().trim();
        String scheduleInput = schedule.getText().toString().trim();


        if(TextUtils.isEmpty(titleInput) || TextUtils.isEmpty(conditionInput) || TextUtils.isEmpty(scheduleInput) || TextUtils.isEmpty(locationInput)|| TextUtils.isEmpty(massInput)|| TextUtils.isEmpty(priceInput) ){
            showErrorPopup("Please don't leave any field blank.");
            return;
        }

        try{
            massFinal = Double.parseDouble(massInput);
            priceFinal = Double.parseDouble(priceInput);
        }
        catch(Exception ex){
            showErrorPopup("Please provide valid Mass / Price input.");
            return;
        }


        //start input into database
        Post newPost = new Post (titleInput, conditionInput, massFinal, priceFinal, locationInput, scheduleInput, schoolInput, payments, userId);

        progessDialog.setMessage("Posting in progress...");
        progessDialog.show();

        db.collection("Posts").document().set(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progessDialog.dismiss();
                //Toast.makeText(getApplicationContext(),"You have made a post! ",Toast.LENGTH_SHORT);
                Log.d(TAG, "Make a post successfully!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error happened in posting", e);
                    }
                });
        ;

    }



    private void uploadBookPic(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getApplicationContext().getPackageManager()) != null){
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
                    add1.setImageBitmap(bitmap);
                    //handleUpload(bitmap);
            }
        }
    }



    private void showErrorPopup(String errorMessage){
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        errorTV.setText(errorMessage);
        errorDialog.show();
    }



    public void onClick(View view){
        if(view == backBtn){
            backToMainPage();
        }
        if(view == postBtn){
            makepost();
        }
        if(view == add1 || view == add2 || view == add3){
            uploadBookPic();
        }
        if(view == errorCross){
            errorDialog.dismiss();
        }
    }
}
