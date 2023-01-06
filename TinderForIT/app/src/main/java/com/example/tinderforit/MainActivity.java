package com.example.tinderforit;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.tinderforit.app_interface.MainCallBacks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends FragmentActivity implements MainCallBacks {

    BottomNavigationView bottomNavigationView;
    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    ChatFragment chatFragment = new ChatFragment();
    MessagesFragment messagesFragment = new MessagesFragment();
    MatchesFragment matchesFragment = new MatchesFragment();
    CongratMatchedFragment congratMatchedFragment = new CongratMatchedFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_nav);

        getSupportFragmentManager().beginTransaction().replace(R.id.card_frame, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.card_frame, messagesFragment).commit();
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
    } // onCreate


    public void replaceToCongratMatchedFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.fade_out);
        fragmentTransaction.replace(R.id.card_frame, congratMatchedFragment);
        fragmentTransaction.commit();

        hideNavBar();
    }

    public void replaceToHomeFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom);
        fragmentTransaction.replace(R.id.card_frame, homeFragment);
        fragmentTransaction.commit();

        bottomNavigationView.setSelectedItemId(R.id.action_home);
        showNavBar();
    }

    public void replaceToChatFragments() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
        fragmentTransaction.replace(R.id.card_frame, messagesFragment);
        fragmentTransaction.commit();

        bottomNavigationView.setSelectedItemId(R.id.action_chat);
        showNavBar();
    }

    private void hideNavBar(){
        bottomNavigationView.setVisibility(View.GONE);
    }

    private void showNavBar(){
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMsgFromFragToMain(String sender, String strValue) {
        if (sender.equals("HomeFragment")){
            congratMatchedFragment.onMsgFromMainToFragment(strValue);
        }
    }
}