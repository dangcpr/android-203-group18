package com.example.tinderforit.messages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tinderforit.R;
import com.squareup.picasso.Picasso;

import java.util.List;
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder>{

    private List<MessagesList> messagesLists;
    private final Context context;
    public MessagesAdapter(List<MessagesList> messagesLists, Context context) {
        this.messagesLists = messagesLists;
        this.context = context;
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public MessagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_adapter,null));
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MyViewHolder holder, int position) {
        MessagesList list=messagesLists.get(position);

        if(!list.getUserProfileURL().isEmpty()){
            Picasso.get().load(list.getUserProfileURL()).into(holder.ptr_userProfile);
        }
        holder.txt_username.setText(list.getUserName());
        holder.txt_lastMessage.setText(list.getUserLastMessage());
        holder.txt_seenMassage.setText(String.valueOf(list.getSeenMessage()));
        if(list.getSeenMessage()==0){
            holder.txt_seenMassage.setVisibility(View.GONE);
            holder.txt_lastMessage.setTextColor(context.getResources().getColor(R.color.light_primary_30));
        }
        else{
            holder.txt_seenMassage.setVisibility(View.VISIBLE);
            holder.txt_lastMessage.setTextColor(context.getResources().getColor(R.color.primary));
        }

        holder.l_rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return messagesLists.size();
    }
    public void updateMessagesList(List<MessagesList> messagesLists){
        this.messagesLists=messagesLists;
    }
    static class MyViewHolder extends RecyclerView.ViewHolder {
        final ImageView ptr_userProfile;
        final private TextView txt_username;
        final private TextView txt_lastMessage;
        final private TextView txt_seenMassage;
        final private LinearLayout l_rootLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ptr_userProfile=itemView.findViewById(R.id.ptr_userProfile);
            txt_username=itemView.findViewById(R.id.txt_username);
            txt_lastMessage=itemView.findViewById(R.id.txt_lastMessage);
            txt_seenMassage=itemView.findViewById(R.id.txt_unSeenMessage);
            l_rootLayout=itemView.findViewById(R.id.rootLayout);
        }
    }
}