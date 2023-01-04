package com.example.tinderforit;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private List<ChatList> chatLists;
    private Context context;
    private FirebaseAuth mAuth;

    public ChatAdapter(List<ChatList> chatLists, Context context) {
        this.chatLists = chatLists;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        ChatList list = chatLists.get(position);
        if (list.getFromUID().equals(mAuth.getUid())) {
            if(list.getType().equals("pic"))
            {
                holder.l_myLayout.setVisibility(View.GONE);
                holder.l_oppLayout.setVisibility(View.GONE);
                holder.lp_myLayout.setVisibility(View.VISIBLE);
                holder.lp_oppLayout.setVisibility(View.GONE);
                Picasso.get().load(list.getMsg()).into(holder.img_myMessage);
                holder.txtp_myMessageTime.setText(String.format("%s %s", list.getDateSend(), list.getTimeSend()));
            }
            else
            {
                holder.l_myLayout.setVisibility(View.VISIBLE);
                holder.l_oppLayout.setVisibility(View.GONE);
                holder.lp_myLayout.setVisibility(View.GONE);
                holder.lp_oppLayout.setVisibility(View.GONE);
                holder.txt_myMessage.setText(list.getMsg());
                holder.txt_myMessageTime.setText(String.format("%s %s", list.getDateSend(), list.getTimeSend()));
            }
        } else {
            if(list.getType().equals("pic"))
            {
                holder.l_myLayout.setVisibility(View.GONE);
                holder.l_oppLayout.setVisibility(View.GONE);
                holder.lp_myLayout.setVisibility(View.GONE);
                holder.lp_oppLayout.setVisibility(View.VISIBLE);
                Picasso.get().load(list.getMsg()).into(holder.img_oppMessage);
                holder.txtp_myMessageTime.setText(String.format("%s %s", list.getDateSend(), list.getTimeSend()));
            }
            else
            {
                holder.l_myLayout.setVisibility(View.GONE);
                holder.l_oppLayout.setVisibility(View.VISIBLE);
                holder.lp_myLayout.setVisibility(View.GONE);
                holder.lp_oppLayout.setVisibility(View.GONE);
                holder.txt_oppMessage.setText(list.getMsg());
                holder.txt_oppMessageTime.setText(String.format("%s %s", list.getDateSend(), list.getTimeSend()));
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatLists.size();
    }

    public void updateChatList(List<ChatList> chatLists) {
        this.chatLists = chatLists;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout l_oppLayout, l_myLayout, lp_oppLayout, lp_myLayout;
        private TextView txt_oppMessage, txt_myMessage;
        private ImageView img_oppMessage, img_myMessage;
        private TextView txt_oppMessageTime, txt_myMessageTime, txtp_oopMessageTime, txtp_myMessageTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            l_oppLayout = itemView.findViewById(R.id.l_oppLayout);
            l_myLayout = itemView.findViewById(R.id.l_myLayout);
            lp_oppLayout = itemView.findViewById(R.id.lp_oppLayout);
            lp_myLayout = itemView.findViewById(R.id.lp_myLayout);
            txt_oppMessage = itemView.findViewById(R.id.txt_oppMessage);
            txt_myMessage = itemView.findViewById(R.id.txt_myMessage);
            img_myMessage = itemView.findViewById(R.id.pic_myMessage);
            img_oppMessage = itemView.findViewById(R.id.pic_oppMessage);
            txt_oppMessageTime = itemView.findViewById(R.id.txt_oppMessageTime);
            txt_myMessageTime = itemView.findViewById(R.id.txt_myMessageTime);
            txtp_myMessageTime = itemView.findViewById(R.id.txtp_myMessageTime);
            txtp_oopMessageTime = itemView.findViewById(R.id.txtp_oppMessageTime);
        }
    }

}
