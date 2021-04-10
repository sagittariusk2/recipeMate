package com.booleank2j.recipe_mate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class forgetPasswordActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextView resetStatus,signInTv;
    private EditText emailET;
    private ImageView back;
    private Button resetbtn;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.forget_password);
        resetbtn=findViewById(R.id.resetBtn);
        resetStatus=findViewById(R.id.resetStat);
        emailET=findViewById(R.id.mailbox);
        back=findViewById(R.id.backicon);
        signInTv=findViewById(R.id.signInTv);
        firebaseAuth = FirebaseAuth.getInstance();
        resetbtn.setOnClickListener(view -> firebaseAuth.sendPasswordResetEmail(emailET.getText().toString()).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String email=emailET.getText().toString();
                if(isValidEmail(email)) {
                    resetStatus.setText("Reset password mail has been sent to your mail ID");
                    signInTv.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(forgetPasswordActivity.this,"Please enter valid email ID", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                resetStatus.setText("Oops! . Couldn't send the mail or You are not registered. Please try again");
            }
        }));
        signInTv.setOnClickListener(view -> {
            Intent in=new Intent(forgetPasswordActivity.this,MainActivity.class);
            forgetPasswordActivity.this.finish();
            startActivity(in);
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(forgetPasswordActivity.this,SignInActivity.class);
                forgetPasswordActivity.this.finish();
                startActivity(in);
            }
        });

    }
    private boolean isValidEmail(CharSequence t){
        return(!TextUtils.isEmpty(t) && Patterns.EMAIL_ADDRESS.matcher(t).matches());
    }

}
