package mapp.com.sg.bookhub;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mapp.com.sg.bookhub.Models.Order;
import mapp.com.sg.bookhub.Models.Post;
import mapp.com.sg.bookhub.storeui.ImageAdapter;

public class BookDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView pageHeader;
    private ImageButton backBtn;
    private Post book;

    private ProgressDialog progessDialog;

    private String school;
    private String TAG = "BOOK DETAILS";

    private TextView title;
    private TextView price;
    private TextView seller;
    private TextView publish;
    private TextView condition;
    private TextView mass;
    private TextView schedule;
    private TextView location;
    private TextView payment;

    private Button orderBtn;
    private ViewPager viewPager;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String userId;


    //error pop-up
    private Dialog errorDialog;
    private ImageButton errorCross;
    private TextView errorTV;

    //success pop-up
    private Dialog successDialog;
    private ImageButton successCross;
    private TextView successTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdetails);


        Intent intent = getIntent();
        book = (Post) intent.getSerializableExtra("BOOK");
        school = book.getSchool();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

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
        publish = findViewById(R.id.publishTime_TV);

        title.setText(book.getTitle() + " - " + book.getIsbn() + " - " + book.getAuthor());
        price.setText("SGD " + book.getPrice().toString());
        publish.setText(book.getCreatedAt());
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

        orderBtn = findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(this);
        progessDialog = new ProgressDialog(this);


        errorDialog = new Dialog(this);
        errorDialog.setContentView(R.layout.errorpopup);
        errorCross = errorDialog.findViewById(R.id.exit_btn);
        errorCross.setOnClickListener(this);
        errorTV = errorDialog.findViewById(R.id.error_TV);

        successDialog = new Dialog(this);
        successDialog.setContentView(R.layout.successpopup);
        successCross = successDialog.findViewById(R.id.back_btn);
        successCross.setOnClickListener(this);
        successTV = successDialog.findViewById(R.id.success_TV);

        boolean canbuy = isMyBook();
        if(!canbuy){
            orderBtn.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == backBtn){
            Intent intent = new Intent(this, IndividualSchoolActivity.class);
            intent.putExtra("SCHOOL", school);
            startActivity(intent);
        }
        if (v == orderBtn){
            progessDialog.setMessage("Placing your order...");
            progessDialog.show();
            createOrder();
        }
    }

    private void createOrder(){
        Date currentTime = Calendar.getInstance().getTime();

        Order newOrder = new Order(book.getKey(),userId,currentTime.toString());
        db.collection("Orders").document().set(newOrder).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updatePost();
                progessDialog.dismiss();
                Toast.makeText(getApplicationContext(), "You have made a post! ", Toast.LENGTH_SHORT);
                Log.d(TAG, "Place an order successfully!");
                showSuccessPopup("You have placed the order!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error happened in ordering", e);
                        progessDialog.dismiss();
                        showErrorPopup("Failed to place order! Please Try again.");
                    }
                });
    }

    private void updatePost(){
        book.setHasBeenBought(true);
        db.collection("Posts").document(book.getKey()).set(book).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Updated a post to been bought!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error happened in updating", e);
                    }
                });

    }

    private void showErrorPopup(String errorMessage) {
        errorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        errorTV.setText(errorMessage);
        errorDialog.show();
    }

    private void showSuccessPopup(String successMessage) {
        successDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        successTV.setText(successMessage);
        successDialog.show();
    }

    private boolean isMyBook(){
        String bookCreatedBy = book.getCreatedBy();
        if(bookCreatedBy.equals(userId)){
            return true;
        }
        return false;
    }

}
