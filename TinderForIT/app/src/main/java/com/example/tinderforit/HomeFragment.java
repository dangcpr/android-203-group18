package com.example.tinderforit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.google.firebase.database.ValueEventListener;
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

    public String userGender;
    public String oppositeGender;

    private DatabaseReference usersDB;
    private FirebaseUser currentUser;

    ArrayList<Data> array = new ArrayList<>();
    MyAppAdapter myAppAdapter;

    Button btnTest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        SwipeFlingAdapterView filingAdapterView;
        filingAdapterView = (SwipeFlingAdapterView)v.findViewById(R.id.swipe);

        ViewHolder viewHolder;
        myAppAdapter = new MyAppAdapter(array, getContext());

        usersDB = FirebaseDatabase.getInstance().getReference().child("UserProfile");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        filingAdapterView.setAdapter(myAppAdapter);
        filingAdapterView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object o) {
                // Dislike case
                Data person = array.get(0);
                String personID = person.getUID();
                Log.e("Dislike",personID);
                usersDB.child(personID).child("Connection").child("Dislike").child(currentUser.getUid()).setValue(true);

                array.remove(0);
                myAppAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRightCardExit(Object o) {
                // Like case
                Data person = array.get(0);
                String personID = person.getUID();
                Log.e("Like",personID);
                usersDB.child(personID).child("Connection").child("Like").child(currentUser.getUid()).setValue(true);

                array.remove(0);
                myAppAdapter.notifyDataSetChanged();

                isMatchWith(personID);
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

    @Override
    public void onResume() {
        super.onResume();

        checkUserGender();
    }

    @Override
    public void onStop() {
        super.onStop();
        array.clear();
        myAppAdapter.notifyDataSetChanged();
    }

    private void isMatchWith(String personID) {
        DatabaseReference currentConnectionDb = usersDB.child(currentUser.getUid()).child("Connection").child("Like").child(personID);
        currentConnectionDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Log.e("new Match","true");
                    usersDB.child(snapshot.getKey()).child("Connection").child("Match").child(currentUser.getUid()).setValue(0);
                    usersDB.child(currentUser.getUid()).child("Connection").child("Match").child(snapshot.getKey()).setValue(0);
                    // Change MatchFragment

                    ((MainActivity) getActivity()).onMsgFromFragToMain("HomeFragment", currentUser.getUid() + "," + snapshot.getKey());
                    ((MainActivity) getActivity()).replaceToCongratMatchedFragments();
                }
                else {
                    // just one-way like
                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    public int checkUserGender(){
        DatabaseReference userDb = usersDB.child(currentUser.getUid());
        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        if (snapshot.child("gender").getValue() != null){
                            userGender = snapshot.child("gender").getValue().toString();
                            switch (userGender) {
                                case "Male":
                                    oppositeGender = "Female";
                                    Log.e("Opposite",oppositeGender);
                                    break;
                                case "Female":
                                    oppositeGender = "Male";
                                    Log.e("Opposite",oppositeGender);
                                    break;
                            }

                            getOppositeGenderData();
                        }
                    }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }

        });
        return 1;
    }

    public void getOppositeGenderData(){
        {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            usersDB.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@androidx.annotation.NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if (snapshot.exists()
                            && !snapshot.getKey().equals(user.getUid()) // Don't show user data on match card
                            && !snapshot.child("Connection").child("Dislike").hasChild(currentUser.getUid()) // Don't show people whom user disliked
                            && !snapshot.child("Connection").child("Like").hasChild(currentUser.getUid())  // Don't show people whom user liked
                            &&  snapshot.child("gender").getValue().toString().equals(oppositeGender))
                    {

                        if(snapshot.child("firstName").exists()){
                            firstName = snapshot.child("firstName").getValue().toString() + " ";
                        }

                        if(snapshot.child("lastName").exists()){
                            lastName = snapshot.child("lastName").getValue().toString();
                        }

                        if(snapshot.child("dateOfBirth").exists()){
                            DOB = snapshot.child("dateOfBirth").getValue().toString();
                        }

                        if(snapshot.child("imageUrl").exists()){
                            avatar = snapshot.child("imageUrl").getValue().toString();
                        }

                        if(snapshot.child("email").exists()){
                            email = snapshot.child("email").getValue().toString();
                        }

                        array.add(new Data(snapshot.getKey(),firstName, lastName, DOB, avatar));

                        myAppAdapter.notifyDataSetChanged();

                        avatar=""; // avatar like a "global" var in this fragment -> must clear before get next person data

                        Log.e("size",String.valueOf(myAppAdapter.getCount()));
                        Log.e("Debug: ", firstName + " " + lastName + " " + DOB + " " + avatar);
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

        }
    }
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

    private String UID;
    private String dataFirstName;
    private String dataLastname;
    private String dataDOB;
    private String dataAvatar;

    public Data(String UID, String dataFirstName, String dataLastname, String dataDOB, String dataAvatar) {
        this.UID = UID;
        this.dataFirstName = dataFirstName;
        this.dataLastname = dataLastname;
        this.dataDOB = dataDOB;
        this.dataAvatar = dataAvatar;
    }

    public String getUID(){return UID;}

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