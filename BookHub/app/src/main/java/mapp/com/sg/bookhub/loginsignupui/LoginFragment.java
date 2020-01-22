package mapp.com.sg.bookhub.loginsignupui;

import android.app.ProgressDialog;
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

import mapp.com.sg.bookhub.R;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button signinButton;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.loginfragment, container, false);

        firebaseAuth = firebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            //Profile activity here
        }

        signinButton = (Button) rootView.findViewById(R.id.login_btn);
        editTextEmail = (EditText) rootView.findViewById(R.id.loginemail_input);
        editTextPassword = (EditText) rootView.findViewById(R.id.loginpassword_input);
        progressDialog = new ProgressDialog(getContext());

        signinButton.setOnClickListener(this);
        return rootView;
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), "Login Successfully", Toast.LENGTH_SHORT).show();
                            //Start profile activity here
                        }
                        else{
                            Toast.makeText(getContext(), "Unsuccessful, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }
    public void onClick(View view){
        if(view == signinButton) {
            userLogin();
        }
    }
}
