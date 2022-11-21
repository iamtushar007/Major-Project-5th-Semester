package com.example.letschat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.Models.Message;
import com.example.letschat.R;
import com.example.letschat.databinding.ItemRecievedBinding;
import com.example.letschat.databinding.ItemSentBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message>messages;

    final int ITEM_SENT=1;
    final int ITEM_RECIEVE=2;



    public MessagesAdapter(Context context, ArrayList<Message> messages){
        this.context=context;
        this.messages=messages;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SENT){
            View view= LayoutInflater.from(context).inflate(R.layout.item_sent,parent,false);
            return new SentViewHolder(view);
        }else{
            View view= LayoutInflater.from(context).inflate(R.layout.item_recieved,parent,false);
            return new RecieverViewHolder(view);
        }
    }


    @Override
    public int getItemViewType(int position) {
        Message message=messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId())){
            return ITEM_SENT;
        }else{
            return ITEM_RECIEVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message=messages.get(position);
        if(holder.getClass()==SentViewHolder.class){
            SentViewHolder viewHolder=(SentViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());
        }else{
           RecieverViewHolder viewHolder=(RecieverViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder{

        ItemSentBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=ItemSentBinding.bind(itemView);

        }
    }
    public class RecieverViewHolder extends RecyclerView.ViewHolder{

        ItemRecievedBinding binding;
        public RecieverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=ItemRecievedBinding.bind(itemView);

        }
    }

}
