package mapp.com.sg.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener  {

    private ImageButton postbtn;
    private ImageButton profilebtn;
    private ImageButton logoutbtn;
    private ImageButton storebtn;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        firebaseAuth = FirebaseAuth.getInstance();
        postbtn = (ImageButton) findViewById(R.id.post_ImgBtn);
        profilebtn = (ImageButton) findViewById(R.id.profile_ImgBtn);
        logoutbtn = (ImageButton) findViewById(R.id.logout_ImgBtn);
        storebtn = (ImageButton) findViewById(R.id.store_ImgBtn);

        logoutbtn.setOnClickListener(this);
        profilebtn.setOnClickListener(this);
        postbtn.setOnClickListener(this);
        storebtn.setOnClickListener(this);
    }

    public void onClick(View view){
        if(view == postbtn) {
            Intent intent = new Intent(this, PostActivity.class);
            startActivity(intent);
        }

        if(view == profilebtn){
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        if(view == logoutbtn){
            firebaseAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        if(view == storebtn){
            Intent intent = new Intent(this, StoreActivity.class);
            startActivity(intent);
        }
    }
}
