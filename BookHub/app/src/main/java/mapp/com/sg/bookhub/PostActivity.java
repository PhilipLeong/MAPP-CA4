package mapp.com.sg.bookhub;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import mapp.com.sg.bookhub.Models.Post;
import mapp.com.sg.bookhub.Models.User;

public class PostActivity extends  AppCompatActivity implements View.OnClickListener {

    private TextView pageTitle;
    private ImageButton backBtn;
    private Button postBtn;

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

    private FirebaseFirestore db;
    private static final String TAG = "PostActivity";

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

    }
    private void backToMainPage(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void makepost(){
        String titleInput = title.getText().toString().trim();
        String conditionInput = condition.getText().toString().trim();
        Double massInput = Double.parseDouble(mass.getText().toString().trim());
        Double priceInput = Double.parseDouble(price.getText().toString().trim());
        String locationInput = location.getText().toString().trim();
        String scheduleInput = schedule.getText().toString().trim();
        String schoolInput;
        boolean validation = true;


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
                    //school not selected, validation, error message
                    validation = false;
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


        if(TextUtils.isEmpty(titleInput) || TextUtils.isEmpty(conditionInput) || TextUtils.isEmpty(scheduleInput) || TextUtils.isEmpty(locationInput)){
            //something to be done
            validation = false;
            return;
        }


        //start input into database
        Post newPost = new Post (titleInput, conditionInput, massInput, priceInput, locationInput, scheduleInput, schoolInput, payments, userId);

        db.collection("Posts").document().set(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
        ;

    }

    public void onClick(View view){
        if(view == backBtn){
            backToMainPage();
        }
        if(view == postBtn){
            makepost();
        }
    }
}
