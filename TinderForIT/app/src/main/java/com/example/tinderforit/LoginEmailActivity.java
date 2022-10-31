package com.example.tinderforit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginEmailActivity extends Activity {
    private Button mLogin;
    private Button mCreateAccount;
    private EditText infoEmail;
    private String Email;
    private EditText infoPassword;
    private String Password;
    private TextView mForgetPass;

    private FirebaseAuth mAuth;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);

        mLogin = findViewById(R.id.btn_login);
        mCreateAccount = findViewById(R.id.btn_create_account);
        infoEmail = findViewById(R.id.info_email);
        infoPassword = findViewById(R.id.info_password);
        mForgetPass = findViewById(R.id.forgot_pass);

        context = getApplicationContext();

        mAuth = FirebaseAuth.getInstance();

        mForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (infoEmail.getText().toString().isEmpty()) {
                    infoEmail.setError("Email is Missing");
                    return;
                }

                Email = infoEmail.getText().toString();
                mAuth.sendPasswordResetEmail(Email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(LoginEmailActivity.this,"Password reset link sent via email\nPlease also check your spam mail", Toast.LENGTH_LONG).show();
                                else
                                    Toast.makeText(LoginEmailActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check if Email or Password is empty
                if (infoEmail.getText().toString().isEmpty()) {
                    infoEmail.setError("Email is Missing");
                    return;
                }
                if (infoPassword.getText().toString().isEmpty()) {
                    infoPassword.setError("Password is Missing");
                    return;
                }

                //Login user
                Email = infoEmail.getText().toString();
                Password = infoPassword.getText().toString();

                mAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    // Check is verified email
                                    if(!mAuth.getCurrentUser().isEmailVerified()){
                                        Intent i = new Intent(LoginEmailActivity.this, VerifyEmailActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else
                                    Toast.makeText(LoginEmailActivity.this, "Login is successful", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(LoginEmailActivity.this, "Login is failure: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        mCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginEmailActivity.this, com.example.tinderforit.CreateEmailAccountActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        }
    }
}