package com.example.tinderforit.chat;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinderforit.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<ChatList> chatLists;
    private final Context context;
    private FirebaseAuth mAuth;
    public ChatAdapter(List<ChatList> chatLists, Context context) {
        this.chatLists = chatLists;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter_layout,null));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {
        mAuth=FirebaseAuth.getInstance();
        ChatList list=chatLists.get(position);
        if(list.getFromUID().equals(mAuth.getUid())){
            holder.l_myLayout.setVisibility(View.VISIBLE);
            holder.l_oppLayout.setVisibility(View.GONE);
            holder.txt_myMessage.setText(list.getMsg());
            holder.txt_myMessageTime.setText(String.format("%s %s", list.getDateSend(), list.getTimeSend()));
        }
        else{
            holder.l_myLayout.setVisibility(View.GONE);
            holder.l_oppLayout.setVisibility(View.VISIBLE);
            holder.txt_oppMessage.setText(list.getMsg());
            holder.txt_oppMessageTime.setText(String.format("%s %s", list.getDateSend(), list.getTimeSend()));
        }
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }
    public void updateChatList(List<ChatList> chatLists){
        this.chatLists=chatLists;
    }
    static  class   MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout l_oppLayout, l_myLayout;
        private TextView txt_oppMessage, txt_myMessage;
        private TextView txt_oppMessageTime, txt_myMessageTime;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            l_oppLayout=itemView.findViewById(R.id.l_oppLayout);
            l_myLayout=itemView.findViewById(R.id.l_myLayout);
            txt_oppMessage=itemView.findViewById(R.id.txt_oppMessage);
            txt_myMessage=itemView.findViewById(R.id.txt_myMessage);
            txt_oppMessageTime=itemView.findViewById(R.id.txt_oppMessageTime);
            txt_myMessageTime=itemView.findViewById(R.id.txt_myMessageTime);
        }
    }

}
