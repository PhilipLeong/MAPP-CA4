package mapp.com.sg.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageButton backbtn;
    private TextView pagetitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        backbtn = (ImageButton) findViewById(R.id.back_btn);
        pagetitle = (TextView) findViewById(R.id.pageTitle_TV);
        pagetitle.setText("About Us");
        backbtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == backbtn){
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        }
    }
}
