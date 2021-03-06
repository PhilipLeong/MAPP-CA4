package mapp.com.sg.bookhub.profileui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import mapp.com.sg.bookhub.HomeActivity;
import mapp.com.sg.bookhub.Models.Post;
import mapp.com.sg.bookhub.R;

public class EditActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener{
    private TextView pageTitle;
    private ImageButton backBtn;
    private Button submitBtn;
    private Button deleteBtn;

    //error pop-up
    private Dialog errorDialog;
    private ImageButton errorCross;
    private TextView errorTV;

    //success pop-up
    private Dialog successDialog;
    private ImageButton successCross;
    private TextView successTV;

    private ProgressDialog progessDialog;

    private EditText title;
    private EditText condition;
    private EditText mass;
    private EditText price;
    private EditText location;
    private EditText schedule;
    private EditText isbn;
    private EditText author;
    private RadioGroup radioGroup1;
    private RadioGroup radioGroup2;

    private RadioButton radioSelected;

    private RadioButton socbtn;
    private RadioButton sbbtn;
    private RadioButton maebtn;
    private RadioButton clsbtn;
    private RadioButton eeebtn;
    private RadioButton abebtn;
    private RadioButton smabtn;
    private RadioButton madbtn;

    private CheckBox chkpaynow;
    private CheckBox chkcash;
    private CheckBox chkpaylah;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private List<String> payments;

    private List<String> imgs;


    private ImageButton add1;
    private ImageButton add2;
    private ImageButton add3;

    private FirebaseFirestore db;
    private static final String TAG = "EditActivity";
    private int TAKE_IMAGE_CODE = 10001;


    private ByteArrayOutputStream img1;
    private ByteArrayOutputStream img2;
    private ByteArrayOutputStream img3;

    private String listingid;
    private int currentUploadLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        listingid = intent.getStringExtra("LISTING");

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();

        pageTitle = (TextView) findViewById(R.id.pageTitle_TV);
        pageTitle.setText("Edit Post");

