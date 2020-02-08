package mapp.com.sg.bookhub;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import mapp.com.sg.bookhub.Models.Order;
import mapp.com.sg.bookhub.Models.Post;
import mapp.com.sg.bookhub.Models.User;
import mapp.com.sg.bookhub.storeui.ImageAdapter;

public class BookDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView pageHeader;
    private ImageButton backBtn;
    private Post book;


    private String school;
    private String TAG = "BOOK DETAILS";

    private TextView title;
    private TextView price;
    private TextView publish;
    private TextView condition;
    private TextView mass;
    private TextView schedule;
    private TextView location;
    private TextView payment;
    private TextView sellerName;
    private ImageButton sellerProfile;

    private Button orderBtn;
    private ViewPager viewPager;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String userId;

    private User seller;

    private Dialog profileDialog;

    private TextView popupemail;
    private TextView popupbio;
    private TextView popupusername;
    private TextView popupschoolcourse;
    private CircleImageView popupprofilepic;
    private ImageButton profileCross;


    public SharedPreferences preferences;
    public static final String MYPREFERENCES = "BOOKDETAILS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdetails);

        preferences = getSharedPreferences(MYPREFERENCES, MODE_PRIVATE);

        Intent intent = getIntent();
        book = (Post) intent.getSerializableExtra("BOOK");
        school = book.getSchool();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        viewPager = findViewById(R.id.viewpager);
        ImageAdapter adapter = new ImageAdapter(this, book.getImgs());
        viewPager.setAdapter(adapter);


        pageHeader = findViewById(R.id.pageTitle_TV);
        pageHeader.setText("Book Details");


        Log.d(TAG, book.getTitle());

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);

        title = findViewById(R.id.bookInfo_TV);
        price = findViewById(R.id.price_TV);
        condition = findViewById(R.id.description_TV);
        mass = findViewById(R.id.mass_TV);
        schedule = findViewById(R.id.calendar_TV);
        location = findViewById(R.id.location_TV);
        payment = findViewById(R.id.payment_TV);
        publish = findViewById(R.id.publishTime_TV);
        sellerName = findViewById(R.id.seller_TV);
        sellerProfile = findViewById(R.id.seller_profile);

        title.setText(book.getTitle() + " - " + book.getIsbn() + " - " + book.getAuthor());
        price.setText("SGD " + book.getPrice().toString());
        publish.setText(book.getCreatedAt());
        condition.setText(book.getCondition());
        mass.setText(book.getMass().toString() + " kg");
        schedule.setText(book.getSchedule());
        location.setText(book.getLocation());
        List<String> payments = book.getPayments();
        String paymentStr = "";
        for (int i = 0; i < payments.size(); i++) {
            paymentStr += payments.get(i);
            if (i != (payments.size() - 1)) {
                paymentStr += ", ";
            }
        }
        payment.setText(paymentStr);

        orderBtn = findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(this);


        DocumentReference docRef = db.collection("users").document(book.getCreatedBy());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        seller = document.toObject(User.class);
                        if (isMyBook()) {
                            Log.d(TAG, "This book is published by myself");
                            sellerName.setText(seller.getAccount() + " (You) ");
                            sellerProfile.setVisibility(View.GONE);
                            orderBtn.setVisibility(View.GONE);
                        }
                        else if(book.getHasBeenBought()){
                            orderBtn.setVisibility(View.GONE);
                        }
                    } else {
                        Log.d(TAG, "No such seller");
                    }
                } else {
                    Log.d(TAG, "get seller failed with ", task.getException());
                }
            }
        });


        profileDialog = new Dialog(this);
        profileDialog.setContentView(R.layout.profilepopup);
        profileCross = profileDialog.findViewById(R.id.exit_btn);
        sellerProfile = (ImageButton) findViewById(R.id.seller_profile);


        popupemail = (TextView) profileDialog.findViewById(R.id.myemail_TV);
        popupusername = (TextView) profileDialog.findViewById(R.id.myname_TV);
        popupbio = (TextView) profileDialog.findViewById(R.id.mybio_TV);
        popupschoolcourse = (TextView) profileDialog.findViewById(R.id.mycourse_TV);
        popupprofilepic = (CircleImageView) profileDialog.findViewById(R.id.userImage_IV);


        sellerProfile.setOnClickListener(this);
        profileCross.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        if (v == backBtn) {
            Intent intent = new Intent(this, IndividualSchoolActivity.class);
            intent.putExtra("SCHOOL", school);
            startActivity(intent);
        }
        if (v == orderBtn) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("Title", book.getTitle());
            editor.putString("ISBN", book.getIsbn());
            editor.putString("Price", (String) price.getText());
            editor.putString("Mass", (String) mass.getText());
            editor.putString("Location", book.getLocation());
            editor.putString("Schedule", book.getSchedule());
            editor.putString("Payments", (String) payment.getText());
            editor.putString("Id", book.getKey());
            editor.putString("ImgUrl", book.getImgs().get(0));

            editor.commit();
            Intent intent = new Intent(this, OrderConfirmationActivity.class);
            intent.putExtra("BOOK", book);
            startActivity(intent);
        }

        if(v == profileCross){
            profileDialog.dismiss();
        }
        if(v == sellerProfile){
            showPopup();
        }
    }



    private boolean isMyBook() {
        String bookCreatedBy = book.getCreatedBy();
        Log.d(TAG, "seller is " + bookCreatedBy);
        Log.d(TAG, "I'm " + userId);
        return bookCreatedBy.equals(userId);
    }

    private void showPopup(){
        profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DocumentReference docRef = db.collection("users").document(book.getCreatedBy());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                seller = documentSnapshot.toObject(User.class);
                Log.d("Data", String.valueOf(documentSnapshot));
                popupemail.setText("Email: ???????????");
                popupusername.setText(seller.getAccount());
                popupbio.setText("Bio: "+seller.getBio());
                popupschoolcourse.setText("Course: "+seller.getSchoolCourse());

                final StorageReference reference = FirebaseStorage.getInstance().getReference();
                reference.child("profileImages").child(userId+".jpeg").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        popupprofilepic.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        });
        Log.d("InputEmail",popupemail.getText().toString());
        profileDialog.show();
    }


}
