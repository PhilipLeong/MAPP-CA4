package mapp.com.sg.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class StoreActivity extends AppCompatActivity implements View.OnClickListener{
    private Button socBtn;
    private Button sbBtn;
    private Button maeBtn;
    private Button clsBtn;
    private Button abeBtn;
    private Button eeeBtn;
    private Button madBtn;
    private Button smaBtn;

    private Button profileBtn;
    private Button storeBtn;
    private ImageButton postBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        socBtn = (Button) findViewById(R.id.soc_Btn);
        socBtn.setOnClickListener(this);

        sbBtn = (Button) findViewById(R.id.sb_Btn);
        sbBtn.setOnClickListener(this);

        maeBtn = (Button) findViewById(R.id.mae_Btn);
        maeBtn.setOnClickListener(this);

        clsBtn = (Button) findViewById(R.id.cls_Btn);
        clsBtn.setOnClickListener(this);

        abeBtn = (Button) findViewById(R.id.abe_Btn);
        abeBtn.setOnClickListener(this);

        eeeBtn = (Button) findViewById(R.id.eee_Btn);
        eeeBtn.setOnClickListener(this);

        madBtn = (Button) findViewById(R.id.mad_Btn);
        madBtn.setOnClickListener(this);

        smaBtn = (Button) findViewById(R.id.sma_Btn);
        smaBtn.setOnClickListener(this);


        profileBtn = (Button) findViewById(R.id.profile_Btn);
        storeBtn = (Button) findViewById(R.id.store_Btn);
        postBtn = (ImageButton) findViewById(R.id.post_Btn);

        storeBtn.setOnClickListener(this);
        postBtn.setOnClickListener(this);
        profileBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if(v == postBtn){
            Intent intent = new Intent(this, PostActivity.class);
            startActivity(intent);
        }
        else if(v == storeBtn){
            Intent intent = new Intent(this, StoreActivity.class);
            startActivity(intent);
        }
        else if(v == profileBtn){
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
        else{
            String school = ((Button) v).getText().toString();
            Log.d("Store page click:", school);
            Intent intent = new Intent(this, IndividualSchoolActivity.class);
            intent.putExtra("SCHOOL", school);
            startActivity(intent);
        }
    }
}
