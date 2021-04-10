package com.booleank2j.recipe_mate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.booleank2j.recipe_mate.model.Food;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class post2Activity extends AppCompatActivity {

    ImageView AddIngredientButton;
    LinearLayout IngredientId;
    TextView next2;
    ScrollView scrollView1;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post2);

        //Getting Data(Food, email) From Previous Activity
        Food food = (Food) getIntent().getSerializableExtra("foodClass");
        String email = getIntent().getStringExtra("email");

        //Initialising Layout Content
        AddIngredientButton=findViewById(R.id.AddIngredientButton);
        IngredientId=findViewById(R.id.IngredientId);
        next2=findViewById(R.id.next2);
        scrollView1=findViewById(R.id.scrollView1);

        //Getting list of ingredients
        Vector<String> ingredient=new Vector<>();
        ingredient.addElement("Select An Item");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.child("Ingredient").getChildren())
                    ingredient.addElement(Objects.requireNonNull(snapshot1.getValue()).toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast toast = Toast.makeText(post2Activity.this, error.toString(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        //Adding Layout
        AddIngredientButton.setOnClickListener(view -> {
            @SuppressLint("InflateParams") View view1 = getLayoutInflater().inflate(R.layout.add_ingredient, null, false);
            ImageView delete = view1.findViewById(R.id.delete);
            AutoCompleteTextView ingredientText = view1.findViewById(R.id.ingredientHint);
            ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(post2Activity.this, android.R.layout.simple_list_item_1, ingredient);
            ingredientText.setAdapter(arrayAdapter);
            ingredientText.setThreshold(1);
            delete.setOnClickListener(view2 -> ((ViewManager)view1.getParent()).removeView(view1));
            IngredientId.addView(view1);
            scrollView1.post(() -> scrollView1.fullScroll(ScrollView.FOCUS_DOWN));
        });

        //Moving to next Activity
        next2.setOnClickListener(view -> {
            ArrayList<String> inFirst=new ArrayList<>();
            ArrayList<String> inSecond=new ArrayList<>();
            for(int i=0; i<IngredientId.getChildCount(); i++) {
                View view1=IngredientId.getChildAt(i);
                @SuppressLint("CutPasteId") AutoCompleteTextView a=view1.findViewById(R.id.ingredientHint);
                EditText e=view1.findViewById(R.id.amount);
                inFirst.add(a.getText().toString());
                inSecond.add(e.getText().toString());
            }
            Intent intent=new Intent(post2Activity.this, post3Activity.class);
            intent.putExtra("foodClass", food);
            intent.putExtra("email", email);
            intent.putStringArrayListExtra("inFirst", inFirst);
            intent.putStringArrayListExtra("inSecond", inSecond);
            startActivity(intent);
        });

    }
}
