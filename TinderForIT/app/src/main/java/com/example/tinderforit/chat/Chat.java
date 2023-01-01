package com.example.tinderforit.chat;

import android.app.Activity;
import android.os.Bundle;

import com.example.tinderforit.R;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinderforit.messages.Messages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import de.hdodenhof.circleimageview.CircleImageView;

public class Chat extends Activity {
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://tinder-it-e576d-default-rtdb.firebaseio.com/");
    private FirebaseAuth mAuth;

    private RecyclerView chattingRecyclerView;
    private final List<ChatList> chatLists=new ArrayList<ChatList>();
    private ChatAdapter chatAdapter;

    private String getChatKey;
    private String getUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ImageView btn_arrowBack=findViewById(R.id.btn_arrowBack);
        final TextView txt_username=findViewById(R.id.txt_username);
        final EditText edt_messagesEditText=findViewById(R.id.edt_messagesEditText);
        final ImageView btn_send=findViewById(R.id.btn_send);
        final CircleImageView ptr_userProfile=findViewById(R.id.ptr_userProfile);
        //firebase authentication
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
        final String getUIDCurrentUser=mAuth.getUid();
        //get data from message adapter class
        final  String getUserName= getIntent().getStringExtra("userName");
        final  String getUserProfile= getIntent().getStringExtra("userProfileURL");
        getChatKey= getIntent().getStringExtra("userChatKey");
        getUID= getIntent().getStringExtra("userUID");
        txt_username.setText(getUserName);
        Picasso.get().load(getUserProfile).into(ptr_userProfile);
        //Recycler view
        chattingRecyclerView=findViewById(R.id.list_messages);
        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(Chat.this));
        chatAdapter=new ChatAdapter(chatLists,Chat.this);
        chattingRecyclerView.setAdapter(chatAdapter);
        //set last time seen msg
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int countMsg =(int)snapshot.child("Chat").child(getChatKey).child("Messages").getChildrenCount();
                databaseReference.child("UserProfile").child(getUIDCurrentUser).child("Connection").child("Match").child(getUID).setValue(countMsg);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //set last time seen msg


        //set adapter msg
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(getChatKey.isEmpty()) {
                    getChatKey = String.valueOf(snapshot.child("Chat").getChildrenCount()+1);
                }
                chatLists.clear();
                if (snapshot.hasChild("Chat")) {
                    final String getFirstUID= snapshot.child("Chat").child(getChatKey).child("FirstUID").getValue(String.class);
                    final String getSecondUID= snapshot.child("Chat").child(getChatKey).child("SecondUID").getValue(String.class);
                    if (snapshot.child("Chat").child(getChatKey).hasChild("Messages")){
                        chatLists.clear();

                        if((getFirstUID.equals(getUID) && getSecondUID.equals(getUIDCurrentUser)) || (getSecondUID.equals(getUID) && getFirstUID.equals(getUIDCurrentUser))) {
                            for (DataSnapshot messagesSnapshot : snapshot.child("Chat").child(getChatKey).child("Messages").getChildren()) {
                                if (messagesSnapshot.hasChild("msg") && messagesSnapshot.hasChild("FromUID")) {

                                    final String messagesTimestamp = messagesSnapshot.getKey();
                                    final String messagesFromUID = messagesSnapshot.child("FromUID").getValue(String.class);
                                    final String messagesText = messagesSnapshot.child("msg").getValue(String.class);


                                    //assert messagesTimestamp != null;
                                    Timestamp timestamp = new Timestamp(Long.parseLong(messagesTimestamp));
                                    Date date=new Date(timestamp.getTime());
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
                                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa",Locale.getDefault());

                                    //Add new msg to RecyclerView
                                    ChatList chatList = new ChatList(messagesFromUID, messagesText, simpleDateFormat.format(date), simpleTimeFormat.format(date));
                                    chatLists.add(chatList);
                                    chatAdapter.updateChatList(chatLists);
                                    chattingRecyclerView.scrollToPosition(chatLists.size() - 1);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Chat.this, Messages.class);
                startActivity(intent);
            }
        });
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String getChatMessage=edt_messagesEditText.getText().toString();
                if(!getChatMessage.equals("")){
                    final String currentTimestamp=String.valueOf(System.currentTimeMillis());
                    Timestamp timestamp = new Timestamp(Long.parseLong(currentTimestamp));
                    Date date=new Date(timestamp.getTime());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
                    SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa",Locale.getDefault());
                    final String messagesFromUID=mAuth.getUid();
                    final String messagesToUID=getUID;

                    databaseReference.child("Chat").child(getChatKey).child("FirstUID").setValue(messagesFromUID);
                    databaseReference.child("Chat").child(getChatKey).child("SecondUID").setValue(messagesToUID);
                    databaseReference.child("Chat").child(getChatKey).child("Messages").child(currentTimestamp).child("FromUID").setValue(mAuth.getUid());
                    databaseReference.child("Chat").child(getChatKey).child("Messages").child(currentTimestamp).child("msg").setValue(getChatMessage);

                    //update messages
                    ChatList chatList = new ChatList(messagesFromUID, getChatMessage,simpleDateFormat.format(date), simpleTimeFormat.format(date));
                    chatLists.add(chatList);
                    chatAdapter.updateChatList(chatLists);
                    chattingRecyclerView.scrollToPosition(chatLists.size() - 1);
                    //update last time users seen msg
                    //set last time seen msg
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int countMsg =(int)snapshot.child("Chat").child(getChatKey).child("Messages").getChildrenCount();
                            databaseReference.child("UserProfile").child(messagesFromUID).child("Connection").child("Match").child(getUID).setValue(countMsg);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //clear buff Edit text
                    edt_messagesEditText.setText("");
                }
            }
        });
    }
}