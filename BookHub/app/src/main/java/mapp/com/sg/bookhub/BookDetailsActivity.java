package mapp.com.sg.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import mapp.com.sg.bookhub.Models.Post;
import mapp.com.sg.bookhub.storeui.ImageAdapter;

public class BookDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView pageHeader;
    private ImageButton backBtn;
    private Post book;

    private String school;
    private String TAG = "BOOK DETAILS";

    private TextView title;
    private TextView price;
    private TextView seller;
    private TextView condition;
    private TextView mass;
    private TextView schedule;
    private TextView location;
    private TextView payment;

    private Button orderBtn;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdetails);

        Intent intent = getIntent();
        book = (Post) intent.getSerializableExtra("BOOK");
        school = book.getSchool();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ImageAdapter adapter = new ImageAdapter(this, book.getImgs());
        viewPager.setAdapter(adapter);


        pageHeader = (TextView)findViewById(R.id.pageTitle_TV);
        pageHeader.setText("Book Details");


        Log.d(TAG, book.getTitle());

        backBtn = (ImageButton) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);

        title = findViewById(R.id.bookInfo_TV);
        price = findViewById(R.id.price_TV);
        condition = findViewById(R.id.description_TV);
        mass = findViewById(R.id.mass_TV);
        schedule = findViewById(R.id.calendar_TV);
        location = findViewById(R.id.location_TV);
        payment = findViewById(R.id.payment_TV);

        title.setText(book.getTitle() + " - " + book.getIsbn() + " - " + book.getAuthor());
        price.setText(book.getPrice().toString());
        condition.setText(book.getCondition());
        mass.setText(book.getMass().toString());
        schedule.setText(book.getSchedule());
        List<String> payments = book.getPayments();
        String paymentStr = "";
        for (int i = 0; i < payments.size(); i++){
            paymentStr += payments.get(i);
            if(i != (payments.size() - 1)){
                paymentStr += ", ";
            }
        }
        payment.setText(paymentStr);

    }

    @Override
    public void onClick(View v) {
        if (v == backBtn){
            Intent intent = new Intent(this, IndividualSchoolActivity.class);
            intent.putExtra("SCHOOL", school);
            startActivity(intent);
        }
    }
}
