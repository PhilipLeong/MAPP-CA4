package mapp.com.sg.bookhub;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import mapp.com.sg.bookhub.Models.Post;
import mapp.com.sg.bookhub.storeui.MyAdapter;

public class IndividualSchoolActivity extends AppCompatActivity implements View.OnClickListener {

    private String selectedSchool;
    private final String TAG = "Individual School Store";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private TextView pageHeader;
    
    private List<Post> posts;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;


    private ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recylerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        pageHeader = (TextView)findViewById(R.id.pageTitle_TV); 
        posts = new ArrayList<Post>();


        Intent intent = getIntent();
        selectedSchool = intent.getStringExtra("SCHOOL");
        setHeader();

        db.collection("Posts")
                .whereEqualTo("school", selectedSchool)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documents = task.getResult();
                            if (documents.size() != 0) {

                                for (QueryDocumentSnapshot document : documents) {
                                    Log.d("here!", document.getId());
                                    Post p = document.toObject(Post.class);
                                    p.setKey(document.getId());
                                    posts.add(p);
                                    startRecycleView(posts);
                                }
                            }
                            else{
                                Log.d(TAG,"No book in this store.");
                                noBookImageView();
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        backBtn = (ImageButton) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);
    }

    private void startRecycleView (List<Post> posts){
        adapter = new MyAdapter(posts, this);
        recyclerView.setAdapter(adapter);
    }


    private void noBookImageView (){
        Log.d(TAG,"No book in this store.");
        ImageView nobookIV = new ImageView(this);
        nobookIV.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        nobookIV.setImageResource(R.drawable.nobook);

        recyclerView.addView(nobookIV);
    }

    private void setHeader (){
        String header;
        switch (selectedSchool){
            case "SOC":
                header = "School Of Computing";
                break;

            case "SB":
                header = "Business School";
                break;

            case "MAE":
                header = "Mechanical Aeronautical Engineering";
                break;

            case "CLS":
                header = "Chemical Life Science";
                break;

            case "ABE":
                header = "Architecture The Built Environment";
                break;

            case "EEE":
                header = "Electrical & Electronic Engineering";
                break;

            case "MAD":
                header = "Media, Arts & Design";
                break;

            case "SMA":
                header = "Singapore Maritime Academy";
                break;
            default:
                header="school";
        }
        pageHeader.setText(header);
        
    }

    @Override
    public void onClick(View v) {
        if (v == backBtn) {
            Intent intent = new Intent(this, StoreActivity.class);
            startActivity(intent);
        }
    }
}
