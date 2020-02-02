package mapp.com.sg.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
    }

    @Override
    public void onClick(View v) {
        String school = ((Button) v).getText().toString();
        Log.d("Store page click:", school);
        Intent intent = new Intent(this, IndividualSchoolActivity.class);
        intent.putExtra("SCHOOL", school);
        startActivity(intent);
    }
}
