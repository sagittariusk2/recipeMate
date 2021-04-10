package com.booleank2j.recipe_mate;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;

public class feedActivity  extends AppCompatActivity {
    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private DatabaseReference ref;
    public static boolean LogIn=true;
    private String name,email,likes,dislikes,profileurl;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView linkContainer;

    LinearLayout feedLayout;
    ImageView addPost, searchRecipe;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.feed_layout);
        setTitle("RecipeMate");
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

        drawerLayout = findViewById(R.id.drawer_layout);
        ref= FirebaseDatabase.getInstance().getReference();
        toggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.navlayout);
        linkContainer = findViewById(R.id.linkContainer);
        View headerView = navigationView.getHeaderView(0);
        CircleImageView circleImageView = headerView.findViewById(R.id.cardView);

        databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()) {
                    if(snapshot1.child("email").getValue().toString().equals(email)) {
                        profileurl=snapshot1.child("profileImage").getValue().toString();
                        int l=0, d=0;
                        for(DataSnapshot snapshot2:snapshot1.child("post").getChildren()) {
                            l += Integer.parseInt(snapshot2.child("likes").getValue().toString());
                            d += Integer.parseInt(snapshot2.child("dislikes").getValue().toString());
                        }
                        likes= String.valueOf(l);
                        dislikes= String.valueOf(d);
                        databaseReference.child("User").child(snapshot1.getKey()).child("likes").setValue(String.valueOf(l));
                        databaseReference.child("User").child(snapshot1.getKey()).child("dislikes").setValue(String.valueOf(d));
                        break;
                    }
                }
                final Bitmap[] bitmap = new Bitmap[1];

                storageReference = FirebaseStorage.getInstance().
                        getReferenceFromUrl("gs://recipe-mate-fc02b.appspot.com/").
                        child("profileImage/"+profileurl);
                try {
                    final File localFile = File.createTempFile("image", "jpg");
                    storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                        bitmap[0] = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        circleImageView.setImageBitmap(bitmap[0]);
                    }).addOnFailureListener(e -> {
                        Toast toast = Toast.makeText(feedActivity.this, e.getMessage(), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String link = Objects.requireNonNull(snapshot.child("ShareLink").child("link").getValue()).toString();
                linkContainer.setText(link);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        navigationView.setNavigationItemSelectedListener(item -> {
            int id=item.getItemId();
            if(id==R.id.profile){

                Intent in= new Intent(feedActivity.this,profileActivity.class);
                in.putExtra("emailID", email);
                in.putExtra("name", name);
                in.putExtra("likes", likes);
                in.putExtra("dislikes", dislikes);
                in.putExtra("profileurl", profileurl);
                startActivity(in);
            }
            if(id==R.id.feedback) {
                Intent in =new Intent(feedActivity.this,FeedbackActivity.class);
                startActivity(in);
            }
            if(id==R.id.contactUs) {
                Intent in =new Intent(feedActivity.this,contactActivity.class);
                startActivity(in);
            }
            if(id == R.id.share) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                String message = linkContainer.getText().toString();
                shareIntent.putExtra(Intent.EXTRA_TEXT, message);
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            }
            if(id==R.id.logout){
                LogIn=false;
                editor.clear();
                editor.apply();
                FirebaseAuth.getInstance().signOut();
                ProgressDialog progressDialog = new ProgressDialog(feedActivity.this);
                progressDialog.setTitle("Logging Out....");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Intent in =new Intent(feedActivity.this,MainActivity.class);
                        finish();
                        startActivity(in);
                    }
                }, 2000);



            return false;

        });

        ProgressDialog progressDialog = new ProgressDialog(feedActivity.this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 2000);


        /*Feed Activity showing Code
        ..
        ..
        ..
         */
        //Feed Code......
        feedLayout = findViewById(R.id.feedLayout);
        addPost = findViewById(R.id.addPost);
        searchRecipe = findViewById(R.id.searchRecipe);
        databaseReference.child("FoodCategory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Vector<Pair<String, View>> views = new Vector<>();

                for(DataSnapshot snapshot1:snapshot.getChildren()) {
                    for(DataSnapshot snapshot2:snapshot1.getChildren()) {
                        for(DataSnapshot snapshot3:snapshot2.getChildren()) {
                            @SuppressLint("InflateParams") View view=getLayoutInflater().inflate(R.layout.feed_container, null, false);
                            ImageView imageView2=view.findViewById(R.id.imageView2);
                            ImageView likeIm=view.findViewById(R.id.likeIm);
                            ImageView dislikeIm=view.findViewById(R.id.dislikeIm);
                            TextView foodName=view.findViewById(R.id.foodName);
                            foodName.setText(snapshot3.child("foodName").getValue().toString());
                            CircleImageView vegView=view.findViewById(R.id.vegView);
                            if(snapshot3.child("veg").getValue().toString().equals("false"))
                                vegView.setBackgroundColor(Color.RED);
                            else
                                vegView.setBackgroundColor(Color.GREEN);

                            final Bitmap[] bitmap = new Bitmap[1];

                            storageReference = FirebaseStorage.getInstance().
                                    getReferenceFromUrl("gs://recipe-mate-fc02b.appspot.com/").
                                    child("Image/"+snapshot3.child("imageUrl").getValue());
                            try {
                                final File localFile = File.createTempFile("image", "jpg");
                                storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                                    bitmap[0] = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                    imageView2.setImageBitmap(bitmap[0]);
                                }).addOnFailureListener(e -> Toast.makeText(feedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            imageView2.setOnClickListener(view1 -> {
                                if(bitmap[0]==null) {
                                    Toast toast = Toast.makeText(feedActivity.this, "Loading...", Toast.LENGTH_SHORT);
                                    toast.setGravity(Gravity.CENTER, 0, 0);
                                    toast.show();
                                }
                                else {
                                    Intent intent = new Intent(feedActivity.this, IngredientsPageActivity.class);
                                    intent.putExtra("foodId", snapshot3.child("foodId").getValue().toString());
                                    intent.putExtra("imageName", snapshot3.child("imageUrl").getValue().toString());
                                    startActivity(intent);
                                }
                            });


                            String foodId=snapshot3.child("foodId").getValue().toString();
                            final String[] profileImage = new String[1];
                            databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snap) {
                                    int flag=0;
                                    for(DataSnapshot snapshot4:snap.getChildren()) {
                                        String like = "0", dislike = "0";
                                        for (DataSnapshot snapshot5 : snapshot4.child("post").getChildren()) {
                                            if (snapshot5.child("foodId").getValue().toString().equals(foodId)) {
                                                flag++;
                                                dislike = snapshot5.child("dislikes").getValue().toString();
                                                like = snapshot5.child("likes").getValue().toString();
                                                break;
                                            }
                                        }
                                        for (DataSnapshot snapshot5 : snapshot4.child("liked").getChildren()) {
                                            if(snapshot5.getValue().toString().equals(foodId)) {
                                                likeIm.setImageResource(R.drawable.like);
                                                break;
                                            }
                                        }
                                        for (DataSnapshot snapshot5 : snapshot4.child("disliked").getChildren()) {
                                            if(snapshot5.getValue().toString().equals(foodId)) {
                                                dislikeIm.setImageResource(R.drawable.dislike);
                                                break;
                                            }
                                        }
                                        if (flag == 1) {
                                            profileImage[0] = snapshot4.child("profileImage").getValue().toString();
                                            CircleImageView cardView = view.findViewById(R.id.cardView);
                                            final Bitmap[] bitmap1 = new Bitmap[1];
                                            storageReference = FirebaseStorage.getInstance().
                                                    getReferenceFromUrl("gs://recipe-mate-fc02b.appspot.com/").
                                                    child("profileImage/" + profileImage[0]);
                                            try {
                                                final File localFile = File.createTempFile("image", "jpg");
                                                storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                                                    bitmap1[0] = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                                    cardView.setImageBitmap(bitmap1[0]);
                                                }).addOnFailureListener(e -> Toast.makeText(feedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            TextView likeId = view.findViewById(R.id.likeId);
                                            TextView dislikeId = view.findViewById(R.id.dislikeId);
                                            likeId.setText(like);
                                            dislikeId.setText(dislike);
                                            break;
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            likeIm.setOnClickListener(view1 -> {
                                databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snap) {
                                        for(DataSnapshot snapshot4:snap.getChildren()) {
                                            if (snapshot4.child("email").getValue().toString().equals(email)) {
                                                int flag=0;
                                                for(DataSnapshot snapshot5:snapshot4.child("liked").getChildren()) {
                                                    if(snapshot5.getValue().toString().equals(foodId)) {
                                                        flag++;
                                                        break;
                                                    }
                                                }
                                                if(flag==0) {
                                                    TextView likeId = view.findViewById(R.id.likeId);
                                                    likeId.setText(String.valueOf(Integer.parseInt(likeId.getText().toString())+1));
                                                    likeIm.setImageResource(R.drawable.like);
                                                    databaseReference.child("User").child(snapshot4.getKey()).child("liked").push().setValue(foodId);
                                                    databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for(DataSnapshot snapshot5:snapshot.getChildren()) {
                                                                for(DataSnapshot snapshot6:snapshot5.child("post").getChildren()) {
                                                                    if(snapshot6.child("foodId").getValue().toString().equals(foodId)) {
                                                                        int x=Integer.parseInt(snapshot6.child("likes").getValue().toString());
                                                                        x++;
                                                                        databaseReference.child("User").child(snapshot5.getKey()).child("post").child(snapshot6.getKey()).child("likes").setValue(String.valueOf(x));
                                                                        Toast toast = Toast.makeText(feedActivity.this, "Added to like", Toast.LENGTH_LONG);
                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                        toast.show();
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                                break;
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            });

                            dislikeIm.setOnClickListener(view1 -> {
                                databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snap) {
                                        for(DataSnapshot snapshot4:snap.getChildren()) {
                                            if (snapshot4.child("email").getValue().toString().equals(email)) {
                                                int flag=0;
                                                for(DataSnapshot snapshot5:snapshot4.child("disliked").getChildren()) {
                                                    if(snapshot5.getValue().toString().equals(foodId)) {
                                                        flag++;
                                                        break;
                                                    }
                                                }
                                                if(flag==0) {
                                                    TextView dislikeId = view.findViewById(R.id.dislikeId);
                                                    dislikeId.setText(String.valueOf(Integer.parseInt(dislikeId.getText().toString())+1));
                                                    dislikeIm.setImageResource(R.drawable.dislike);
                                                    databaseReference.child("User").child(snapshot4.getKey()).child("disliked").push().setValue(foodId);
                                                    databaseReference.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for(DataSnapshot snapshot5:snapshot.getChildren()) {
                                                                for(DataSnapshot snapshot6:snapshot5.child("post").getChildren()) {
                                                                    if(snapshot6.child("foodId").getValue().toString().equals(foodId)) {
                                                                        int x=Integer.parseInt(snapshot6.child("dislikes").getValue().toString());
                                                                        x++;
                                                                        databaseReference.child("User").child(snapshot5.getKey()).child("post").child(snapshot6.getKey()).child("dislikes").setValue(String.valueOf(x));
                                                                        Toast toast = Toast.makeText(feedActivity.this, "Added to dislike", Toast.LENGTH_LONG);
                                                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                                                        toast.show();
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            });

                            views.add(new Pair(snapshot3.child("foodId").getValue().toString(), view));
                        }
                    }
                }

                views.sort((o1, o2) -> o1.first.compareTo(o2.first));
                for(int i=views.size()-1; i>=0; i--) {
                    feedLayout.addView(views.elementAt(i).second);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast toast = Toast.makeText(feedActivity.this, error.toString(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        addPost.setOnClickListener(view -> {
            Intent intent1 = new Intent(feedActivity.this, post1Activity.class);
            intent1.putExtra("email", email);
            startActivity(intent1);
        });

        searchRecipe.setOnClickListener(view -> {
            Intent intent1=new Intent(feedActivity.this, RecipeSearchActivity.class);
            intent1.putExtra("email", email);
            startActivity(intent1);
        });



    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
