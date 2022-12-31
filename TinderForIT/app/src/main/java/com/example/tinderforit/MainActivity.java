package com.example.tinderforit;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    ChatFragment chatFragment = new ChatFragment();
    MatchesFragment matchesFragment = new MatchesFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
//
//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                switch (item.getItemId())
//                {
//                    case R.id.action_profile:
//                        Intent i = new Intent(MainActivity.this, SettingProfileActivity.class);
//                        startActivity(i);
//                        break;
//
//                    case R.id.action_chat:
//                        Intent j = new Intent(MainActivity.this, ChatActivity.class);
//                        startActivity(j);
//                        break;
//
//                    case R.id.action_matches:
//                        Intent k = new Intent(MainActivity.this, MatchesActivity.class);
//                        startActivity(k);
//                        break;
//
//                }
//                return false;
//            }
//        });
        bottomNavigationView = findViewById(R.id.bottom_nav);

        getSupportFragmentManager().beginTransaction().replace(R.id.card_frame, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.card_frame, chatFragment).commit();
                        return true;
                    case R.id.action_matches:
                        getSupportFragmentManager().beginTransaction().replace(R.id.card_frame, matchesFragment).commit();
                        return true;
                    case R.id.action_profile:
                        getSupportFragmentManager().beginTransaction().replace(R.id.card_frame, profileFragment).commit();
                        return true;
                    case R.id.action_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.card_frame, homeFragment).commit();
                        return true;
                }
                return false;
            }
        });
    }

    public void replaceToCongratMatchedFragments(CongratMatchedFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.fade_out);
        fragmentTransaction.replace(R.id.card_frame, fragment);
        fragmentTransaction.commit();
    }

    public void replaceToHomeFragments(HomeFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.fade_out);
        fragmentTransaction.replace(R.id.card_frame, fragment);
        fragmentTransaction.commit();
    }

    public void replaceToChatFragments(ChatFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.fade_out);
        fragmentTransaction.replace(R.id.card_frame, fragment);
        fragmentTransaction.commit();
    }
}