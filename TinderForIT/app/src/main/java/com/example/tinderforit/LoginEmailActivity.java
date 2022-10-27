package com.example.tinderforit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginEmailActivity extends Activity {

    Button mCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        mCreateAccount = findViewById(R.id.btn_create_account);

        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginEmailActivity.this, com.example.tinderforit.CreateEmailAccountActivity.class);
                startActivity(i);
            }
        });
    }
}