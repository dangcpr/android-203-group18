package com.example.tinderforit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    String firstName;
    String lastName;
    String DOB;
    String avatar;
    String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);



        SwipeFlingAdapterView filingAdapterView;
        filingAdapterView = (SwipeFlingAdapterView)v.findViewById(R.id.swipe);

        ArrayList<Data> array = new ArrayList<>();

        MyAppAdapter myAppAdapter;
        ViewHolder viewHolder;
        myAppAdapter = new MyAppAdapter(array, getContext());



        checkUserGender();


        // Get data from Firebase to array

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference oppositeGenderDB = FirebaseDatabase.getInstance().getReference().child("UserProfile").child(oppositeGender);

        oppositeGenderDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists() && snapshot.getKey() != user.getUid()){
                    firstName = snapshot.child("firstName").getValue().toString() + " ";
                    lastName = snapshot.child("lastName").getValue().toString();
                    DOB = snapshot.child("dateOfBirth").getValue().toString();
                    avatar = snapshot.child("imageUrl").getValue().toString();
                    email = snapshot.child("email").getValue().toString();
                    array.add(new Data(firstName, lastName, DOB, avatar));

                    myAppAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onChildChanged(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@androidx.annotation.NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {}
        });

        /*
        mDatabase.addValueEventListener(new ValueEventListener() {
            String firstName = "";
            String lastName = "";
            String DOB = "";
            String avatar = "";
            String email = "";
            String emailCurrent = user.getEmail();

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.child("firstName").exists()) {
                        firstName = postSnapshot.child("firstName").getValue().toString() + " ";
                    }
                    if (postSnapshot.child("lastName").exists()) {
                        lastName = postSnapshot.child("lastName").getValue().toString();
                    }
                    if (postSnapshot.child("dateOfBirth").exists()) {
                        DOB = postSnapshot.child("dateOfBirth").getValue().toString();
                    }
                    if (postSnapshot.child("imageUrl").exists()) {
                        avatar = postSnapshot.child("imageUrl").getValue().toString();
                    }
                    if (postSnapshot.child("email").exists()) {
                        email = postSnapshot.child("email").getValue().toString();
                        if (!email.equals(emailCurrent))
                            array.add(new Data(firstName, lastName, DOB, avatar));
                    }
                    Log.e("Debug: ", firstName + " " + lastName + " " + DOB + " " + avatar);
                }
                myAppAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("onCancelled", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        });*/


        filingAdapterView.setAdapter(myAppAdapter);
        filingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
            }

            @Override
            public void onLeftCardExit(Object o) {
            }

            @Override
            public void onRightCardExit(Object o) {
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {
            }

            @Override
            public void onScroll(float v) {
            }
        });


        filingAdapterView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int i, Object o) {

            }
        });

        return v;
    } // onCreateView

    public String userGender;
    public String oppositeGender;

    public void checkUserGender() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference maleDB = FirebaseDatabase.getInstance().getReference().child("UserProfile").child("Male");
        maleDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(user.getUid())){
                    userGender = "Male";
                    oppositeGender = "Female";
                }
            }
            @Override
            public void onChildChanged(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@androidx.annotation.NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {}
        });

        DatabaseReference femaleDB = FirebaseDatabase.getInstance().getReference().child("UserProfile").child("Female");
        femaleDB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(user.getUid())){
                    userGender = "Female";
                    oppositeGender = "Male";
                }
            }
            @Override
            public void onChildChanged(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@androidx.annotation.NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {}
        });
    } // checkUserGender

}



///////////////////////////////////////////////////////////// Other Classes///////////////////////////////////////

class ViewHolder {
    public static FrameLayout background;
    TextView matchname;
    TextView matchDOB;
    ImageView matchavatar;
}
class MyAppAdapter extends BaseAdapter {

    public List<Data> parkingList;
    public Context context;

    MyAppAdapter(List<Data> apps, Context context) {
        this.parkingList = apps;
        this.context = context;
    }

    @Override
    public int getCount() {
        return parkingList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View rowView = convertView;
        ViewHolder viewHolder;
        if (rowView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.data, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.matchname = (TextView) rowView.findViewById(R.id.matchname);
            viewHolder.matchDOB = (TextView) rowView.findViewById(R.id.matchDOB);
            viewHolder.matchavatar = (ImageView) rowView.findViewById(R.id.matchavatar);
            rowView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.matchname.setText(parkingList.get(position).getDataFirstName() + parkingList.get(position).getDataLastName());
        viewHolder.matchDOB.setText(parkingList.get(position).getDataDOB());

        Glide.with(context ).load(parkingList.get(position).getdataAvatar()).into(viewHolder.matchavatar);

        return (rowView);
    }
}

class Data {

    private String dataFirstName;
    private String dataLastname;
    private String dataDOB;
    private String dataAvatar;

    public Data(String dataFirstName, String dataLastname, String dataDOB, String dataAvatar) {
        this.dataFirstName = dataFirstName;
        this.dataLastname = dataLastname;
        this.dataDOB = dataDOB;
        this.dataAvatar = dataAvatar;
    }

    public String getDataFirstName() {
        return dataFirstName;
    }

    public String getDataLastName() {
        return dataLastname;
    }

    public String getDataDOB() {
        return dataDOB;
    }

    public String getdataAvatar() {
        return dataAvatar;
    }
}