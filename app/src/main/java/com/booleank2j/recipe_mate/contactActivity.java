package com.booleank2j.recipe_mate;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class contactActivity extends AppCompatActivity {
    TextView sendEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.contact_us);
        sendEmail=findViewById(R.id.sendEmail);
        sendEmail.setOnClickListener(view -> {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            String[] recipients={"autocratsboolean@gmail.com"};
            emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
            emailIntent.setType("text/html");
            emailIntent.setPackage("com.google.android.gm");
            startActivity(Intent.createChooser(emailIntent, "Send mail"));
        });
    }
}
