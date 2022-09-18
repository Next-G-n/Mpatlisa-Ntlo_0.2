package com.example.mpatlisantlo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mpatlisantlo.Adapter.AdapterChat;
import com.example.mpatlisantlo.models.ModelChat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
private Toolbar toolbar;
private RecyclerView recyclerView;
private ImageView profileIV;
private TextView name,user_status;
private EditText message;
private ImageButton send_btn;
FirebaseAuth fAuth;
String hisUid;
String myUid;
String hisImage;
FirebaseDatabase firebaseDatabase;
DatabaseReference usersDbRef;
//checking if use has seen message or not
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;

    List<ModelChat>chatList;
    AdapterChat adapterChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //init view
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        recyclerView=findViewById(R.id.chat_recyclerView);
        profileIV=findViewById(R.id.profile_image);
        name=findViewById(R.id.user_name);
        user_status=findViewById(R.id.user_status);
        message=findViewById(R.id.messageEt);
        send_btn=findViewById(R.id.send_button);

        //layout for RecycleView
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

         Intent intent =getIntent();
         hisUid=intent.getStringExtra("hisUid");

        fAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        usersDbRef=firebaseDatabase.getReference("Users");
        //search user to get that user's info
        final Query userUser=usersDbRef.orderByChild("uid").equalTo(hisUid);
        //get user picture and name
        userUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until required info is received
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String name2=""+ds.child("User_Name").getValue();
                    hisImage=""+ds.child("User_Image").getValue();
                    String typingStatus=""+ds.child("typingTo").getValue();
                    String onlineStatus=""+ds.child("onlineStatus").getValue();

                    //check typing status and onlineStatus
                    if(typingStatus.equals(myUid)){
                        user_status.setText("typing...");
                    }else {
                        if(onlineStatus.equals("online")){
                            user_status.setText(onlineStatus);
                        }else {
                            //convert timestamp to proper time data
                            Calendar cal=Calendar.getInstance(Locale.ENGLISH) ;
                            cal.setTimeInMillis(Long.parseLong(onlineStatus));

                            String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
                            user_status.setText("Last seen at:"+dateTime);

                        }
                    }

                    //set data
                    name.setText(name2);
                    try{

                        Picasso.get().load(hisImage).placeholder(R.drawable.ic_user_icon).into(profileIV);

                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_user_icon).into(profileIV);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //click to send message
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get text from edit text
                String message2=message.getText().toString().trim();
                //check if text is empty or not
                if (TextUtils.isEmpty(message2)){
                    //text empty 
                    Toast.makeText(ChatActivity.this, "Cannot send the empty message...", Toast.LENGTH_SHORT).show();
                }else {
                    //text not empty
                    sendMessage(message2);
                }
            }
        });

        //check edit text  change listener
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    checkTypingStatus("noOne");
                }
                else{
                    checkTypingStatus(hisUid);//uid of receiver
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        readMessages();
        seenMessage();

    }

    private void seenMessage() {
        userRefForSeen=FirebaseDatabase.getInstance().getReference("chat");
        seenListener=userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid)&&chat.getSender().equals(hisUid)){
                        HashMap<String, Object>hasSeenMap=new HashMap<>();
                        hasSeenMap.put("isSeen",true);
                        ds.getRef().updateChildren(hasSeenMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages() {
        chatList=new ArrayList<>();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelChat chat=ds.getValue(ModelChat.class);
                    if(chat.getReceiver().equals(myUid)&&chat.getSender().equals(hisUid)||chat.getReceiver().equals(hisUid)&&chat.getSender().equals(myUid)){
                        chatList.add(chat);
                    }
                    adapterChat=new AdapterChat(ChatActivity.this,chatList,hisImage);
                    adapterChat.notifyDataSetChanged();
                    //set adapter to recycleview
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String message2) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();

        String timestamp= String.valueOf(System.currentTimeMillis());

        HashMap<String, Object>hashMap=new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message2);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen",false);
        databaseReference.child("Chats").push().setValue(hashMap);
        //reset edit text after sending message
        message.setText("");
    }

    private void checkOnlineStatus(String status){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String ,Object>hashMap=new HashMap<>();
        hashMap.put("onlineStatus",status);
        //update value of onlineStatus of current user
        dbRef.updateChildren(hashMap);

    }

    private void checkTypingStatus(String typing){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users").child(myUid);
        HashMap<String ,Object>hashMap=new HashMap<>();
        hashMap.put("typingTo",typing);
        //update value of onlineStatus of current user
        dbRef.updateChildren(hashMap);

    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user=fAuth.getCurrentUser();
        if(user !=null){
            myUid=user.getUid();//currently signed in user's uid

        }else{
            startActivity(new Intent(this,MainPageActivity.class));
            finish();
        }

    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timeStamp= String.valueOf(System.currentTimeMillis());
        //set offline with last seen time stamp
        checkOnlineStatus(timeStamp);
        checkTypingStatus("noOne");
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }
}
