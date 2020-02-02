package mapp.com.sg.bookhub;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

public class ProfileActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        email = (TextView) findViewById(R.id.myemail_TV);
        username = (TextView) findViewById(R.id.myname_TV);
        bio = (TextView) findViewById(R.id.mybio_TV);
        schoolcourse = (TextView) findViewById(R.id.mycourse_TV);
        profilepic = (CircleImageView) findViewById(R.id.userImage_IV);
        profileBtn = (Button) findViewById(R.id.profile_Btn);

        Drawable active = ResourcesCompat.getDrawable(ProfileActivity.this.getResources(), R.drawable.profile_icon_active, null);
        profileBtn.setCompoundDrawablesWithIntrinsicBounds(null, active,null,null);
        profileBtn.setTextColor(Color.parseColor("#4a90e2"));

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userId = currentUser.getUid();
        loadDetails();
    }

    private void loadDetails(){
        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
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

}