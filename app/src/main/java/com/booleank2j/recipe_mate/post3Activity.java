package com.booleank2j.recipe_mate;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewManager;
import android.webkit.MimeTypeMap;
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
import com.booleank2j.recipe_mate.model.Food;
import com.booleank2j.recipe_mate.model.UserPost;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class post3Activity extends AppCompatActivity {

    ImageView addStep;
    CircularProgressButton submitButton;
    LinearLayout RecipeId;
    ScrollView scrollview2;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Image");

    String name, likes, dislikes, profileurl, email;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post3);

        //Getting Data(Food, email) From lastActivity
        Food food= (Food) getIntent().getSerializableExtra("foodClass");
        email = getIntent().getStringExtra("email");
        ArrayList<String> inFirst=getIntent().getStringArrayListExtra("inFirst");
        ArrayList<String> inSecond=getIntent().getStringArrayListExtra("inSecond");
        Vector<Pair<String, String>> v1=new Vector<>();
        for(int i=0; i<inFirst.size(); i++) {
            v1.addElement(new Pair<>(inFirst.get(i), inSecond.get(i)));
        }
        food.setIngredientsList(v1);

        //Initializing layout content
        addStep=findViewById(R.id.addStep);
        submitButton=findViewById(R.id.submitButton);
        RecipeId=findViewById(R.id.RecipeId);
        scrollview2=findViewById(R.id.scrollview2);

        //Adding to layout
        addStep.setOnClickListener(view -> {
            int count=RecipeId.getChildCount();
            @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.add_recipe, null, false);
            TextView textView1=view1.findViewById(R.id.stepCount);
            textView1.setText("Step : "+ (count+1));
            ImageView delete = view1.findViewById(R.id.delete);
            delete.setOnClickListener(view22 -> {
                ((ViewManager)view1.getParent()).removeView(view1);
                int x=RecipeId.getChildCount();
                for(int i=0; i<x; i++) {
                    View view2=RecipeId.getChildAt(i);
                    TextView t=view2.findViewById(R.id.stepCount);
                    t.setText("Step : "+ (i+1));
                }
            });
            scrollview2.post(() -> scrollview2.fullScroll(ScrollView.FOCUS_DOWN));
            RecipeId.addView(view1);
        });

        Vector<String> ingredient=new Vector<>();
        ingredient.addElement("Select an item");
        final String[] userNode = {""};


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.child("Ingredient").getChildren())
                    ingredient.addElement(Objects.requireNonNull(snapshot1.getValue()).toString());
                for (DataSnapshot snapshot1:snapshot.child("User").getChildren()) {
                    if(snapshot1.child("email").getValue().toString().equals(email)) {
                        userNode[0] = snapshot1.getKey();
                        likes=snapshot1.child("likes").getValue().toString();
                        dislikes = snapshot1.child("dislikes").getValue().toString();
                        name = snapshot1.child("name").getValue().toString();
                        profileurl = snapshot1.child("profileImage").getValue().toString();
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast toast = Toast.makeText(post3Activity.this, error.toString(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        submitButton.setOnClickListener(view -> {
            submitButton.startAnimation();
            Vector<Pair<String, String>> in=new Vector<>();
            for(int i=0; i<RecipeId.getChildCount(); i++) {
                View view1=RecipeId.getChildAt(i);
                EditText e=view1.findViewById(R.id.recipe);
                in.addElement(new Pair<>("Step "+(i+1), e.getText().toString()));
            }
            food.setHowToMake(in);

            for(int i=0; i<inFirst.size(); i++) {
                if(!ingredient.contains(inFirst.get(i))) {
                    databaseReference.child("Ingredient").push().setValue(inFirst.get(i));
                }
            }

            String imageName=food.getFoodId()+"."+getFileExtension(Uri.parse(food.getImageUrl()));
            StorageReference fileReference = storageReference.child(imageName);
            fileReference.putFile(Uri.parse(food.imageUrl)).addOnSuccessListener(taskSnapshot -> {
                food.setImageUrl(imageName);
                databaseReference.child("FoodCategory").child("0").child(food.category).push().setValue(food);
                UserPost post = new UserPost();
                post.setDislikes("0");
                post.setLikes("0");
                post.setFoodId(food.getFoodId());
                databaseReference.child("User").child(userNode[0]).child("post").push().setValue(post);
                Toast toast = Toast.makeText(post3Activity.this, "Uploaded", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                submitButton.revertAnimation();
                Intent intent = new Intent(post3Activity.this, feedActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("emailID", email);
                intent.putExtra("profileurl", profileurl);
                intent.putExtra("likes", likes);
                intent.putExtra("dislikes", dislikes);
                startActivity(intent);
                finish();
            }).addOnFailureListener(e -> {
                Toast toast = Toast.makeText(post3Activity.this, e.toString(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }).addOnProgressListener(snapshot -> {

            });
        });

    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
