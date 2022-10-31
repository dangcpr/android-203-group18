package com.example.tinderforit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class VerifyEmailActivity extends Activity {
    Button btnVerify, btnReturnLogin;
    TextView txtVerifyMsg;
    FirebaseAuth mAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        btnVerify = findViewById(R.id.btn_verify);
        btnReturnLogin = findViewById(R.id.btn_return_login_2);
        txtVerifyMsg = findViewById(R.id.txt_verify_msg);
        mAuth = FirebaseAuth.getInstance();

        btnReturnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VerifyEmailActivity.this, LoginEmailActivity.class);
                startActivity(i);
                finish();
            }
        });

        checkVerifiedEmail();

    }// onCreate

    @Override
    protected void onResume() {
        super.onResume();

        checkVerifiedEmail();
    }

    private void checkVerifiedEmail(){
        mAuth.getCurrentUser().reload();
        boolean isVerified = mAuth.getCurrentUser().isEmailVerified();

        if(isVerified) {
            txtVerifyMsg.setText("");
            btnVerify.setVisibility(View.INVISIBLE);
            Toast.makeText(VerifyEmailActivity.this,"Email verified successfully",Toast.LENGTH_SHORT).show();
        }

        if(!isVerified) {
            txtVerifyMsg.setText("Your Email Address is not Verify\nPlease verify your Email Address");
            btnVerify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                txtVerifyMsg.setText("Verification email is sent.Please check your mail box or spam");
                            } else {
                                Toast.makeText(VerifyEmailActivity.this,"Error" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }
}