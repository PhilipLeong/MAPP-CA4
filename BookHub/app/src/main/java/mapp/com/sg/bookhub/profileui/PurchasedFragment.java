package mapp.com.sg.bookhub.profileui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import mapp.com.sg.bookhub.Models.Post;
import mapp.com.sg.bookhub.R;

public class PurchasedFragment extends Fragment {

    private String selectedSchool;
    private final String TAG = "Purchased";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private TextView pageHeader;

    private List<Post> posts;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    private String userid;
    private ImageButton backBtn;

    private Button profileBtn;
    private Button storeBtn;
    private ImageButton postBtn;
    private List<String> orders;

    private ImageView nobookImage;
    private TextView nobookText;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.purchasedfragment, container, false);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userid = firebaseAuth.getCurrentUser().getUid();

        nobookImage = (ImageView) rootView.findViewById(R.id.nobook_IV);
        nobookText = (TextView) rootView.findViewById(R.id.nobook_TV);

        profileBtn = (Button) rootView.findViewById(R.id.profile_Btn);
        storeBtn = (Button) rootView.findViewById(R.id.store_Btn);
        postBtn = (ImageButton) rootView.findViewById(R.id.post_Btn);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recylerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        nobookImage = (ImageView) rootView.findViewById(R.id.nobook_IV);
        nobookText = (TextView) rootView.findViewById(R.id.nobook_TV);

        pageHeader = (TextView)rootView.findViewById(R.id.pageTitle_TV);
        posts = new ArrayList<Post>();

        Log.d("test","Works!");
        Log.d("Userid",userid);
        db.collection("Orders")
                .whereEqualTo("createdBy", userid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot documents = task.getResult();
                            Log.d("Documents", ""+documents.size());
                            if (documents.size() != 0) {
                                nobookImage.setVisibility(View.GONE);
                                nobookText.setVisibility(View.GONE);
                                for (QueryDocumentSnapshot document : documents) {
                                    String order = document.getData().get("postId").toString();
                                    Log.d("Order: ",order);
                                    DocumentReference docRef = db.collection("Posts").document(order);
                                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Post p = documentSnapshot.toObject(Post.class);
                                            posts.add(p);
                                            startRecycleView(posts);
                                        }
                                    });
                                }
                            }
                            else{
                                recyclerView.setVisibility(View.GONE);
                                Log.d(TAG,"No purchased books");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return rootView;
    }

    private void startRecycleView (List<Post> posts){
        adapter = new PurchasedAdapter(posts, getContext());
        Log.d("Adapter",adapter.toString());
        recyclerView.setAdapter(adapter);
    }

}
