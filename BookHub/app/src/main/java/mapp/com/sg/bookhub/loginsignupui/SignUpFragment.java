package mapp.com.sg.bookhub.loginsignupui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private FirebaseFirestore db;


    private ProgressDialog progessDialog;

    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.signupfragment, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        editTextEmail = (EditText) rootView.findViewById(R.id.email_input);
        editTextPassword = (EditText) rootView.findViewById(R.id.password_input);
        editTextAccount = (EditText) rootView.findViewById(R.id.account_input);
        editTextSchoolCourse = (EditText) rootView.findViewById(R.id.schoolcourse_input);
        editTextBio = (EditText) rootView.findViewById(R.id.bio_input);
        registerButton =(Button)rootView.findViewById(R.id.signup_btn);
        progessDialog = new ProgressDialog(getContext());

        registerButton.setOnClickListener(this);

        return rootView;
    }

    private void registerUser(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        final String account = editTextAccount.getText().toString().trim();
        final String schoolcourse = editTextSchoolCourse.getText().toString().trim();
        final String bio = editTextBio.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            //Email is empty
            Toast.makeText(getContext(), "Please enter email", Toast.LENGTH_SHORT).show();
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
                            startActivity(new Intent(getContext(), MainActivity.class));
                        }
                        else{
                            Toast.makeText(getContext(), "Unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view){
        if(view == registerButton){
            registerUser();
        }
    }
}
