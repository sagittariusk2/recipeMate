package com.booleank2j.recipe_mate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class post1Activity extends AppCompatActivity {

    EditText foodName;
    Spinner categorySpinner;
    ImageView uploadPic;
    TextView next;
    CheckBox checkVeg, checkNVeg;

    private static final int IMAGE_PICK_CODE=1000;
    Uri i;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post1);

        //email of the user
        String email=getIntent().getStringExtra("email");

        //Initialising Layout Content
        foodName=findViewById(R.id.foodName);
        categorySpinner=findViewById(R.id.categorySpinner);
        uploadPic=findViewById(R.id.uploadPic);
        next=findViewById(R.id.next);
        checkNVeg=findViewById(R.id.checkNVeg);
        checkVeg=findViewById(R.id.checkVeg);

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
                Toast toast = Toast.makeText(post1Activity.this, error.toString(), Toast.LENGTH_SHORT);
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
        categorySpinner.setPrompt("Select an Item");
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerArrayAdapter);

        //Picture Select
        uploadPic.setOnClickListener(view -> openFileChooser());

        //Moving Next Page
        next.setOnClickListener(view -> {
            String name=foodName.getText().toString();
            String category=categorySpinner.getSelectedItem().toString();
            String veg="true";
            if(checkVeg.isChecked() && checkNVeg.isChecked()) {
                Toast toast=Toast.makeText(post1Activity.this, "You can Select only one out of veg and non-veg", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            else if(!checkVeg.isChecked() && !checkNVeg.isChecked()) {
                Toast toast=Toast.makeText(post1Activity.this, "Please select veg/Non-veg", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            else if(name.isEmpty()) {
                Toast toast=Toast.makeText(post1Activity.this, "Please Enter Food Name", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            else if(category.equals("Select an Item")) {
                Toast toast=Toast.makeText(post1Activity.this, "Please Select Category", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            else if(i==null) {
                Toast toast=Toast.makeText(post1Activity.this, "Please Upload Image", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
            else {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                Food food=new Food(formatter.format(new Date()));
                food.setFoodName(name);
                food.setImageUrl(i.toString());
                food.setCategory(categorySpinner.getSelectedItem().toString());
                if(checkNVeg.isChecked()) veg="false";
                if(checkVeg.isChecked()) veg="true";
                food.setVeg(veg);
                Intent intent=new Intent(post1Activity.this, post2Activity.class);
                intent.putExtra("email", email);
                intent.putExtra("foodClass", food);
                startActivity(intent);
            }
        });
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
            uploadPic.setImageURI(i);
        }
    }
}
