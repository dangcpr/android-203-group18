package com.example.tinderforit;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MessagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagesFragment newInstance(String param1, String param2) {
        MessagesFragment fragment = new MessagesFragment();
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

    // Parameters
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://tinder-it-e576d-default-rtdb.firebaseio.com/");
    private FirebaseAuth mAuth;

    private RecyclerView messagesRecyclerView;
    private final List<MessagesList> messagesLists=new ArrayList<MessagesList>();
    private MessagesAdapter messagesAdapter;

    private ImageView userPicture;
    private String  getChatKey="";
    private String getLastMessage="";
    int getSeenMassage=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_messages, container, false);
        //Objects
        userPicture=view.findViewById(R.id.userProfilePic);
        //Firebase authentication
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
        final String getUIDCurrentUser=mAuth.getUid();
        //set Recycler View
        messagesRecyclerView=view.findViewById(R.id.list_messagesRecyclerView);
        messagesRecyclerView.setHasFixedSize(true);
        messagesRecyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));
        messagesAdapter=new MessagesAdapter(messagesLists,getActivity());
        messagesRecyclerView.setAdapter(messagesAdapter);
        //get profile picture from realtime database
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //set profile pic to images view.
                assert getUIDCurrentUser != null;
                final  String userProfileUrl = snapshot.child("UserProfile").child(getUIDCurrentUser).child("imageUrl").getValue(String.class);
                if (userProfileUrl!=null) {
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
                assert getUIDCurrentUser != null;
                for (DataSnapshot dataSnapshot : snapshot.child("UserProfile").child(getUIDCurrentUser).child("Connection").child("Match").getChildren()) {
                    final String userUIDMatched = dataSnapshot.getKey();

                    if (!Objects.equals(mAuth.getUid(), userUIDMatched)) {
                        assert userUIDMatched != null;
                        String userName = snapshot.child("UserProfile").child(userUIDMatched).child("lastName").getValue(String.class)+" "+
                                snapshot.child("UserProfile").child(userUIDMatched).child("firstName").getValue(String.class);
                        String userProfileURL = snapshot.child("UserProfile").child(userUIDMatched).child("imageUrl").getValue(String.class);
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

                                            final String getFirstUid=chatKeyDataSnapshot.child("FirstUID").getValue(String.class);
                                            final String getSecondUid =chatKeyDataSnapshot.child("SecondUID").getValue(String.class);

                                            assert getFirstUid != null;
                                            if((getFirstUid.equals(userUIDMatched) && getSecondUid.equals(getUIDCurrentUser)) || (getFirstUid.equals(getUIDCurrentUser) && getSecondUid.equals(userUIDMatched)) ){
                                                getChatKey=chatKeyDataSnapshot.getKey();
                                                for(DataSnapshot chatDataSnapshot:chatKeyDataSnapshot.child("Messages").getChildren()){
                                                    int countMsg = (int)snapshot.child("Chat").child(getChatKey).child("Messages").getChildrenCount();
                                                    int lastMsgSeen =snapshot.child("UserProfile").child(getUIDCurrentUser).child("Connection").child("Match").child(userUIDMatched).getValue(Integer.class);

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

        return view;
    }

}

class MessagesList{
    String userChatKey, userUID, userName, userLastMessage, userProfileURL;
    int seenMessage;

    public MessagesList(String userChatKey, String userUID, String userName, String userLastMessage, String userProfileURL, int seenMessage) {
        this.userChatKey = userChatKey;
        this.userUID = userUID;
        this.userName = userName;
        this.userLastMessage = userLastMessage;
        this.userProfileURL = userProfileURL;
        this.seenMessage = seenMessage;
    }

    public String getUserChatKey() {
        return userChatKey;
    }

    public String getUserUID() {
        return userUID;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserLastMessage() {
        return userLastMessage;
    }

    public String getUserProfileURL() {
        return userProfileURL;
    }

    public int getSeenMessage() {
        return seenMessage;
    }
}