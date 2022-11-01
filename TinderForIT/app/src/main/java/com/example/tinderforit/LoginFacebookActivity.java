package com.example.tinderforit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginFacebookActivity extends Activity {

    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_facebook);

        btnBack = (Button) findViewById(R.id.Back);

        btnBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Toast.makeText(LoginFacebookActivity.this,"Đăng Xuất", Toast.LENGTH_SHORT).show();
                // Đăng xuất trên firebase
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(LoginFacebookActivity.this, Choose_Login_And_Reg.class);
                startActivity(i);
            }
        });
    }
}