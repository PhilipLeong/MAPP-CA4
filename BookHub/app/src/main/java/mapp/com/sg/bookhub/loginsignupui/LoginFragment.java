package mapp.com.sg.bookhub.loginsignupui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import mapp.com.sg.bookhub.HomeActivity;
import mapp.com.sg.bookhub.R;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button signinButton;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressDialog progressDialog;
    private Dialog errorDialog;
    private FirebaseAuth firebaseAuth;
    private ImageButton cross;
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
        errorDialog = new Dialog(getContext());
        errorDialog.setContentView(R.layout.errorpopup);
        cross = errorDialog.findViewById(R.id.exit_btn);

        cross.setOnClickListener(this);
        signinButton.setOnClickListener(this);
        return rootView;
    }

    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) ){
            showPopup();
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
                            Intent intent = new Intent(getContext(), HomeActivity.class);
                            startActivity(intent);
                        }
                        else{
                            showPopup();
                        }
                    }
                });


    }

    private void startHomepage(){
        Intent intent = new Intent(getContext(), HomeActivity.class);
        startActivity(intent);
    }

    private void showPopup(){
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView msg = errorDialog.findViewById(R.id.error_TV);
        msg.setText("Please enter valid credentials");
        errorDialog.show();
    }

    public void onClick(View view){
        if(view == signinButton) {
            userLogin();
        }
        if(view == cross){
            errorDialog.dismiss();
        }
    }
}
