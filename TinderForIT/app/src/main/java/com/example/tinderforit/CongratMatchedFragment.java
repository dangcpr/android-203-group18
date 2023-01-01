package com.example.tinderforit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.tinderforit.app_interface.FragmentCallBacks;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CongratMatchedFragment extends Fragment implements FragmentCallBacks {

    public CongratMatchedFragment() {
        // Required empty public constructor
    }

    Button btnToHomeFragment, btnToChatFragment;
    ImageView userImg, oppoImg;
    String [] twoUID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_congrat_matched, container, false);

        btnToHomeFragment = (Button) v.findViewById(R.id.btnToHomeFrag);
        btnToChatFragment = (Button) v.findViewById(R.id.btnToChatFrag);
        userImg = (ImageView) v.findViewById(R.id.img_user);
        oppoImg = (ImageView) v.findViewById(R.id.img_opposite);

        btnToHomeFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).replaceToHomeFragments();
            }
        });

        btnToChatFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).replaceToChatFragments();
            }
        });

        // Get avatar
        DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("UserProfile");
        user.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(twoUID[0])){
                    Glide.with(getContext()).load(snapshot.child("imageUrl").getValue().toString()).into(userImg);
                }
                if (snapshot.getKey().equals(twoUID[1])){
                    Glide.with(getContext()).load(snapshot.child("imageUrl").getValue().toString()).into(oppoImg);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return v;
    }

    @Override
    public void onMsgFromMainToFragment(String strValue) {
        twoUID = strValue.split(",");
    }
}