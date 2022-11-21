package com.example.letschat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.letschat.Adapters.MessagesAdapter;
import com.example.letschat.Models.Message;
import com.example.letschat.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class    ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    MessagesAdapter adapter;
    ArrayList<Message> messages;

    String senderRoom,recieverRoom;

    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        messages=new ArrayList<>();
        adapter=new MessagesAdapter(this,messages);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        String name=getIntent().getStringExtra("name");
        String  recieverUid=getIntent().getStringExtra("uid");
        String senderUid= FirebaseAuth.getInstance().getUid();

        senderRoom =senderUid+recieverUid;
        recieverRoom=recieverUid+senderUid;

        database=FirebaseDatabase.getInstance();

        database.getReference().child("chats")
                        .child(senderRoom)
                        .child("messages")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                messages.clear();
                                for(DataSnapshot snapshot1: snapshot.getChildren()){
                                    Message message=snapshot1.getValue(Message.class);
                                    messages.add(message);
                                }

                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messsageTxt=binding.messageBox.getText().toString();

                Date date=new Date();
                Message message=new Message(messsageTxt,senderUid,date.getTime());
                binding.messageBox.setText("");

                    database.getReference().child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .push()
                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    database.getReference().child("chats")
                                            .child(recieverRoom)
                                            .child("messages")
                                            .push()
                                            .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                }
                                            });
                                }
                            });
            }
        });

        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}