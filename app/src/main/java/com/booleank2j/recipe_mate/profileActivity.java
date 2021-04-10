package com.booleank2j.recipe_mate;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.booleank2j.recipe_mate.model.Food;
import com.booleank2j.recipe_mate.model.UserPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.booleank2j.recipe_mate.feedActivity.LogIn;

public class profileActivity extends AppCompatActivity {
    private String name,email,likes,dislikes,profileurl;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private  TextView nameTv,likesTv,dislikesTv,emailTv;
    CircleImageView cardView;
    private static final int IMAGE_PICK_CODE=1000;
    Uri i;
    CircularProgressButton update;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImage");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.profile_layout);
        Bundle extras;
        Intent intent=getIntent();
        extras = intent.getExtras();
        name = extras.getString("name");
        email = extras.getString("emailID");
        likes = extras.getString("likes");
        dislikes = extras.getString("dislikes");
        profileurl = extras.getString("profileurl");
        sharedPreferences = getSharedPreferences("userDB",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        TextView logoutTv=findViewById(R.id.logout);
        nameTv=findViewById(R.id.nameTv);
        likesTv=findViewById(R.id.likesTv);
        dislikesTv=findViewById(R.id.dislikesTv);
        emailTv=findViewById(R.id.mailcontainer);
        cardView=findViewById(R.id.cardView);
        update=findViewById(R.id.update);

        databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()) {
                    if(snapshot1.child("email").getValue().toString().equals(email)) {
                        int l=0, d=0;
                        for(DataSnapshot snapshot2:snapshot1.child("post").getChildren()) {
                            l += Integer.parseInt(snapshot2.child("likes").getValue().toString());
                            d += Integer.parseInt(snapshot2.child("dislikes").getValue().toString());
                        }
                        likes= String.valueOf(l);
                        dislikes= String.valueOf(d);
                        databaseReference.child("User").child(snapshot1.getKey()).child("likes").setValue(String.valueOf(l));
                        databaseReference.child("User").child(snapshot1.getKey()).child("dislikes").setValue(String.valueOf(d));
                        likesTv.setText(likes);
                        dislikesTv.setText(dislikes);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        nameTv.setText(name);
        emailTv.setText(email);
        sharedPreferences = getSharedPreferences("userDB",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        logoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                LogIn=false;
                editor.clear();
                editor.apply();
                ProgressDialog progressDialog = new ProgressDialog(profileActivity.this);
                progressDialog.setTitle("Logging Out....");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Intent in =new Intent(profileActivity.this,MainActivity.class);
                        finish();
                        startActivity(in);
                    }
                }, 2000);

            }
        });

        final Bitmap[] bitmap = new Bitmap[1];

        storageReference = FirebaseStorage.getInstance().
                getReferenceFromUrl("gs://recipe-mate-fc02b.appspot.com/").
                child("profileImage/"+profileurl);
        try {
            final File localFile = File.createTempFile("image", "jpg");
            storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                bitmap[0] = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                cardView.setImageBitmap(bitmap[0]);
            }).addOnFailureListener(e -> {
                Toast toast = Toast.makeText(profileActivity.this, "Please Upload an Image", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        cardView.setOnClickListener(view -> {
            openFileChooser();
        });

        update.setOnClickListener(view -> {
            if(i!=null) {
                update.startAnimation();
                String imageName = email + "." + getFileExtension(i);
                StorageReference fileReference = storageReference;
                fileReference.putFile(i).addOnSuccessListener(taskSnapshot -> {
                    databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                if (snapshot1.child("email").getValue().toString().equals(email)) {
                                    databaseReference.child("User").child(snapshot1.getKey()).child("profileImage").setValue(imageName);
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Toast toast = Toast.makeText(profileActivity.this, "Uploaded", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    update.revertAnimation();
                }).addOnFailureListener(e -> {
                    Toast toast = Toast.makeText(profileActivity.this, e.toString(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }).addOnProgressListener(snapshot -> {

                });
            }
            else {
                Toast toast = Toast.makeText(profileActivity.this, "Please Select A new Photo", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode==IMAGE_PICK_CODE && data != null && data.getData() != null) {
            i=data.getData();
            cardView.setImageURI(i);
        }
    }
}
