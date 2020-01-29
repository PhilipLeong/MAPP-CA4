package mapp.com.sg.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PostActivity extends  AppCompatActivity implements View.OnClickListener {

    private TextView pageTitle;
    private ImageButton backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        pageTitle = (TextView) findViewById(R.id.pageTitle_TV);
        pageTitle.setText("Post");

        backBtn = (ImageButton) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);

    }
    private void backToMainPage(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
    public void onClick(View view){
        if(view == backBtn){
            backToMainPage();
        }
    }
}
