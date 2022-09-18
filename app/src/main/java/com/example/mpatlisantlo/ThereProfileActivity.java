package com.example.mpatlisantlo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mpatlisantlo.Adapter.AdapterPosts;
import com.example.mpatlisantlo.models.ModelPost;
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
import java.util.List;

public class ThereProfileActivity extends AppCompatActivity {


    private TextView user_name,phone_number,email,location;
    private ImageView current_image,cover_image;
    String telNum;
    ImageButton chat,call;

    RecyclerView postsRecycleView;

    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    String uid;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String myUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);

        user_name=findViewById(R.id.user_name2);
        phone_number=findViewById(R.id.user_phoneNumber);
        email=findViewById(R.id.user_email2);
        //cover_image=view.findViewById(R.id.CoverPhoto);
        current_image=findViewById(R.id.User_image2);
        location=findViewById(R.id.user_location2);
        chat=findViewById(R.id.chatImage2);
        call=findViewById(R.id.contactImage);
        postsRecycleView=findViewById(R.id.recycleView_post);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCall = new Intent(Intent.ACTION_CALL);
                telNum=phone_number.getText().toString();
                if (telNum.trim().isEmpty()){
                    intentCall.setData(Uri.parse("tel:567788"));
                    Toast.makeText(getApplicationContext(),"Number is empty", Toast.LENGTH_SHORT).show();
                } else if (!telNum.trim().isEmpty()){
                    intentCall.setData(Uri.parse("tel:"+ telNum));
                    Toast.makeText(getApplicationContext(),"OKya", Toast.LENGTH_SHORT).show();
                    requestPermision();
                }else {
                    startActivity(intentCall);
                }
            }
        });

        //get uid of clicked user to retrieve his post
        Intent intent=getIntent();
        uid=intent.getStringExtra("uid");
        //checkUserStatus();

        //firebase
        firebaseAuth=FirebaseAuth.getInstance();



        Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until require data get
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //get data
                    String name=""+ds.child("User_Name").getValue();
                    String user_email=""+ds.child("Email").getValue();
                    String phone=""+ds.child("Phone_Number").getValue();
                    String Location=""+ds.child("Location").getValue();
                    String user_image=""+ds.child("User_Image").getValue();

                    //set data

                    user_name.setText(name);
                    location.setText(Location);
                    email.setText(user_email);

                    try{
                        Picasso.get().load(user_image).into(current_image);
                    }catch (Exception e){
                        //if there is any execption  getting image then set default
                        Picasso.get().load(R.drawable.ic_user_icon);
                    }
                    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                    if (user!=null){
                        //user is signed in
                        myUid=user.getUid();


                    }

                    if(!uid.equals(myUid)){
                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(ThereProfileActivity.this, ChatActivity.class);
                                intent.putExtra("hisUid",uid);
                                startActivity(intent);


                            }
                        });

                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(ThereProfileActivity.this, ChatActivity.class);
                                intent.putExtra("hisUid",uid);
                                startActivity(intent);


                                
                            }
                        });
                     /*
                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intentCall = new Intent(Intent.ACTION_CALL);
                                intentCall.putExtra("hisUid",uid);
                                telNum=phone_number.getText().toString();
                                if (telNum.trim().isEmpty()){
                                    intentCall.setData(Uri.parse("tel:567788"));
                                    Toast.makeText(getApplicationContext(),"Number is empty", Toast.LENGTH_SHORT).show();
                                } else if (!telNum.trim().isEmpty()){
                                    intentCall.setData(Uri.parse("tel:"+ telNum));
                                    Toast.makeText(getApplicationContext(),"OKya", Toast.LENGTH_SHORT).show();
                                    requestPermision();
                                }
                                startActivity(intentCall);


                            }
                        });*/
                   /*     public void onClick(View v) {
                            Intent intentCall = new Intent(Intent.ACTION_CALL);
                            telNum=phone_number.getText().toString();
                            if (telNum.trim().isEmpty()){
                                intentCall.setData(Uri.parse("tel:567788"));
                                Toast.makeText(getApplicationContext(),"Number is empty", Toast.LENGTH_SHORT).show();
                            } else if (!telNum.trim().isEmpty()){
                                intentCall.setData(Uri.parse("tel:"+ telNum));
                                Toast.makeText(getApplicationContext(),"OKya", Toast.LENGTH_SHORT).show();
                                requestPermision();
                            }else {
                                startActivity(intentCall);
                            }
                        }*/
                    }else {
                        chat.setVisibility(View.GONE);
                        call.setVisibility(View.GONE);
                    }





                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "onCancelled: error displaying"+databaseError.getMessage());

            }
        });

        postList=new ArrayList<>();
        loadHisPost();

    }

    private void loadHisPost() {
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        //show newest post first,for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recycle view
        postsRecycleView.setLayoutManager(layoutManager);

        //init post list
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
        //qurey to load post
        Query query=ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelPost modelPost=ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                    adapterPosts=new AdapterPosts(ThereProfileActivity.this,postList);
                    postsRecycleView.setAdapter(adapterPosts);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ThereProfileActivity.this, "Error "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private  void checkUserStatus(){


        user=firebaseAuth.getCurrentUser();
        if(user !=null){

        }else{
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
    }
    public void requestPermision(){
        ActivityCompat.requestPermissions(ThereProfileActivity.this, new String[]{ Manifest.permission.CALL_PHONE},1);
    }

}
