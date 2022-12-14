package com.example.tinderforit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class SplashScreenActivity extends Activity {

    Button btnLogOut;
    private FirebaseAuth mAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private Button Continue;
    ImageSlider imageSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

//        //connect firebase and google login request
//        mAuth=FirebaseAuth.getInstance();
//        gso=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        gsc= GoogleSignIn.getClient(this,gso);

        Continue = findViewById(R.id.Continue);
        imageSlider = findViewById(R.id.image_slider);

        Continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SplashScreenActivity.this, Choose_Login_And_Reg.class);
                startActivity(i);
            }
        });

        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.girl1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.girl2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.girl3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.girl4, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.girl5, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        //display user info

//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
//        if (acct != null) {
//            String personName = acct.getDisplayName();
//            String personGivenName = acct.getGivenName();
//            String personFamilyName = acct.getFamilyName();
//            String personEmail = acct.getEmail();
//            String personId = acct.getId();
//            Uri personPhoto = acct.getPhotoUrl();
//            //show email address
//            Toast.makeText(MainActivity.this,personEmail,Toast.LENGTH_SHORT).show();
//        }
//
//        btnLogOut=(Button) findViewById(R.id.btnLogout);
//        btnLogOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LogOut();
//            }
//        });
//    }
//
//    private void LogOut(){
//        gsc.signOut()
//                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        mAuth.signOut();
//                        //finish();
//                        Intent myIntent=new Intent(MainActivity.this,Choose_Login_And_Reg.class);
//                        startActivity(myIntent);
//                    }
//                });
    }
}
