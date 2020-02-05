package mapp.com.sg.bookhub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import mapp.com.sg.bookhub.Models.Post;
import mapp.com.sg.bookhub.storeui.BookPreviewAdapter;

public class IndividualSchoolActivity extends AppCompatActivity implements View.OnClickListener {

    private String selectedSchool;
    private final String TAG = "Individual School Store";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private TextView pageHeader;
    
    private List<Post> posts;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progessDialog;

    private ImageButton backBtn;

    private Button profileBtn;
    private Button storeBtn;
    private ImageButton postBtn;

    private ImageView nobookImage;
    private TextView nobookText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school);
        progessDialog = new ProgressDialog(this);
        progessDialog.setMessage("Loading books now...");
        progessDialog.show();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        nobookImage = (ImageView) findViewById(R.id.nobook_IV);
        nobookText = (TextView) findViewById(R.id.nobook_TV);

        profileBtn = (Button) findViewById(R.id.profile_Btn);
        storeBtn = (Button) findViewById(R.id.store_Btn);
        postBtn = (ImageButton) findViewById(R.id.post_Btn);

        storeBtn.setOnClickListener(this);
        postBtn.setOnClickListener(this);
        profileBtn.setOnClickListener(this);

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
                                nobookImage.setVisibility(View.GONE);
                                nobookText.setVisibility(View.GONE);
                                progessDialog.dismiss();
                                for (QueryDocumentSnapshot document : documents) {
                                    Log.d("here!", document.getId());
                                    Post p = document.toObject(Post.class);
                                    p.setKey(document.getId());
                                    posts.add(p);
                                    startRecycleView(posts);
                                }
                            }
                            else{
                                recyclerView.setVisibility(View.GONE);
                                progessDialog.dismiss();
                                Log.d(TAG,"No book in this store.");
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
        adapter = new BookPreviewAdapter(posts, this);
        recyclerView.setAdapter(adapter);
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
        if(v == postBtn){
            Intent intent = new Intent(this, PostActivity.class);
            startActivity(intent);
        }
        if(v == storeBtn){
            Intent intent = new Intent(this, StoreActivity.class);
            startActivity(intent);
        }
        if(v == profileBtn){
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        }
    }
}
