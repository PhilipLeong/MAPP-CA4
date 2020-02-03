package mapp.com.sg.bookhub.storeui;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.List;

import mapp.com.sg.bookhub.BookDetails;
import mapp.com.sg.bookhub.IndividualSchoolActivity;
import mapp.com.sg.bookhub.Models.Post;
import mapp.com.sg.bookhub.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements View.OnClickListener {

    private List<Post> postItems;
    private Context context;
    private static DecimalFormat df2 = new DecimalFormat("##.00");

    public MyAdapter(List<Post> posts, Context context){
        this.postItems = posts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bookpreview_cardview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Post postItem = postItems.get(position);
        holder.titleTV.setText(postItem.getTitle());
        holder.isbnTV.setText(postItem.getIsbn());

        String price = df2.format(postItem.getPrice());
        holder.priceTV.setText("SGD " + price);
        //holder.titleTV.setText(postItem.getTitle());
        holder.morebtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Store page click:", postItem.getTitle());
                Intent intent = new Intent(context, BookDetails.class);
                intent.putExtra("BOOK", postItem);
                context.startActivity(intent);
            }
        });

        StorageReference downloadURL = FirebaseStorage.getInstance().getReferenceFromUrl(postItem.getImgs().get(0));

        final long ONE_MEGABYTE = 1024 * 1024;
        downloadURL.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                holder.previewImg.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

    }

    @Override
    public int getItemCount() {
        return postItems.size();
    }

    @Override
    public void onClick(View v) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTV;
        public TextView isbnTV;
        public TextView priceTV;
        public ImageButton morebtn;
        public ImageView previewImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTV = (TextView) itemView.findViewById(R.id.booktitle_TV);
            isbnTV = (TextView) itemView.findViewById(R.id.isbn_TV);
            priceTV = (TextView) itemView.findViewById(R.id.price_TV);
            morebtn = (ImageButton) itemView.findViewById(R.id.more_Btn);
            previewImg = (ImageView) itemView.findViewById(R.id.bookimage_IV);
        }
    }

}
