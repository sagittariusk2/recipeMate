package com.booleank2j.recipe_mate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    private EditText namebox,email,password1;
    private Button signupbtn;
    private Button loginbtn;
    private ImageView back;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private DatabaseReference ref;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.signup);

       signupbtn=findViewById(R.id.sub);
        namebox=findViewById(R.id.namebox);
        email=findViewById(R.id.mailbox);
        password1=findViewById(R.id.passbox);
        progressDialog=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
       FirebaseDatabase database = FirebaseDatabase.getInstance();
       back=findViewById(R.id.backicon);
     //  sharedPreferences=getSharedPreferences("RegisterDB",MODE_PRIVATE);
      // editor=sharedPreferences.edit();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in= new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(in);
            }
        });
      ref= database.getReference().child("User");
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });





    }
    private void Register(){
       String name=namebox.getText().toString();
       String mail=email.getText().toString();
        String pass1=password1.getText().toString();
       String likes="0";
        String dislikes="0";
        String profileImage="Image";

        if(TextUtils.isEmpty(name)){
            email.setError("Enter an emailId");
            return;
        }
        else if(TextUtils.isEmpty(pass1)){
            password1.setError("Confirm your password");
            return;
        }

        else if(pass1.length()<4){
            password1.setError("Length should be >4");
            return;
        }
        else if(!isValidEmail(mail)){
            email.setError("Invalid email");
        }

        else{
            progressDialog.setMessage("Please wait");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
            firebaseAuth.createUserWithEmailAndPassword(mail, pass1).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Objects.requireNonNull(firebaseAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            user u= new user(name, mail, likes, dislikes,profileImage);
                            String uid = ref.push().getKey();
                            assert uid != null;
                            ref.child(uid).setValue(u);

                            Toast.makeText(SignUpActivity.this, "Verification email sent.Please verify your email ID", Toast.LENGTH_SHORT).show();
                            email.setText("");
                            password1.setText("");
                            namebox.setText("");
                        } else {
                            Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(SignUpActivity.this, "Registration Failed. Email ID has already been used", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            });
        }


        }


   private boolean isValidEmail(CharSequence t){
        return(!TextUtils.isEmpty(t) && Patterns.EMAIL_ADDRESS.matcher(t).matches());
    }

}
