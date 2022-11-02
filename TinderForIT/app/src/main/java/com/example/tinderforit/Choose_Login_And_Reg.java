package com.example.tinderforit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Choose_Login_And_Reg extends Activity {

    private Button mLogin, mRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_and_reg);

        Button mLoginEmail = (Button) findViewById(R.id.login_with_email);
        Button mLoginFacebook = (Button) findViewById(R.id.login_with_facebook);
        Button mLoginPhone = (Button) findViewById(R.id.login_with_phone_number);
        Button mLoginGoogle = (Button) findViewById(R.id.login_with_google);

        mLoginEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Choose_Login_And_Reg.this, com.example.tinderforit.LoginEmailActivity.class);
                startActivity(i);
            }
        });

        mLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Choose_Login_And_Reg.this, com.example.tinderforit.LoginFacebookActivity.class);
                startActivity(i);
            }
        });

        mLoginPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Choose_Login_And_Reg.this, com.example.tinderforit.LoginPhoneActivity.class);
                startActivity(i);
            }
        });

        mLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Choose_Login_And_Reg.this, com.example.tinderforit.LoginGoogleActivity.class);
                startActivity(i);
            }
        });

    }
}