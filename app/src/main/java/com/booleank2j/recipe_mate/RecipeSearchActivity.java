package com.booleank2j.recipe_mate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Vector;

public class RecipeSearchActivity extends AppCompatActivity {

    AutoCompleteTextView IngredientsSearch;
    Button RecipeAddBtn, RecipeSearchBtn;
    ChipGroup chipGroup;
    Spinner categorySpinner2;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_recipes);

        String email = getIntent().getStringExtra("email");

        RecipeAddBtn=findViewById(R.id.RecipeAddBtn);
        IngredientsSearch=findViewById(R.id.IngredientsSearch);
        chipGroup=findViewById(R.id.chipGroup);
        categorySpinner2=findViewById(R.id.categorySpinner2);
        RecipeSearchBtn=findViewById(R.id.RecipeSearchBtn);

        Vector<String> ingredient=new Vector<>();
        ingredient.addElement("--SELECT--");

        //Chip
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.child("Ingredient").getChildren())
                    ingredient.addElement(Objects.requireNonNull(snapshot1.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast toast = Toast.makeText(RecipeSearchActivity.this, error.toString(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(RecipeSearchActivity.this, android.R.layout.simple_list_item_1, ingredient);
        IngredientsSearch.setAdapter(arrayAdapter);
        IngredientsSearch.setThreshold(1);
        RecipeAddBtn.setOnClickListener(view -> {
            Chip chip=new Chip(this);
            chip.setText(IngredientsSearch.getText().toString());
            chip.setOnCloseIconClickListener(view1 -> ((ViewManager)view1.getParent()).removeView(view1));
            chip.setCloseIconVisible(true);
            chip.setCheckable(false);
            chip.setClickable(true);
            chipGroup.addView(chip);
            chipGroup.setVisibility(View.VISIBLE);
            IngredientsSearch.setText("");
        });

        //Spinner
        ArrayList<String> s = new ArrayList<>();
        s.add("Select an Item");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1:snapshot.child("FoodCategory").child("0").getChildren()) {
                    s.add(snapshot1.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast toast = Toast.makeText(RecipeSearchActivity.this, error.toString(), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, s){
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position==0) {
                    tv.setBackgroundColor(Color.parseColor("#D5D3D3"));
                }
                else if(position%2 == 1) {
                    // Set the item background color
                    tv.setBackgroundColor(Color.parseColor("#FFF9A600"));
                }
                else {
                    // Set the alternate item background color
                    tv.setBackgroundColor(Color.parseColor("#FFE49200"));
                }
                return view;
            }
        };
        categorySpinner2.setPrompt("Select an Item");
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner2.setAdapter(spinnerArrayAdapter);

        //Search
        RecipeSearchBtn.setOnClickListener(view -> {
            String category=categorySpinner2.getSelectedItem().toString();
            ArrayList<String> arrayList=new ArrayList<>();
            int x=chipGroup.getChildCount();
            for(int i=0; i<x; i++) {
                Chip d= (Chip) chipGroup.getChildAt(i);
                arrayList.add(d.getText().toString());
            }
            Intent intent=new Intent(RecipeSearchActivity.this, RecipeSearchList.class);
            intent.putExtra("category", category);
            intent.putExtra("email", email);
            intent.putStringArrayListExtra("arrayList", arrayList);
            startActivity(intent);
        });

    }
}
