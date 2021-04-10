package com.booleank2j.recipe_mate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.booleank2j.recipe_mate.feedActivity.LogIn;

public class SignInActivity extends AppCompatActivity {
    private EditText emailEt,passwordEt;
    private Button loginbtn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private int flag = 0;
    private ImageView back;
    private TextView forgetpass;


    private DatabaseReference ref;
    SharedPreferences sharedPreferences;
   SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.signin);
        loginbtn=findViewById(R.id.Loginbtn);
        forgetpass=findViewById(R.id.forgetpass);
        back=findViewById(R.id.backicon);

        emailEt=findViewById(R.id.mailbox);
        passwordEt=findViewById(R.id.passbox);
        progressDialog=new ProgressDialog(this);
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ref= database.getReference().child("User");
        sharedPreferences = getSharedPreferences("userDB", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (!LogIn) {
            editor.putBoolean("LoggedIn", false);
            editor.apply();
        }
        if (sharedPreferences.getBoolean("LoggedIn", false)) {
            Intent in = new Intent(SignInActivity.this,feedActivity.class);
            in.putExtra("emailID", sharedPreferences.getString("Email", ""));
            in.putExtra("name", sharedPreferences.getString("name", ""));
            in.putExtra("likes", sharedPreferences.getString("likes", ""));
            in.putExtra("dislikes", sharedPreferences.getString("dislikes", ""));
            in.putExtra("profileurl", sharedPreferences.getString("profileurl", ""));
            in.putExtra("LoggedIn", sharedPreferences.getBoolean("LoggedIn", false));
            this.finish();
            startActivity(in);
        }


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Login();
            }
        });
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(SignInActivity.this,forgetPasswordActivity.class);
                finish();
                startActivity(in);

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in=new Intent(SignInActivity.this,MainActivity.class);
               SignInActivity.this.finish();
                startActivity(in);
            }
        });

    }
    private void Login(){
        String email=emailEt.getText().toString();
        String password=passwordEt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailEt.setError("Enter an email Id");
            return;
        }
      else  if (TextUtils.isEmpty(password)) {
            passwordEt.setError("Enter the password");
            return;
        }
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this,task -> {
            if (task.isSuccessful()) {
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            user user = snapshot1.getValue(user.class);
                            assert user != null;
                            if (email.equals((user.getEmail()))) {
                                flag = 1;
                                if (Objects.requireNonNull(firebaseAuth.getCurrentUser()).isEmailVerified()) {
                                    Toast.makeText(SignInActivity.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                                    editor.putString("Email", email);
                                    editor.putString("name", user.getName());
                                    editor.putString("likes", user.getLikes());
                                    editor.putString("dislikes", user.getDislikes());
                                    editor.putString("profileurl", user.getProfileImage());
                                    editor.putBoolean("LoggedIn", true);
                                    editor.commit();
                                    Intent in = new Intent(SignInActivity.this, feedActivity.class);
                                    in.putExtra("emailID", email);
                                    in.putExtra("name", user.getName());
                                    in.putExtra("likes", user.getLikes());
                                    in.putExtra("dislikes", user.getDislikes());
                                    in.putExtra("profileurl", user.getProfileImage());

                                      finish();
                                    startActivity(in);
                                } else {
                                    snapshot1.getRef().removeValue();
                                    firebaseAuth.getCurrentUser().delete();
                                    Toast.makeText(SignInActivity.this, "You didn't verify the Email ID. Please SignUp Again and then verify your email ID", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            }

                        }


                        progressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
           else
                Toast.makeText(SignInActivity.this, "Incorrect Email ID or Password", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();


        });

    }


}

