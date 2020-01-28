package mapp.com.sg.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener  {

    private ImageButton postbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        postbtn = (ImageButton) findViewById(R.id.post_ImgBtn);

        postbtn.setOnClickListener(this);
    }

    public void onClick(View view){
        if(view == postbtn) {
            Intent intent = new Intent(this, PostActivity.class);
            startActivity(intent);
        }
    }
}