        backBtn = (ImageButton) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);

        submitBtn = (Button) findViewById(R.id.submit_Btn);
        deleteBtn = (Button) findViewById(R.id.delete_Btn);
        deleteBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        title = (EditText) findViewById(R.id.book_Input);
        condition = (EditText) findViewById(R.id.itemcond_Input);
        mass = (EditText) findViewById(R.id.mass_Input);
        price = (EditText) findViewById(R.id.price_Input);
        location = (EditText) findViewById(R.id.location_Input);
        schedule = (EditText) findViewById(R.id.schedule_Input);
        isbn = (EditText) findViewById(R.id.isbn_Input);
        author = (EditText) findViewById(R.id.author_Input);

        socbtn = (RadioButton) findViewById(R.id.soc_RadioBtn);
        sbbtn = (RadioButton) findViewById(R.id.sb_RadioBtn);
        maebtn = (RadioButton) findViewById(R.id.mae_RadioBtn);
        clsbtn = (RadioButton) findViewById(R.id.cls_RadioBtn);
        eeebtn = (RadioButton) findViewById(R.id.eee_RadioBtn);
        abebtn = (RadioButton) findViewById(R.id.abe_RadioBtn);
        smabtn = (RadioButton) findViewById(R.id.sma_RadioBtn);
        madbtn = (RadioButton) findViewById(R.id.mad_RadioBtn);



        chkpaynow = (CheckBox) findViewById(R.id.paynow_Chkbox);
        chkpaylah = (CheckBox) findViewById(R.id.paylah_Chkbox);
        chkcash = (CheckBox) findViewById(R.id.cash_Chkbox);

        payments = new ArrayList<>();

        add1 = (ImageButton) findViewById(R.id.addImgBtn1);
        add2 = (ImageButton) findViewById(R.id.addImgBtn2);
        add3 = (ImageButton) findViewById(R.id.addImgBtn3);
        add1.setOnClickListener(this);
        add2.setOnClickListener(this);
        add3.setOnClickListener(this);

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

        progessDialog = new ProgressDialog(this);
        imgs = new ArrayList<>();


        radioGroup1 = (RadioGroup) findViewById(R.id.rbGrp1);
        radioGroup2 = (RadioGroup) findViewById(R.id.rbGrp2);
        radioGroup1.setOnCheckedChangeListener(this);
        radioGroup2.setOnCheckedChangeListener(this);
        loadData();
    }

    private void loadData(){
        DocumentReference docRef = db.collection("Posts").document(listingid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                progessDialog.dismiss();
                Post post = documentSnapshot.toObject(Post.class);
                Log.d("Data", String.valueOf(documentSnapshot));
                title.setText(post.getTitle());
                isbn.setText(post.getIsbn());
                condition.setText(post.getCondition());
                mass.setText(Double.toString(post.getMass()));
                price.setText(Double.toString(post.getPrice()));
                location.setText(post.getLocation());
                schedule.setText(post.getSchedule());
                author.setText(post.getAuthor());

                final StorageReference reference = FirebaseStorage.getInstance().getReference();
                for(int i=0;i<post.getImgs().size();i++){
                    StorageReference downloadURL = FirebaseStorage.getInstance().getReferenceFromUrl(post.getImgs().get(i));
                    final long ONE_MEGABYTE = 1024 * 1024;
                    switch(i){
                        case 0:
                            downloadURL.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                   add1.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                   img1 = handleUpload(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                            break;
                        case 1:
                            downloadURL.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    add2.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                    img2 = handleUpload(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                            break;
                        case 2:
                            downloadURL.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    add3.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                    img3 = handleUpload(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                            break;
                    }
                }//End of for loop
                setSchoolInput(post.getSchool());
                setPaymentInputs(post.getPayments());
            }
        });
    }

    private void setSchoolInput(String school){
        switch(school){
            case("SOC"):
                socbtn.setChecked(true);
                break;
            case("SB"):
                sbbtn.setChecked(true);
                break;
            case("MAE"):
                maebtn.setChecked(true);
                break;
            case("CLS"):
                clsbtn.setChecked(true);
                break;
            case("EEE"):
                eeebtn.setChecked(true);
                break;
            case("ABE"):
                abebtn.setChecked(true);
                break;
            case("SMA"):
                smabtn.setChecked(true);
                break;
            case("MAD"):
                madbtn.setChecked(true);
                break;
        }
    }

    private void setPaymentInputs(List<String> payments){
        for(int i=0;i<payments.size();i++){
            switch (payments.get(i)){
                case("PayNow"):
                    chkpaynow.setChecked(true);
                    break;
                case("Cash"):
                    chkcash.setChecked(true);
                    break;
                case("PayLah"):
                    chkpaylah.setChecked(true);
                    break;
            }
        }
    }

    private void backToMainPage() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }


    private void takeBookPic() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(intent, TAKE_IMAGE_CODE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    if (currentUploadLoc == 1) {
                        add1.setImageBitmap(bitmap);
                        img1 = handleUpload(bitmap);
                    }
                    if (currentUploadLoc == 2) {
                        add2.setImageBitmap(bitmap);
                        img2 = handleUpload(bitmap);
                    }
                    if (currentUploadLoc == 3) {
                        add3.setImageBitmap(bitmap);
                        img3 = handleUpload(bitmap);
                    }
            }
        }
    }

    private ByteArrayOutputStream handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos;
    }


    public void upload1(ByteArrayOutputStream baos) {
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("bookImages")
                .child(userId + Math.random() * 1000 * Math.random() + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                              Log.d(TAG, "canupload");
                                              reference.getDownloadUrl()
                                                      .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                          public void onSuccess(Uri uri) {
                                                              Log.d(TAG, "get uri");

                                                              imgs.add(uri.toString());
                                                              if (img2 != null) {
                                                                  upload2(img2);
                                                              } else if (img3 != null) {
                                                                  upload3(img3);
                                                              } else {
                                                                  Log.d(TAG, imgs.get(0));
                                                                  posttodb(imgs);
                                                              }
                                                          }
                                                      });
                                          }
                                      }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure", e.getCause());
                        showErrorPopup("Can't upload pictures");
                    }
                });
    }

    public void upload2(ByteArrayOutputStream baos) {
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("bookImages")
                .child(userId + Math.random() * 1000 * Math.random() + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                              Log.d(TAG, "canupload");
                                              reference.getDownloadUrl()
                                                      .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                          public void onSuccess(Uri uri) {
                                                              imgs.add(uri.toString());
                                                              if (img3 != null) {
                                                                  upload3(img3);
                                                              } else {
                                                                  posttodb(imgs);
                                                              }
                                                          }
                                                      });
                                          }
                                      }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure", e.getCause());
                        showErrorPopup("Can't upload pictures");
                    }
                });
    }

    public void upload3(ByteArrayOutputStream baos) {
        final StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("bookImages")
                .child(userId + Math.random() * 1000 * Math.random() + ".jpeg");

        reference.putBytes(baos.toByteArray())
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                              Log.d(TAG, "canupload");
                                              reference.getDownloadUrl()
                                                      .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                          public void onSuccess(Uri uri) {
                                                              Log.d(TAG, "get uri");
                                                              imgs.add(uri.toString());
                                                              posttodb(imgs);
                                                          }
                                                      });
                                          }
                                      }
                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure", e.getCause());
                        showErrorPopup("Can't upload pictures");
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

    public void onClick(View view) {
        if (view == backBtn || view == successCross) {
            backToMainPage();
        }
        if (view == submitBtn) {
            updatepost();
        }
        if (view == add1) {
            currentUploadLoc = 1;
            takeBookPic();
        }

        if (view == add2) {
            currentUploadLoc = 2;
            takeBookPic();
        }

        if (view == add3) {
            currentUploadLoc = 3;
            takeBookPic();
        }

        if (view == errorCross) {
            errorDialog.dismiss();
        }

        if(view == deleteBtn){
            deleteListing();
        }
    }

    private void deleteListing(){
        db.collection("Posts").document(listingid)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(getApplicationContext(), "You have deleted a post! ", Toast.LENGTH_SHORT);
                        showSuccessPopup("You have deleted a post!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }


    private void updatepost() {
        progessDialog.setMessage("Verifying inputs...");
        progessDialog.show();
        if(title.getText() == null || isbn.getText() == null || condition.getText() == null || mass.getText() == null || price.getText() == null || location.getText() == null || schedule.getText() == null || author.getText() == null){
            progessDialog.dismiss();
            showErrorPopup("Please enter valid details.");
            return;
        }
        if (img1 != null) {
            upload1(img1);
        } else if (img2 != null) {
            upload2(img2);
        } else if (img3 != null) {
            upload3(img3);
        } else {
            progessDialog.dismiss();
            showErrorPopup("Please upload at least 1 image.");
            return;
        }
    }


    private void posttodb(List<String> uris) {

        Log.d(TAG, "CAN START UPDATE");
        Double massFinal = 0.0;
        Double priceFinal = 0.0;
        String schoolInput;

        //Get and validate radio button, (School input)
        int selectedId = radioGroup1.getCheckedRadioButtonId();
        if (selectedId != -1) {
            radioSelected = (RadioButton) findViewById(radioGroup1.getCheckedRadioButtonId());
            schoolInput = radioSelected.getText().toString();
        } else {
            selectedId = radioGroup2.getCheckedRadioButtonId();
            if (selectedId != -1) {
                radioSelected = (RadioButton) findViewById(radioGroup2.getCheckedRadioButtonId());
                schoolInput = radioSelected.getText().toString();
            } else {
                progessDialog.dismiss();
                showErrorPopup("Please select the school :).");
                return;
            }
        }


        //Get checkbox
        payments.clear();
        if (chkcash.isChecked()) {
            payments.add(chkcash.getText().toString());
        }
        if (chkpaylah.isChecked()) {
            payments.add(chkpaylah.getText().toString());
        }
        if (chkpaynow.isChecked()) {
            payments.add(chkpaynow.getText().toString());
        }
        if (payments.size() == 0) {
            progessDialog.dismiss();
            showErrorPopup("Please check at least one payment method.");
            return;
        }


        String titleInput = title.getText().toString().trim();
        String conditionInput = condition.getText().toString().trim();
        String massInput = mass.getText().toString().trim();
        String priceInput = price.getText().toString().trim();
        String locationInput = location.getText().toString().trim();
        String scheduleInput = schedule.getText().toString().trim();
        String isbnInput = isbn.getText().toString().trim();
        String authorInput = author.getText().toString().trim();


        if (TextUtils.isEmpty(titleInput) || TextUtils.isEmpty(conditionInput) || TextUtils.isEmpty(scheduleInput) || TextUtils.isEmpty(locationInput) || TextUtils.isEmpty(massInput) || TextUtils.isEmpty(priceInput) || TextUtils.isEmpty(isbnInput) || TextUtils.isEmpty(authorInput)) {
            progessDialog.dismiss();
            showErrorPopup("Please don't leave any field blank.");
            return;
        }

        try {
            massFinal = Double.parseDouble(massInput);
            priceFinal = Double.parseDouble(priceInput);
        } catch (Exception ex) {
            progessDialog.dismiss();
            showErrorPopup("Please provide valid Mass / Price input.");
            return;
        }



        Date currentTime = Calendar.getInstance().getTime();
        String current = currentTime.toString();


        progessDialog.dismiss();
        progessDialog.setMessage("Posts in progress...");
        progessDialog.show();
        //start input into database
        Post newPost = new Post(titleInput, authorInput, isbnInput, conditionInput, massFinal, priceFinal, locationInput, scheduleInput, schoolInput, payments, userId, uris, false, current);


        db.collection("Posts").document(listingid).set(newPost).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progessDialog.dismiss();
                Toast.makeText(getApplicationContext(), "You have made a post! ", Toast.LENGTH_SHORT);
                Log.d(TAG, "Updated a post successfully!");
                showSuccessPopup("You have updated a post!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error happened in updating", e);
                        progessDialog.dismiss();
                        showErrorPopup("Update failed! Please Try again.");
                    }
                });
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == radioGroup1) {

            int selectedId = radioGroup2.getCheckedRadioButtonId();
            if (selectedId != -1) {
                radioGroup2.setOnCheckedChangeListener(null);
                radioGroup2.clearCheck();
                radioGroup2.setOnCheckedChangeListener(this);
            }
        } else if (group == radioGroup2) {
            int selectedId = radioGroup1.getCheckedRadioButtonId();
            if (selectedId != -1) {
                radioGroup1.setOnCheckedChangeListener(null);
                radioGroup1.clearCheck();
                radioGroup1.setOnCheckedChangeListener(this);
            }
        }
    }
}
