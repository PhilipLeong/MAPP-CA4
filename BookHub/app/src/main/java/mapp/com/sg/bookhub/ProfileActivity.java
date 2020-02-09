package mapp.com.sg.bookhub;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import mapp.com.sg.bookhub.Models.User;
import mapp.com.sg.bookhub.profileui.SectionsPagerAdapter;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "My Profile";
    private TextView email;
    private TextView username;
    private TextView bio;
    private TextView schoolcourse;
    private CircleImageView profilepic;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private String userId;
    private FirebaseUser currentUser;
    private Button profileBtn;
    private Button storeBtn;
    private ImageButton postBtn;

    private TextView pageTitle;
    private ImageButton backHomeBtn;
    private ImageButton aboutbtn;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        pageTitle = (TextView) findViewById(R.id.pageTitle_TV);
        pageTitle.setText(TAG);

        backHomeBtn = (ImageButton) findViewById(R.id.back_btn);
        backHomeBtn.setOnClickListener(this);

        email = (TextView) findViewById(R.id.myemail_TV);
        username = (TextView) findViewById(R.id.myname_TV);
        bio = (TextView) findViewById(R.id.mybio_TV);
        schoolcourse = (TextView) findViewById(R.id.mycourse_TV);
        profilepic = (CircleImageView) findViewById(R.id.userImage_IV);
        profileBtn = (Button) findViewById(R.id.profile_Btn);
        storeBtn = (Button) findViewById(R.id.store_Btn);
        postBtn = (ImageButton) findViewById(R.id.post_Btn);
        aboutbtn = (ImageButton) findViewById(R.id.aboutus);
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userId = currentUser.getUid();

        aboutbtn.setOnClickListener(this);
        storeBtn.setOnClickListener(this);
        postBtn.setOnClickListener(this);

        Drawable active = ResourcesCompat.getDrawable(ProfileActivity.this.getResources(), R.drawable.profile_icon_active, null);
        profileBtn.setCompoundDrawablesWithIntrinsicBounds(null, active,null,null);
        profileBtn.setTextColor(Color.parseColor("#4a90e2"));

        loadDetails();
    }



    private void loadDetails(){
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(User.class);
                Log.d("Data", String.valueOf(documentSnapshot));
                email.setText("Email: "+currentUser.getEmail());
                username.setText(user.getAccount());
                bio.setText("Bio: "+user.getBio());
                schoolcourse.setText("Course: "+user.getSchoolCourse());
                final StorageReference reference = FirebaseStorage.getInstance().getReference();
                reference.child("profileImages").child(userId+".jpeg").getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        profilepic.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        });
    }

    public void onClick(View v){
        if(v == postBtn){
            Intent intent = new Intent(this, PostActivity.class);
            startActivity(intent);
        }
        if(v == storeBtn){
            Intent intent = new Intent(this, StoreActivity.class);
            startActivity(intent);
        }

        if (v == backHomeBtn){
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        }
        if(v == aboutbtn){
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
        }
    }

}