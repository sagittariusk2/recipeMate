package com.booleank2j.recipe_mate;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Objects;

import static com.booleank2j.recipe_mate.feedActivity.LogIn;

public class MainActivity extends AppCompatActivity {

    private Button signupBtn;
   private Button loginbtn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_main);
        signupBtn=findViewById(R.id.signupbtn);
        loginbtn=findViewById(R.id.Loginbtn);
       sharedPreferences = getSharedPreferences("userDB", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (!LogIn) {
            editor.putBoolean("LoggedIn", false);
            editor.apply();
        }
        if (sharedPreferences.getBoolean("LoggedIn", false)) {
            Intent in = new Intent(MainActivity.this,feedActivity.class);
            in.putExtra("emailID", sharedPreferences.getString("Email", ""));
            in.putExtra("name", sharedPreferences.getString("name", ""));
            in.putExtra("likes", sharedPreferences.getString("likes", ""));
            in.putExtra("dislikes", sharedPreferences.getString("dislikes", ""));
            in.putExtra("profileurl", sharedPreferences.getString("profileurl", ""));
            in.putExtra("LoggedIn", sharedPreferences.getBoolean("LoggedIn", false));
            this.finish();
            startActivity(in);
        }

      signupBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!isConnected(MainActivity.this)){
                    showCustomDialog();

                }
                else {
                    Intent in = new Intent(MainActivity.this, SignUpActivity.class);
                    finish();
                    startActivity(in);
                }
            }
        });
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected(MainActivity.this)){
                    showCustomDialog();

                }
                else {
                    Intent in = new Intent(MainActivity.this, SignInActivity.class);
                    finish();
                    startActivity(in);
                }
            }
        });


    }
    private boolean isConnected(MainActivity mainActivity) {
        ConnectivityManager connectivityManager=(ConnectivityManager)mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifi!=null && wifi.isConnected()) ||(mobile!=null && mobile.isConnected())){
            return true;
        }
        else{
            return false;
        }
    }
    private void showCustomDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogStyle);

        builder.setMessage("Please connect to the internet to proceed further").setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(MainActivity.this,MainActivity.class));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));

    }

}