package com.example.letschat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.Models.Message;
import com.example.letschat.R;
import com.example.letschat.databinding.ItemRecievedBinding;
import com.example.letschat.databinding.ItemSentBinding;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter{

    Context context;
    ArrayList<Message>messages;

    final int ITEM_SENT=1;
    final int ITEM_RECIEVE=2;


    String senderRoom;
    String recieverRoom;

    public MessagesAdapter(Context context, ArrayList<Message> messages,String senderRoom,String recieverRoom){
        this.context=context;
        this.messages=messages;
        this.senderRoom=senderRoom;
        this.recieverRoom=recieverRoom;
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

        int reactions[]= new int[]{
                R.drawable.heart,
                R.drawable.like,
                R.drawable.laughing,
                R.drawable.shocked,
                R.drawable.sad,
                R.drawable.angry_face
        };

        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();



        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass()==SentViewHolder.class)
            {
                SentViewHolder viewHolder=(SentViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }else
            {
                RecieverViewHolder viewHolder=(RecieverViewHolder) holder;
                viewHolder.binding.feeling.setImageResource(reactions[pos]);
                viewHolder.binding.feeling.setVisibility(View.VISIBLE);
            }


            message.setFeeling(pos);


            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(senderRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            FirebaseDatabase.getInstance().getReference()
                    .child("chats")
                    .child(recieverRoom)
                    .child("messages")
                    .child(message.getMessageId()).setValue(message);

            return true; // true is closing popup, false is requesting a new selection
        });


        if(holder.getClass()==SentViewHolder.class){
            SentViewHolder viewHolder=(SentViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());


            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });


        }else{
           RecieverViewHolder viewHolder=(RecieverViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());

            viewHolder.binding.message.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {

                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });

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
