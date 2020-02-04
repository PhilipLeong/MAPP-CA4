package mapp.com.sg.bookhub.profileui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import mapp.com.sg.bookhub.Models.Post;
import mapp.com.sg.bookhub.R;


public class ListingsFragment extends Fragment {
    private List<Post> posts;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progessDialog;
    private GridView gridview;
    private String userid;
    private ListingsAdapter adapter;
    private final String TAG = "Listings";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.listingsfragment, container, false);
        progessDialog = new ProgressDialog(getContext());
        progessDialog.setMessage("Loading listings now...");
        progessDialog.show();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        userid = firebaseAuth.getCurrentUser().getUid();

        gridview = (GridView) rootView.findViewById(R.id.listings_GridView);
        posts = new ArrayList<Post>();
        Log.d("Testing",userid);
        db.collection("Posts")
                .whereEqualTo("createdBy", "5KjYdlC58WQnQVX8dhRahmgQlYJ3")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progessDialog.dismiss();
                        if (task.isSuccessful()) {
                            QuerySnapshot documents = task.getResult();
                            if (documents.size() != 0) {

                                for (QueryDocumentSnapshot document : documents) {
                                    Log.d("here!", document.getId());
                                    Post p = document.toObject(Post.class);
                                    p.setKey(document.getId());
                                    posts.add(p);
                                    startGridView(posts);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        return rootView;
    }

    private void startGridView (List<Post> posts){
        adapter = new ListingsAdapter(posts, getContext());
       gridview.setAdapter(adapter);
    }
}
