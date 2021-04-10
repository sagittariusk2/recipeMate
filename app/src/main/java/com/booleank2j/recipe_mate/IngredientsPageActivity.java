package com.booleank2j.recipe_mate;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

import java.io.File;
import java.io.IOException;

public class IngredientsPageActivity extends AppCompatActivity {

    Button ingredientBtn, recipeBtn;
    ImageView imageId;
    LinearLayout ingredientShow, recipeShow;
    ScrollView scrL;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ingredients_page);

        String imageName = getIntent().getStringExtra("imageName");
        String foodId = getIntent().getStringExtra("foodId");

        ingredientBtn=findViewById(R.id.ingredientBtn);
        recipeBtn=findViewById(R.id.recipeBtn);
        imageId = findViewById(R.id.imageId);
        ingredientShow=findViewById(R.id.ingredientShow);
        recipeShow=findViewById(R.id.recipeShow);
        scrL = findViewById(R.id.scrL);

        final Bitmap[] bitmap = new Bitmap[1];

        storageReference = FirebaseStorage.getInstance().
                getReferenceFromUrl("gs://recipe-mate-fc02b.appspot.com/").
                child("Image/"+imageName);
        try {
            final File localFile = File.createTempFile("image", "jpg");
            storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                bitmap[0] = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                imageId.setImageBitmap(bitmap[0]);
            }).addOnFailureListener(e -> Toast.makeText(IngredientsPageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            e.printStackTrace();
        }

        databaseReference.child("FoodCategory").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.getChildren()) {
                    for (DataSnapshot snapshot2 : snapshot1.getChildren()) {
                        for (DataSnapshot snapshot3 : snapshot2.getChildren()) {
                            if(snapshot3.child("foodId").getValue().toString().equals(foodId)) {
                                for(DataSnapshot snapshot4:snapshot3.child("howToMake").getChildren()) {
                                    @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.add_recipe, null, false);
                                    TextView textView1=view1.findViewById(R.id.stepCount);
                                    textView1.setText(snapshot4.child("first").getValue().toString());
                                    ImageView delete = view1.findViewById(R.id.delete);
                                    delete.setVisibility(View.INVISIBLE);
                                    EditText recipe = view1.findViewById(R.id.recipe);
                                    recipe.setKeyListener(null);
                                    recipe.setText(snapshot4.child("second").getValue().toString());
                                    recipeShow.addView(view1);
                                }
                                for(DataSnapshot snapshot4:snapshot3.child("ingredientsList").getChildren()) {
                                    @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.add_ingredient, null, false);
                                    ImageView delete = view1.findViewById(R.id.delete);
                                    delete.setVisibility(View.INVISIBLE);
                                    TextView ingredientText = view1.findViewById(R.id.ingredientHint);
                                    ingredientText.setKeyListener(null);
                                    ingredientText.setText(snapshot4.child("first").getValue().toString());
                                    EditText amount=view1.findViewById(R.id.amount);
                                    amount.setKeyListener(null);
                                    amount.setText(snapshot4.child("second").getValue().toString());
                                    ingredientShow.addView(view1);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        recipeBtn.setBackground(null);
        recipeShow.setVisibility(View.INVISIBLE);
        recipeBtn.setOnClickListener(view -> {
            recipeBtn.setBackgroundResource(R.drawable.button);
            ingredientBtn.setBackground(null);
            recipeShow.setVisibility(View.VISIBLE);
            ingredientShow.setVisibility(View.INVISIBLE);
            scrL.post(() -> scrL.fullScroll(ScrollView.FOCUS_UP));
        });

        ingredientBtn.setOnClickListener(view -> {
            ingredientBtn.setBackgroundResource(R.drawable.button);
            recipeBtn.setBackground(null);
            ingredientShow.setVisibility(View.VISIBLE);
            recipeShow.setVisibility(View.INVISIBLE);
            scrL.post(() -> scrL.fullScroll(ScrollView.FOCUS_UP));
        });
    }
}
