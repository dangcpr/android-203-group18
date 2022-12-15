package com.example.tinderforit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.Login;
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

    private CallbackManager mCallbackManager;
    Button btnLoginFacebook;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    //CallbackManager mCallbackManager;
    private LoginButton loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_and_reg);

        Button mLoginEmail = (Button) findViewById(R.id.login_with_email);
        Button mLoginFacebook = (Button) findViewById(R.id.login_with_facebook);
        Button mLoginPhone = (Button) findViewById(R.id.login_with_phone_number);
        Button mLoginGoogle = (Button) findViewById(R.id.login_with_google);


//        //firebase and google sign in
//        mAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        //Check if user is signed in (non-null)
//        if (currentUser != null) {
//            currentUser.reload();
//        }
//        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        gsc = GoogleSignIn.getClient(this, gso);
//        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
//            // Kết nối thành công
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                handleFacebookAccessToken(loginResult.getAccessToken());
//            }
//
//            // Không cho phép kết nối
//            @Override
//            public void onCancel() {
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//            }
//        });



//        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LoginManager.getInstance().logInWithReadPermissions(Choose_Login_And_Reg.this,
//                        Arrays.asList("email", "public_profile"));
//            }
//        });

        loginButton=findViewById(R.id.login_button);
        //firebase and google sign in
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //Check if user is signed in (non-null)
        if(currentUser != null){
            currentUser.reload();
        }
        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        gsc= GoogleSignIn.getClient(this,gso);

        //login facebook
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallbackManager = CallbackManager.Factory.create();
                //loginButton.setReadPermissions("email", "public_profile");
                loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(Choose_Login_And_Reg.this,"Login Facebook success",Toast.LENGTH_SHORT).show();
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
                LoginWithFacebook();
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


//    }
//    private  void LoginWithGoogle(){
//        //login with google account
//        Intent signIntent=gsc.getSignInIntent();
//        startActivityForResult(signIntent,RC_SIGN_IN);
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode==RC_SIGN_IN){
//            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
//            try{
//                GoogleSignInAccount account=task.getResult(ApiException.class);
//                AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
//                mAuth.signInWithCredential(credential)
//                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if(task.isSuccessful()){
//                                    //finish();
//                                    boolean _isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
//
//                                    if(_isNewUser){
//                                        //Do Stuffs for new user
//                                        Toast.makeText(Choose_Login_And_Reg.this,"Login Fail Show Form Sign Up",Toast.LENGTH_SHORT).show();
//                                    }else{
//                                        //Continue with Sign up
//                                        Toast.makeText(Choose_Login_And_Reg.this,"Login Success User is exists in Firebase",Toast.LENGTH_SHORT).show();
//                                    }
//                                    Intent myIntent=new Intent(Choose_Login_And_Reg.this,MainActivity.class);
//                                    startActivity(myIntent);
//                                }
//                                else{
//                                    //do something
//                                    Toast.makeText(Choose_Login_And_Reg.this, "something went wrong", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//            }
//            catch (ApiException e){
//                //do something
//                Toast.makeText(Choose_Login_And_Reg.this, "something went wrong", Toast.LENGTH_SHORT).show();
//            }
//        }
//        else
//        {
//            mCallbackManager.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//    private void handleFacebookAccessToken(AccessToken token) {
//        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
//        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                if (task.isSuccessful()) {
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    boolean _isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
//                    if(_isNewUser){
//                        Toast.makeText(Choose_Login_And_Reg.this,"Đăng kí",Toast.LENGTH_SHORT).show();
//                        // Activity Đăng kí
//                        // Tạm thời giả sử đăng kí xong, vào main luôn // Xài activity giả là LoginFbActivity, Activity này không cần dùng đến
//                        Intent myIntent = new Intent(Choose_Login_And_Reg.this,LoginFacebookActivity.class);
//                        startActivity(myIntent);
//                    }else{
//                        Toast.makeText(Choose_Login_And_Reg.this,"Đăng nhập",Toast.LENGTH_SHORT).show();
//                        // Activity Main
//                        Intent myIntent = new Intent(Choose_Login_And_Reg.this,LoginFacebookActivity.class);
//                        startActivity(myIntent);
//                    }
//                } else {
//                    Toast.makeText(Choose_Login_And_Reg.this, "Error", Toast.LENGTH_SHORT).show();
//                    updateUI(null);
//                }
//            }
//        });
//    }
//
//    private void updateUI(FirebaseUser user) {
//

    }

    private void LoginWithFacebook() {
        //login with google account
        //when you click on facebook icon call loginButton.setOnClickListener();
        loginButton.callOnClick();
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
                AuthCredential credential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    //finish();
                                    boolean _isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();

                                    if(_isNewUser){
                                        //Do Stuffs for new user
                                        Toast.makeText(Choose_Login_And_Reg.this,"Login Google Fail Show Form Sign Up",Toast.LENGTH_SHORT).show();
                                    }else{
                                        //Continue with Sign up
                                        Toast.makeText(Choose_Login_And_Reg.this,"Login GoogleSuccess User is exists in Firebase",Toast.LENGTH_SHORT).show();
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
        Log.d("Token: ", "Token: " + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Choose_Login_And_Reg.this, new OnCompleteListener<AuthResult>() {
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