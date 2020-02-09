package mapp.com.sg.bookhub.profileui;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.List;

import mapp.com.sg.bookhub.Models.Post;
import mapp.com.sg.bookhub.R;

public class ListingsAdapter extends BaseAdapter {

    private List<Post> postItems;
    private Context context;

    private static DecimalFormat df2 = new DecimalFormat("##.00");

    public ListingsAdapter(List<Post> posts, Context context){
        this.postItems = posts;
        this.context = context;
    }
    // Count the number of image items
    @Override
    public int getCount() {
        return postItems.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    // Display the image in the GridView
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profilelisting_cardview, parent, false);
        final Post postItem = postItems.get(position);
        final ImageView bookpreview = (ImageView) convertView.findViewById(R.id.bookPreview1_IV);
        final TextView title = (TextView) convertView.findViewById(R.id.bookTitle1_TV);
        final TextView price = (TextView) convertView.findViewById(R.id.bookPrice1_TV);
        final Button editpost = (Button) convertView.findViewById(R.id.editpost_Btn);
        String pricetext = df2.format(postItem.getPrice());
        if(postItem.getHasBeenBought() == true){
            editpost.setVisibility(View.GONE);
        }

        title.setText(postItem.getTitle());
        price.setText("SGD "+ pricetext);


        editpost.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("Edit Details click:", postItem.getTitle());
                Intent intent = new Intent(context, EditActivity.class);
                intent.putExtra("LISTING", postItem.getKey());
                context.startActivity(intent);
            }
        });

        StorageReference downloadURL = FirebaseStorage.getInstance().getReferenceFromUrl(postItem.getImgs().get(0));

        final long ONE_MEGABYTE = 1024 * 1024;
        downloadURL.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bookpreview.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        return convertView;
    }

}
