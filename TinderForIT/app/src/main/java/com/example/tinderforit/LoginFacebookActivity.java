package com.example.tinderforit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginFacebookActivity extends Activity {

    Button btnBack = (Button) findViewById(R.id.Back);;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_facebook);

        /*btnBack.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                Intent i = new Intent(LoginFacebookActivity.this, Choose_Login_And_Reg.class);
                startActivity(i);
            }
        });*/
    }
}