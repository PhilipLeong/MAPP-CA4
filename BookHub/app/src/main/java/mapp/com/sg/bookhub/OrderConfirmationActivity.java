package mapp.com.sg.bookhub;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import mapp.com.sg.bookhub.Models.Order;
import mapp.com.sg.bookhub.Models.Post;
import mapp.com.sg.bookhub.Models.User;

public class OrderConfirmationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Order confirmation page";
    private TextView pageHeader;
    private ImageButton backBtn;

    private ImageView bookImgPreview;
    private TextView title;
    private TextView isbn;
    private TextView mass;
    private TextView price;
    private TextView schedule;
    private TextView location;
    private TextView payment;

    private ImageButton moreBtn;

    private Button orderConfirmBtn;

    private ProgressDialog progessDialog;

    public SharedPreferences preferences;
    public static final String MYPREFERENCES = "BOOKDETAILS";


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

    String bookId;
    Post book;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderconfirmation);

        pageHeader = findViewById(R.id.pageTitle_TV);
        pageHeader.setText("Order Confirmation");

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);

        Intent intent = getIntent();
        book = (Post) intent.getSerializableExtra("BOOK");

        preferences = getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);

        bookId = preferences.getString("Id","");

        title = findViewById(R.id.booktitle_TV);
        Log.d(TAG, preferences.getString("Price", ""));
        title.setText(preferences.getString("Title", ""));

        isbn = findViewById(R.id.isbn_TV);
        isbn.setText(preferences.getString("ISBN", ""));

        price = findViewById(R.id.price_TV);
        price.setText(preferences.getString("Price", ""));

        mass = findViewById(R.id.massVal_TV);
        mass.setText(preferences.getString("Mass", ""));

        location = findViewById(R.id.locationVal_TV);
        location.setText(preferences.getString("Location", ""));

        schedule = findViewById(R.id.scheduleVal_TV);
        schedule.setText(preferences.getString("Schedule", ""));


        payment = findViewById(R.id.paymentVal_TV);
        payment.setText(preferences.getString("Payments", ""));

        bookImgPreview = findViewById(R.id.bookimage_IV);

        StorageReference downloadURL = FirebaseStorage.getInstance().getReferenceFromUrl(preferences.getString("ImgUrl", ""));

        final long ONE_MEGABYTE = 1024 * 1024;
        downloadURL.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bookImgPreview.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        moreBtn = findViewById(R.id.more_Btn);
        moreBtn.setOnClickListener(this);

        orderConfirmBtn = findViewById(R.id.orderBtn);
        orderConfirmBtn.setText("Order " + preferences.getString("Price", ""));
        orderConfirmBtn.setOnClickListener(this);



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

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
    }

    @Override
    public void onClick(View v) {
        if(v == backBtn || v == moreBtn){
            Intent intent = new Intent(this, BookDetailsActivity.class);
            intent.putExtra("BOOK", book);
            startActivity(intent);
        }
        else if (v == orderConfirmBtn){
            progessDialog.setMessage("Placing your order...");
            progessDialog.show();
            createOrder();
        }
        else if (v == errorCross){
            errorDialog.dismiss();
        }
        else if (v == successCross){
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
    }

    private void createOrder() {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentTime = Calendar.getInstance().getTime();
        String current = df.format(currentTime.getTime());

        Order newOrder = new Order(bookId, userId, current);
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



    private void updatePost() {
        db.collection("Posts").document(bookId).update("hasBeenBought", true).addOnSuccessListener(new OnSuccessListener<Void>() {
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
}
