package com.example.tinderforit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tinderforit.messages.Messages;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends Activity {

    Button btnLogOut, btnMessages;
    private FirebaseAuth mAuth;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //connect firebase and google login request
        mAuth=FirebaseAuth.getInstance();

        btnLogOut=findViewById(R.id.btnLogout);
        btnMessages=findViewById(R.id.btnMessages);




        btnMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, Messages.class);
                startActivity(intent);
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOut();
            }
        });

    }

    private void LogOut(){
        gsc.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mAuth.signOut();
                        //finish();
                        Intent myIntent=new Intent(MainActivity.this,Choose_Login_And_Reg.class);
                        startActivity(myIntent);
                    }
                });
    }
}
