package com.example.tinderforit;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
public class LoginGoogleActivity extends Activity {
    // nếu hiện dialog lên thì không cần gọi activity này
    private static final int RC_SIGN_IN=7421;
    Button btnLogin;

    private FirebaseAuth mAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_google);

        btnLogin=(Button) findViewById(R.id.btnGoogle);
        //connect firebase and google login request
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Check if user is signed in (non-null)
        if(currentUser != null){
            currentUser.reload();
        }
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc= GoogleSignIn.getClient(this,gso);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginWithGoogle();
            }
        });
    }
    private  void LoginWithGoogle(){
        //login with google account
        Intent signIntent=gsc.getSignInIntent();
        startActivityForResult(signIntent,RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account=task.getResult(ApiException.class);
                AuthCredential credential=GoogleAuthProvider.getCredential(account.getIdToken(),null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    //finish();
                                    Intent myIntent=new Intent(LoginGoogleActivity.this, SplashScreenActivity.class);
                                    startActivity(myIntent);
                                }
                                else{
                                    //do something
                                    Toast.makeText(LoginGoogleActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            catch (ApiException e){
                //do something
                Toast.makeText(LoginGoogleActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

}