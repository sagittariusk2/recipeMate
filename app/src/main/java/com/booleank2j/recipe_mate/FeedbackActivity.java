package com.booleank2j.recipe_mate;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

public class FeedbackActivity extends AppCompatActivity {
    private EditText feedbackTxt,email;
    private DatabaseReference ref;
    private int starNo=0;
    private String sno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.feedback_rate);
        feedbackTxt=findViewById(R.id.feedbackTxt);
        email=findViewById(R.id.email);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        Button submit = findViewById(R.id.feedbackBtn);

        ref= FirebaseDatabase.getInstance().getReference().child("Feedback");

        ratingBar.setOnRatingBarChangeListener((ratingBar1, rating, fromUser) -> {
            starNo= (int) ratingBar1.getRating();
            sno=String.valueOf(starNo);
        });

        submit.setOnClickListener(v -> submitFeedback());


    }

    private void submitFeedback(){
        String mail=email.getText().toString();
        String fbTxt=feedbackTxt.getText().toString();
        feedback fb=new feedback(mail,fbTxt,sno);
        String uid=ref.push().getKey();
        assert uid != null;
        ref.child(uid).setValue(fb);
        Toast.makeText(FeedbackActivity.this,"Thank you for your feedback!",Toast.LENGTH_SHORT).show();
    }
}