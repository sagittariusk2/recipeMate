package com.booleank2j.recipe_mate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecipeSearchList extends AppCompatActivity {

    LinearLayout feedLayout;
    ImageView addPost, searchRecipe;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_layout);

        String email = getIntent().getStringExtra("email");
        ArrayList<String> arrayList=getIntent().getStringArrayListExtra("arrayList");
        String category = getIntent().getStringExtra("category");
        if(category.equals("Select an Item")) category="";

        feedLayout = findViewById(R.id.feedLayout);
        addPost = findViewById(R.id.addPost);
        searchRecipe = findViewById(R.id.searchRecipe);

        //Feed Code...
        databaseReference.child("FoodCategory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Vector<Pair<String, View>> views = new Vector<>();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                        for (DataSnapshot snapshot3 : snapshot2.getChildren()) {
                            int flag = 0;
                            for (DataSnapshot snapshot4 : snapshot3.child("ingredientsList").getChildren()) {
                                String ingredient = snapshot4.child("first").getValue().toString();
                                if (arrayList.contains(ingredient)) {
                                    flag++;
                                    break;
                                }
                            }
                            if (flag == 1) {
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
                                    }).addOnFailureListener(e -> Toast.makeText(RecipeSearchList.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                imageView2.setOnClickListener(view1 -> {
                                    if(bitmap[0]==null) {
                                        Toast toast = Toast.makeText(RecipeSearchList.this, "Loading...", Toast.LENGTH_SHORT);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                    }
                                    else {
                                        Intent intent = new Intent(RecipeSearchList.this, IngredientsPageActivity.class);
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
                                                    }).addOnFailureListener(e -> Toast.makeText(RecipeSearchList.this, e.getMessage(), Toast.LENGTH_SHORT).show());
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
                                                                            Toast toast = Toast.makeText(RecipeSearchList.this, "Added to like", Toast.LENGTH_LONG);
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
                                                                            Toast toast = Toast.makeText(RecipeSearchList.this, "Added to dislike", Toast.LENGTH_LONG);
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

                }
                views.sort((o1, o2) -> o1.first.compareTo(o2.first));
                for (int i = views.size() - 1; i >= 0; i--) {
                    feedLayout.addView(views.elementAt(i).second);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addPost.setOnClickListener(view -> {
            Intent intent = new Intent(RecipeSearchList.this, post1Activity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

        searchRecipe.setOnClickListener(view -> {
            Intent intent=new Intent(RecipeSearchList.this, RecipeSearchActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        });

    }
}
