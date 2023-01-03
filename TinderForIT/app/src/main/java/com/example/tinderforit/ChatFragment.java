package com.example.tinderforit;




import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Intent;
import android.os.Bundle;


import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //Parameters
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://tinder-it-e576d-default-rtdb.firebaseio.com/");
    private FirebaseAuth mAuth;

    private RecyclerView chattingRecyclerView;
    private final List<ChatList> chatLists=new ArrayList<ChatList>();
    private ChatAdapter chatAdapter;

    private String getChatKey;
    private String getUID;

    private Button butGetCall;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {     super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_chat, container, false);

        final ImageView btn_arrowBack=view.findViewById(R.id.btn_arrowBack);
        final TextView txt_username=view.findViewById(R.id.txt_username);
        final EditText edt_messagesEditText=view.findViewById(R.id.edt_messagesEditText);
        final ImageView btn_send=view.findViewById(R.id.btn_send);
        final CircleImageView ptr_userProfile=view.findViewById(R.id.ptr_userProfile);
        //firebase authentication
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
        final String getUIDCurrentUser=mAuth.getUid();
        //get data from message adapter class
        Bundle bundle=this.getArguments();
        final  String getUserName= bundle.getString("userName");
        final  String getUserProfile= bundle.getString("userProfileURL");
        getChatKey= bundle.getString("userChatKey");
        getUID= bundle.getString("userUID");


        txt_username.setText(getUserName);
        Picasso.get().load(getUserProfile).into(ptr_userProfile);
        //Recycler view
        chattingRecyclerView=view.findViewById(R.id.list_messages);
        chattingRecyclerView.setHasFixedSize(true);
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatAdapter=new ChatAdapter(chatLists,getActivity());
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
                MessagesFragment messagesFragment=new MessagesFragment();
                FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.card_frame,messagesFragment).commit();

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

        //Phím thực hiện cuộc gọi
        butGetCall = (Button) view.findViewById(R.id.butGetCall);
        butGetCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getBaseContext(), CallActivity.class);
                i.putExtra("userUID_Call", getUID);
                getActivity().startActivity(i);
            }
        });

        return view;
    }
}

class ChatList {
    private String FromUID, Msg, DateSend, TimeSend;

    public ChatList(String fromUID, String msg, String dateSend, String timeSend) {
        FromUID = fromUID;
        Msg = msg;
        DateSend = dateSend;
        TimeSend = timeSend;
    }

    public String getFromUID() {
        return FromUID;
    }

    public String getMsg() {
        return Msg;
    }

    public String getDateSend() {
        return DateSend;
    }

    public String getTimeSend() {
        return TimeSend;
    }
}