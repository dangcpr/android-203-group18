package com.example.tinderforit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateEmailAccountActivity extends AppCompatActivity {
    Button btnRegister, btnReturnLogin;
    EditText edtEmail, edtPassword, edtConfirmPassword;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_email_account);

        edtEmail = findViewById(R.id.edt_register_email);
        edtPassword = findViewById(R.id.edt_register_password);
        edtConfirmPassword = findViewById(R.id.edt_register_retype_password);

        btnRegister = findViewById(R.id.btn_register_account);
        btnReturnLogin = findViewById(R.id.btn_return_login);

        mAuth = FirebaseAuth.getInstance();

        btnReturnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CreateEmailAccountActivity.this, LoginEmailActivity.class);
                startActivity(i);
                finish();
            }
        }); // button return_login

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        }); // button register


    } // onCreate

    private void createUser() {
        String email = edtEmail.getText().toString();
        String pass = edtPassword.getText().toString().trim();
        String repass = edtConfirmPassword.getText().toString().trim();

        if (isValidateInput(email,pass,repass)) {
            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(CreateEmailAccountActivity.this, "Register successfully",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(CreateEmailAccountActivity.this, VerifyEmailActivity.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(CreateEmailAccountActivity.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean isValidateInput(String email, String pass, String repass) {
        boolean result = false;

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email is Missing");
            edtEmail.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Invalid email");
            edtEmail.requestFocus();
        } else if (TextUtils.isEmpty(pass)) {
            edtPassword.setError("Password is Missing");
            edtPassword.requestFocus();
        } else if (pass.length() < 6) {
            edtPassword.setError("Password must be at least 6 characters");
            edtPassword.requestFocus();
        } else if (TextUtils.isEmpty(repass)) {
            edtConfirmPassword.setError("Please confirm password");
            edtConfirmPassword.requestFocus();
        } else if (!TextUtils.equals(pass, repass)) {
            edtConfirmPassword.setError("Must be same as password");
            edtConfirmPassword.requestFocus();
        }
        else {
            result = true;
        }
        return result;
    }
}