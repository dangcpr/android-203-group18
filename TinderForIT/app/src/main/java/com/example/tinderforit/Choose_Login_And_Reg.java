package com.example.tinderforit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.internal.BaseGmsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Arrays;

public class Choose_Login_And_Reg extends Activity {

    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    Button btnLoginFacebook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_and_reg);

        btnLoginFacebook = (Button) findViewById(R.id.login_with_facebook);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        mAuth = FirebaseAuth.getInstance();


        ///////////////////////////////
        mCallbackManager = CallbackManager.Factory.create();

        // Kết nối với facebook
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            // Kết nối thành công
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            // Không cho phép kết nối
            @Override
            public void onCancel() {}
            @Override
            public void onError(FacebookException error) {}
        });
        ////////////////////////////////

        btnLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(Choose_Login_And_Reg.this,
                        Arrays.asList("email", "public_profile"));
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    boolean _isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                    if(_isNewUser){
                        Toast.makeText(Choose_Login_And_Reg.this,"Đăng kí",Toast.LENGTH_SHORT).show();
                        // Activity Đăng kí
                        // Tạm thời giả sử đăng kí xong, vào main luôn // Xài activity giả là LoginFbActivity, Activity này không cần dùng đến
                        Intent myIntent = new Intent(Choose_Login_And_Reg.this,LoginFacebookActivity.class);
                        startActivity(myIntent);
                    }else{
                        Toast.makeText(Choose_Login_And_Reg.this,"Đăng nhập",Toast.LENGTH_SHORT).show();
                        // Activity Main
                        Intent myIntent = new Intent(Choose_Login_And_Reg.this,LoginFacebookActivity.class);
                        startActivity(myIntent);
                    }
                } else {
                    Toast.makeText(Choose_Login_And_Reg.this, "Error", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {

    }
}