
package com.example.tinderforit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

public class Choose_Login_And_Reg extends Activity {

    private Button mLogin, mRegister;

    private static final int RC_SIGN_IN=1701;
    private FirebaseAuth mAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    CallbackManager mCallbackManager;
    private LoginButton loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_and_reg);

        Button mLoginEmail = (Button) findViewById(R.id.login_with_email);
        Button mLoginFacebook = (Button) findViewById(R.id.login_with_facebook);
        Button mLoginPhone = (Button) findViewById(R.id.login_with_phone_number);
        Button mLoginGoogle = (Button) findViewById(R.id.login_with_google);
        loginButton=findViewById(R.id.login_button);
        //firebase and google sign in
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //Check if user is signed in (non-null)
        if(currentUser != null){
            currentUser.reload();
        }

        //login facebook
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallbackManager = CallbackManager.Factory.create();
                loginButton.setPermissions(Arrays.asList("email","public_profile"));
                loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(Choose_Login_And_Reg.this,"Login Facebook success",Toast.LENGTH_SHORT).show();
                        //Toast.makeText(Choose_Login_And_Reg.this  ,String.valueOf(loginResult.getAccessToken()),Toast.LENGTH_LONG).show();
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }
                    @Override
                    public void onCancel() {
                        Toast.makeText(Choose_Login_And_Reg.this,"Login Facebook cancel",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(@NonNull FacebookException error) {
                        Toast.makeText(Choose_Login_And_Reg.this,"Login Facebook error"+error.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

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
                loginButton.callOnClick();
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
                LoginWithGoogle();
            }
        });

    }

    private  void LoginWithGoogle(){
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc= GoogleSignIn.getClient(this,gso);
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
                AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    //finish();
                                    if(task.getResult().getAdditionalUserInfo().isNewUser()){
                                        //Do Stuffs for new user
                                        Intent myIntent=new Intent(Choose_Login_And_Reg.this, FirstComeActivity.class);
                                        startActivity(myIntent);
                                        finish();
                                    }else{
                                        Intent myIntent=new Intent(Choose_Login_And_Reg.this, MainActivity.class);
                                        startActivity(myIntent);
                                        finish();
                                    }
                                    Intent myIntent=new Intent(Choose_Login_And_Reg.this,MainActivity.class);
                                    startActivity(myIntent);
                                }
                                else{
                                    //do something
                                    Toast.makeText(Choose_Login_And_Reg.this, "GG Login:something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            catch (ApiException e){
                //do something
                Toast.makeText(Choose_Login_And_Reg.this, "GG Login:something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        else{//login facebook
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //finish();
                            boolean _isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                            if(_isNewUser){
                                //Do Stuffs for new user
                                Toast.makeText(Choose_Login_And_Reg.this,"Login Facebook Fail Show Form Sign Up",Toast.LENGTH_SHORT).show();
                            }else{
                                //Continue with Sign up
                                Toast.makeText(Choose_Login_And_Reg.this,"Login Facebook Success User is exists in Firebase",Toast.LENGTH_SHORT).show();
                            }
                            Intent myIntent=new Intent(Choose_Login_And_Reg.this,MainActivity.class);
                            startActivity(myIntent);
                        }
                        else{
                            //do something
                            Toast.makeText(Choose_Login_And_Reg.this, "FB Login: something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}