package com.example.tinderforit.messages;


import android.app.Activity;
import android.os.Bundle;

import com.example.tinderforit.R;

import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class Messages extends Activity {
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://tinder-it-e576d-default-rtdb.firebaseio.com/");
    private FirebaseAuth mAuth;

    private RecyclerView messagesRecyclerView;
    private final List<MessagesList> messagesLists=new ArrayList<MessagesList>();
    private MessagesAdapter messagesAdapter;

    private String getGender="";
    private String getGenderMatch="";
    private ImageView userPicture;
    private String  getChatKey="";
    private String getLastMessage="";
    int getSeenMassage=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        //Objects
        userPicture=findViewById(R.id.userProfilePic);
        //Firebase authentication
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
        final String getUIDCurrentUser=mAuth.getUid();
        //set Recycler View
        messagesRecyclerView=findViewById(R.id.list_messagesRecyclerView);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager( new LinearLayoutManager(this));
        messagesAdapter=new MessagesAdapter(messagesLists,Messages.this);
        messagesRecyclerView.setAdapter(messagesAdapter);
        //get profile picture from realtime database
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //set profile pic to images view.
                if (snapshot.child("UserProfile").child("Female").hasChild(getUIDCurrentUser)) {
                    getGender="Female";
                    final  String userProfileUrl = snapshot.child("UserProfile").child("Female").child(getUIDCurrentUser).child("imageUrl").getValue(String.class);
                    Picasso.get().load(userProfileUrl).into(userPicture);
                }
                else{
                    getGender="Male";
                    final String userProfileUrl = snapshot.child("UserProfile").child("Male").child(getUIDCurrentUser).child("imageUrl").getValue(String.class);
                    Picasso.get().load(userProfileUrl).into(userPicture);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //Add contact list
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesLists.clear();
                if (getGender.equals("Female"))
                    getGenderMatch="Male";
                else getGenderMatch="Female";
                assert getUIDCurrentUser != null;
                for (DataSnapshot dataSnapshot : snapshot.child("UserProfile").child(getGender).child(getUIDCurrentUser).child("Connection").child("Match").getChildren()) {
                    final String userUIDMatched = dataSnapshot.getKey();

                    if (!Objects.equals(mAuth.getUid(), userUIDMatched)) {
                        assert userUIDMatched != null;
                        String userName = snapshot.child("UserProfile").child(getGenderMatch).child(userUIDMatched).child("lastName").getValue(String.class)+" "+
                                snapshot.child("UserProfile").child(getGenderMatch).child(userUIDMatched).child("firstName").getValue(String.class);
                        String userProfileURL = snapshot.child("UserProfile").child(getGenderMatch).child(userUIDMatched).child("imageUrl").getValue(String.class);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                getLastMessage = "";
                                getChatKey = "";
                                getSeenMassage = 0;
                                int getChatCounts=(int)snapshot.getChildrenCount();
                                if(getChatCounts>0){
                                    for (DataSnapshot chatKeyDataSnapshot:snapshot.child("Chat").getChildren()){
                                        if(chatKeyDataSnapshot.hasChild("FirstUID")&&chatKeyDataSnapshot.hasChild("SecondUID")&&chatKeyDataSnapshot.hasChild("Messages")){

                                            final String getFirstUid=chatKeyDataSnapshot.child("FirstUID").child("UID").getValue(String.class);
                                            final String getSecondUid =chatKeyDataSnapshot.child("SecondUID").child("UID").getValue(String.class);

                                            assert getFirstUid != null;
                                            if((getFirstUid.equals(userUIDMatched) && getSecondUid.equals(getUIDCurrentUser)) || (getFirstUid.equals(getUIDCurrentUser) && getSecondUid.equals(userUIDMatched)) ){
                                                getChatKey=chatKeyDataSnapshot.getKey();
                                                for(DataSnapshot chatDataSnapshot:chatKeyDataSnapshot.child("Messages").getChildren()){
                                                    int countMsg = (int)snapshot.child("Chat").child(getChatKey).child("Messages").getChildrenCount();
                                                    int lastMsgSeen;
                                                    if (getFirstUid.equals(getUIDCurrentUser))
                                                        lastMsgSeen=snapshot.child("Chat").child(getChatKey).child("FirstUID").child("seenMsg").getValue(Integer.class);
                                                    else lastMsgSeen=snapshot.child("Chat").child(getChatKey).child("SecondUID").child("seenMsg").getValue(Integer.class);


                                                    getSeenMassage=(countMsg-lastMsgSeen);

                                                    getLastMessage=chatDataSnapshot.child("msg").getValue(String.class);
                                                }
                                            }
                                        }
                                    }
                                }

                                MessagesList messagesList = new MessagesList(getChatKey,userUIDMatched,userName,getLastMessage,userProfileURL, getSeenMassage);
                                messagesLists.add(messagesList);
                                messagesAdapter.updateMessagesList(messagesLists);
                                messagesRecyclerView.scrollToPosition(messagesLists.size() - 1);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}