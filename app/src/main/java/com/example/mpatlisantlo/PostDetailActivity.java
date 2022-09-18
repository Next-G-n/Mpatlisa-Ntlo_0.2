package com.example.mpatlisantlo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mpatlisantlo.Adapter.AdapterBid;
import com.example.mpatlisantlo.Adapter.AdapterBidlandlord;
import com.example.mpatlisantlo.Adapter.AdapterComments;
import com.example.mpatlisantlo.Adapter.AdapterImageView;
import com.example.mpatlisantlo.models.ModelBids;
import com.example.mpatlisantlo.models.ModelComments;
import com.example.mpatlisantlo.models.ModelPost;
import com.example.mpatlisantlo.notifications.Data;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.mpatlisantlo.R.drawable.ic_liked;

public class PostDetailActivity extends AppCompatActivity {

    ImageView uPictureIv,pImage,cAvatarIv,user_icon,likeBtn,chat_image;
    TextView nameTv,pTimeTv,pTitleTv,pDescriptionTv,pLikeTv,pCommentTv,pdRegion,pdLocation,pdPrice;
    ImageButton moreBtn,sentBtn,acceptBtn,call;
    Button Optbtn,placeBid_btn;
    LinearLayout profileLayout,profileLayout2,landlordView2;
    CardView landlordView;
    EditText commentEt;
    //to get details of user and post
    String myUid,myEmail,myName,myDp,postId,spinner,pLikes,hisDp,hisUid,hisName,pImage2,OwnerId ;
    boolean mProcessComment=false;
    boolean mProcessLike=false;
    ProgressDialog pd;
    RecyclerView recyclerView,recyclerView2,recyclerView3;
    List<ModelComments>commentsList;
    ArrayList <String>imageUrls;
    AdapterBidlandlord adapterBidlandlord;
    AdapterComments adapterComments;
    AdapterImageView adapterImageView;
    List<ModelBids> bidsList;
    List<ModelPost> postList;
    AdapterBid adapterBid;
    BottomNavigationView navigationView_top;

    Dialog dialog;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        call=findViewById(R.id.contactImage);
        setContentView(R.layout.activity_post_detail);
        uPictureIv=findViewById(R.id.uPictureIV);
        pImage=findViewById(R.id.postRecycleImageView);
        nameTv=findViewById(R.id.uName);
        pTimeTv=findViewById(R.id.pTimeTV);
        pDescriptionTv=findViewById(R.id.about_house2);
       // pTitleTv=findViewById(R.id.pTitleTV);
      // pLikeTv=findViewById(R.id.pLikesTV);
        moreBtn=findViewById(R.id.more_button);
        sentBtn=findViewById(R.id.sendBtn);
       likeBtn=findViewById(R.id.Like_button);
        Optbtn=findViewById(R.id.btnchange);
        commentEt=findViewById(R.id.commentET);
        cAvatarIv=findViewById(R.id.cAvatarIV);
        profileLayout=findViewById(R.id.profileLayout);
        profileLayout2=findViewById(R.id.profileLayout3);
        landlordView=findViewById(R.id.landlordView);
        //pCommentTv=findViewById(R.id.commentTV);
       // acceptBtn=findViewById(R.id.accept_btn22);
        pdLocation=findViewById(R.id.pdLocation);
        pdPrice=findViewById(R.id.pdPrice);
        pdRegion=findViewById(R.id.pdRegion);
        recyclerView=findViewById(R.id.recycleView_Comment);

        navigationView_top=findViewById(R.id.navigationView_topBack);
        user_icon=findViewById(R.id.User_Icon2);
        chat_image=findViewById(R.id.chatImage);
        dialog=new Dialog(PostDetailActivity.this);
        final AlertDialog.Builder dialog2=new AlertDialog.Builder(PostDetailActivity.this);

        navigationView_top.setOnNavigationItemSelectedListener(selectedListener);
        //recyclerView.setNestedScrollingEnabled(false);
       // recyclerView2=findViewById(R.id.postRecycleImageView);
        //get id of post using intent
        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");
        spinner=intent.getStringExtra("PropertyType");
        OwnerId=intent.getStringExtra("uid");

        //Intent intent1=getIntent();
        //biddername=intent1.getStringExtra("userID");
        //biddersdp=intent1.getStringExtra("uDp");

        loadComments();

        loadPostInfo();
        checkUserStatues();
        loadUserInfo();
       setLike();
        sentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

       // likeBtn.setVisibility(View.VISIBLE);


        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });




        //more   button handle
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions();
            }
        });

        //accept are users request and close the bids
     /*  acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptBid();
            }
        });*/





    }



    private void loadImage() {
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        //set layout to recycleview
        recyclerView2.setLayoutManager(layoutManager);
        //init comments list
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(layoutManager);
        loadUserInfo();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts").child(spinner).child(postId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageUrls.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    int count=1;
                    while(ds.hasChild("Image"+count)){
                        String name=""+ds.child("Image"+count).getValue();
                       imageUrls.add(name);
                        try{
                            Picasso.get().load(name).placeholder(R.drawable.ic_user_icon).into(pImage);

                        }catch (Exception e) {

                        }

                        adapterImageView=new AdapterImageView(getApplicationContext(),imageUrls);
                        recyclerView2.setAdapter(adapterImageView);
                        count=count+1;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void loadComments() {
        LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        //set layout to recycleview

        recyclerView.setLayoutManager(layoutManager);

        //init comments list
        commentsList=new ArrayList<>();
        //path of the post,to get its comments
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts").child(spinner).child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelComments modelComments=ds.getValue(ModelComments.class);
                    commentsList.add(modelComments);

                    adapterComments=new AdapterComments(getApplicationContext(),commentsList);
                    recyclerView.setAdapter(adapterComments);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMoreOptions() {
        PopupMenu popupMenu=new PopupMenu(this,moreBtn, Gravity.END);
        //show delete option in only posts of currently signed in user
        if (hisUid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE,0,0,"Delete");
            popupMenu.getMenu().add(Menu.NONE,1,0,"Edit");
        }
        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id=item.getItemId();
                if(id==0){
                    beginDelete();
                }else if(id==1){
                    //edit is clicked
                    //start AddPostActivity with key "editPost" and the id of the post clicked
                    Intent intent=new Intent(PostDetailActivity.this, AddPostActivity.class);
                    intent.putExtra("key","editPost");
                    intent.putExtra("editPostId",postId);
                    startActivity(intent);
                }
                return false;
            }
        });
        //show menu
        popupMenu.show();
    }

    private void beginDelete() {
        if(pImage.equals("noImage")){
            //post is without image
            deleteWithoutImage();
        }else {
            deleteWithImage();

        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener= new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.backButton:
                            //actionBar.setTitle("Log Out");
                            finish();
                            return true;


                    }

                    return false;
                }
            };

    private void deleteWithImage() {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Delete...");
        StorageReference picRef= FirebaseStorage.getInstance().getReferenceFromUrl(pImage2);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //image delete from database
                Query fquery=FirebaseDatabase.getInstance().getReference("Posts").child(spinner).orderByChild("pId").equalTo(postId);
                fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ds.getRef().removeValue();//remove value from firebase where pid matches

                        }
                        Toast.makeText(PostDetailActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PostDetailActivity.this,HomeFragment.class));
                        finish();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed, cant go further
                pd.dismiss();
                Toast.makeText(PostDetailActivity.this, "Error when delete Image "+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void deleteWithoutImage() {
        final ProgressDialog pd=new ProgressDialog(this);
        pd.setMessage("Delete...");

        Query fquery=FirebaseDatabase.getInstance().getReference("Posts").child(spinner).orderByChild("pId").equalTo(postId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue();//remove value from firebase where pid matches

                }
                Toast.makeText(PostDetailActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLike() {
        final DatabaseReference likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(spinner).child(postId).hasChild(myUid)){
                    //user has liked this post
                  //  likeBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(ic_liked,0,0,0);
                   // likeBtn.setImageDrawable(ic_liked);
                    try{
                        Picasso.get().load(ic_liked).placeholder(R.drawable.ic_liked).into(likeBtn);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_like_black).into(cAvatarIv);
                    }
                    //likeBtn.setText("Liked");

                }else {
                    //user has not liked this post
                    //likeBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_like_black,0,0,0);
                    //Picasso.get().load(R.drawable.ic_like_black).into(likeBtn);
                    try{
                        Picasso.get().load(R.drawable.ic_like_black).placeholder(R.drawable.ic_like_black).into(likeBtn);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_like_black).into(cAvatarIv);
                    }
                  //  likeBtn.setText("Like");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void likePost() {

        mProcessLike=true;
        //get id of the post click
        final DatabaseReference likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(mProcessLike){
                    if(dataSnapshot.child(spinner).child(postId).hasChild(myUid)){
                        //already like, so remove
                        postRef.child(spinner).child(postId).child("pLike").setValue(""+(Integer.parseInt(pLikes)-1));
                        likeRef.child(spinner).child(postId).child(myUid).removeValue();
                        mProcessLike=false;


                    }else {
                        //not like, like it
                        postRef.child(spinner).child(postId).child("pLike").setValue(""+(Integer.parseInt(pLikes)+1));
                        likeRef.child(spinner).child(postId).child(myUid).setValue("Liked");//set any value
                        mProcessLike=false;



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postComment() {
        pd=new ProgressDialog(this);
        pd.setMessage("Adding Comment...");
        final String comment=commentEt.getText().toString().trim();
        if (TextUtils.isEmpty(comment)){
            Toast.makeText(this, "comment is empty...", Toast.LENGTH_SHORT).show();
            return;
        }else{
        String timestamp=String.valueOf(System.currentTimeMillis());
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts").child(spinner).child(postId).child("Comments");
        HashMap<String, Object>hashMap=new HashMap<>();
        hashMap.put("cId",timestamp);
        hashMap.put("comment",comment);
        hashMap.put("uid",myUid);
        hashMap.put("uEmail",myEmail);
        hashMap.put("uDp",myDp);
        hashMap.put("timestamp",timestamp);
        hashMap.put("uName",myName);
        reference.child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                      //added
                        pd.dismiss();
                        Toast.makeText(PostDetailActivity.this, "Comment Added...", Toast.LENGTH_SHORT).show();
                        commentEt.setText("");
                        updateCommentCount();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed, not added


                pd.dismiss();
                Toast.makeText(PostDetailActivity.this, "Error adding comment..."+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        }
    }



    private void updateCommentCount() {
        //whenever user adds comment increase the comment count
        mProcessComment=true;
        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts").child(spinner).child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(mProcessComment){
                    String comments=""+dataSnapshot.child("pComments").getValue();
                    int newCommentVal=Integer.parseInt(comments)+1;
                    ref.child("pComments").setValue(""+newCommentVal);
                    mProcessComment=false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserInfo() {
        //get user info
        Optbtn.setText(spinner);
        Query myRef= FirebaseDatabase.getInstance().getReference("Users");
        myRef.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    myName=""+ds.child("User_Name").getValue();
                    myDp=""+ds.child("User_Image").getValue();

                    try{
                        Picasso.get().load(myDp).placeholder(R.drawable.ic_user_icon).into(cAvatarIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.ic_user_icon).into(cAvatarIv);
                    }
                    try{
                        Picasso.get().load(myDp).placeholder(R.drawable.ic_user_profile_foreground).into(user_icon);

                    }catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void loadPostInfo() {

        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts").child(spinner);
        Query query=reference.orderByChild("pId").equalTo(postId);
        query.keepSynced(true);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //keep checking the posts until get the required post
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    //get data
                    String pTitle=""+ds.child("pTitle").getValue();
                    String pDescr=""+ds.child("pDescr").getValue();
                    pLikes=""+ds.child("pLike").getValue();
                    String pTimeStamp=""+ds.child("pTime").getValue();
                   pImage2=""+ds.child("pImage").getValue();
                    hisDp=""+ds.child("uDp").getValue();
                    hisUid=""+ds.child("uid").getValue();
                    String location=""+ds.child("pLocation").getValue();
                    final String price=""+ds.child("Price").getValue();
                    String region=""+ds.child("pRegion").getValue();
                    String pEmail=""+ds.child("uEmail").getValue();
                    hisName=""+ds.child("uName").getValue();
                    String commentCount=""+ds.child("pComments").getValue();
                    String Response1=""+ds.child("Response").getValue();
                    //spinner=""+ds.child("PropertyType").getValue();


                    Calendar calendar=Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime= DateFormat.format("EEE d MMM yyyy 'at' HH:mm", calendar).toString();


                    //set data
                    pDescriptionTv.setText(pDescr);
                    //pLikeTv.setText(pLikes+" Likes");
                    pTimeTv.setText(pTime);
                    //pCommentTv.setText(commentCount+" Comments");
                    nameTv.setText(hisName);
                    pdLocation.setText(location);
                    pdPrice.setText("BWP "+price);
                    pdRegion.setText(region);
                    //set imge
                   // if (pImage.equals("noImage")){
                        //hide imageView
                       // pImage.setVisibility(View.GONE);
                  //  }else {

                       // pImage.setVisibility(View.VISIBLE);
                       try{
                            Picasso.get().load(pImage2).placeholder(R.drawable.ic_user_icon).into(pImage);

                        }catch (Exception e) {

                       }

                   // }

                    try{
                        Picasso.get().load(hisDp).placeholder(R.drawable.ic_user_icon).into(uPictureIv);

                    }catch(Exception e){
                        Picasso.get().load(R.drawable.ic_user_icon).into(uPictureIv);
                    }
                    if(!hisUid.equals(myUid)){
                       // loadPostInfo();
                        chat_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(PostDetailActivity.this, ChatActivity.class);
                                intent.putExtra("hisUid",hisUid);
                                startActivity(intent);

                            }
                        });

                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intentCall = new Intent(Intent.ACTION_CALL);
                                intentCall.putExtra("hisUid",hisUid);
                              /*  telNum=phone_number.getText().toString();
                                if (telNum.trim().isEmpty()){
                                    intentCall.setData(Uri.parse("tel:567788"));
                                    Toast.makeText(getApplicationContext(),"Number is empty", Toast.LENGTH_SHORT).show();
                                } else if (!telNum.trim().isEmpty()){
                                    intentCall.setData(Uri.parse("tel:"+ telNum));
                                    Toast.makeText(getApplicationContext(),"OKya", Toast.LENGTH_SHORT).show();
                                    requestPermision();
                                }*/
                              requestPermision();
                                startActivity(intentCall);


                            }
                        });

                        if(Response1.equals("none")){
                            Optbtn.setText(spinner);
                            Optbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    HashMap<String,Object> result=new HashMap<>();
                                    result.put("Response","Sent");
                                   DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                    ref.child(spinner).child(postId).updateChildren(result);

                                    String timestamp=String.valueOf(System.currentTimeMillis());
                                    HashMap<String,Object> result2=new HashMap<>();
                                    result2.put("BidsId",timestamp);
                                    result2.put("Original_Price",price);
                                    result2.put("OgUsername",myName);
                                    result2.put("OgDp",myDp);
                                    result2.put("OgUserId",myUid);
                                    result2.put("PostID",postId);
                                    result2.put("Request","not");
                                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Posts");
                                    ref2.child(spinner).child(postId).child("Bids").child(timestamp).updateChildren(result2);
                                }
                            });
                        }else if(Response1.equals("Sent")){

                            loadPostInfo();
                            Optbtn.setText("bid");
                            Optbtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    showPopUP();
                                }
                            });


                        }

                    }else {

                        profileLayout2.setVisibility(View.GONE);
                        landlordView.setVisibility(View.VISIBLE);
                        Optbtn.setVisibility(View.GONE);
                        if(Response1.equals("none")){
                            landlordView.setVisibility(View.GONE);
                        }

                       // loadPostInfo();
                        else if(Response1.equals("Sent")){
                            landlordPopUp();


                        }else if(Response1.equals("Sold")){

                            final ImageView dp22=findViewById(R.id.avatarIv44);

                            final TextView username22=findViewById(R.id.nameTV44);
                            final TextView price22=findViewById(R.id.uBid44);
                            final Button accept=findViewById(R.id.accept_btn233);


                            Query query=FirebaseDatabase.getInstance().getReference("Posts").child(spinner).child(postId).child("Bids").orderByChild("BidsId");
                            query.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //check until require data get
                                    for (DataSnapshot ds:dataSnapshot.getChildren()) {
                                        //get data
                                        String original_price = "" + ds.child("Original_Price").getValue();
                                        String ogUsername = "" + ds.child("OgUsername").getValue();
                                        String ogDp = "" + ds.child("OgDp").getValue();
                                        final String UserId=""+ds.child("OgUserId").getValue();
                                        final String bidsId=""+ds.child("BidsId").getValue();
                                        String request=""+ds.child("Request").getValue();
                                        if (request.equals("Accepted")){
                                            accept.setText("Accepted");
                                            accept.setVisibility(View.VISIBLE);

                                            username22.setText(ogUsername);
                                            price22.setText(original_price);
                                            try {
                                                Picasso.get().load(ogDp).placeholder(R.drawable.ic_user_icon).into(dp22);

                                            } catch (Exception e) {

                                            }


                                        recyclerView3=findViewById(R.id.recycleView_Bids2);
                                        LinearLayoutManager layoutManager4=new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
                                        layoutManager4.setStackFromEnd(true);
                                        layoutManager4.setReverseLayout(true);

                                        recyclerView3.setLayoutManager(layoutManager4);
                                        //init comments list
                                        bidsList=new ArrayList<>();
                                        //path of the post,to get its comments
                                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts").child(spinner).child(postId).child("Bids").child("TopBids");
                                        Query query2=ref.orderByChild("Bid_Price");
                                        query2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                bidsList.clear();
                                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                                    ModelBids modelBids=ds.getValue(ModelBids.class);
                                                    bidsList.add(modelBids);

                                                    adapterBidlandlord=new AdapterBidlandlord(getApplicationContext(),bidsList,postList);
                                                    recyclerView3.setAdapter(adapterBidlandlord);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }else{
                                            accept.setVisibility(View.GONE);
                                            landlordPopUp();
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.d("TAG", "onCancelled: error displaying"+databaseError.getMessage());

                                }
                            });


                        }

                    }

                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    public void landlordPopUp(){
        final ImageView dp22=findViewById(R.id.avatarIv44);

        final TextView username22=findViewById(R.id.nameTV44);
        final TextView price22=findViewById(R.id.uBid44);
        final Button accept=findViewById(R.id.accept_btn233);

        Query query=FirebaseDatabase.getInstance().getReference("Posts").child(spinner).child(postId).child("Bids").orderByChild("BidsId");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until require data get
                for (DataSnapshot ds:dataSnapshot.getChildren()) {
                    //get data
                    String original_price = "" + ds.child("Original_Price").getValue();
                    String ogUsername = "" + ds.child("OgUsername").getValue();
                    String ogDp = "" + ds.child("OgDp").getValue();
                    final String UserId=""+ds.child("OgUserId").getValue();
                    final String bidsId=""+ds.child("BidsId").getValue();
                    String request=""+ds.child("Request").getValue();


                    username22.setText(ogUsername);
                    price22.setText(original_price);
                    try {
                        Picasso.get().load(ogDp).placeholder(R.drawable.ic_user_icon).into(dp22);

                    } catch (Exception e) {

                    }

                    accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            HashMap<String,Object> result=new HashMap<>();
                            //result.put("Response","Accepted");
                            result.put("OwnerID",UserId);
                            result.put("Response","Sold");
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

                            HashMap<String,Object> result2=new HashMap<>();
                            ref.child(spinner).child(postId).updateChildren(result);
                            result2.put("Request","Accepted");
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Posts").child("Rent").child(postId).child("Bids");
                            ref2.child(bidsId).updateChildren(result2);


                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "onCancelled: error displaying"+databaseError.getMessage());

            }
        });
        recyclerView3=findViewById(R.id.recycleView_Bids2);
        LinearLayoutManager layoutManager4=new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        layoutManager4.setStackFromEnd(true);
        layoutManager4.setReverseLayout(true);

        recyclerView3.setLayoutManager(layoutManager4);
        //init comments list
        bidsList=new ArrayList<>();
        //path of the post,to get its comments
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts").child(spinner).child(postId).child("Bids").child("TopBids");
        Query query2=ref.orderByChild("Bid_Price");
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bidsList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelBids modelBids=ds.getValue(ModelBids.class);
                    bidsList.add(modelBids);

                    adapterBidlandlord=new AdapterBidlandlord(getApplicationContext(),bidsList,postList);
                    recyclerView3.setAdapter(adapterBidlandlord);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void showPopUP(){
        final TextView close_btn,username22,price22;
        final ImageView dp22;
        final EditText enterbid;
        Button Btn_accept;


        dialog.setContentView(R.layout.bidding);
        //Btn_accept=(Button)dialog.findViewById(R.id.accept_btn222);
        enterbid=(EditText)dialog.findViewById(R.id.enter_bid);
        placeBid_btn=(Button)dialog.findViewById(R.id.place_bid);
        close_btn=(TextView)dialog.findViewById(R.id.close_btn);
        dp22=(ImageView)dialog.findViewById(R.id.avatarIv33);
        username22=(TextView)dialog.findViewById(R.id.nameTV33);
        price22=(TextView)dialog.findViewById(R.id.uBid33);
        recyclerView3=(RecyclerView)dialog.findViewById(R.id.recycleView_Bids);
       // Btn_accept.setVisibility(View.GONE);

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        DatabaseReference reference2= FirebaseDatabase.getInstance().getReference("Posts").child(spinner).child(postId).child("Bids");
        Query query=reference2.orderByChild("BidsId");
       query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    //get data
                    String original_price = "" + ds.child("Original_Price").getValue();
                    String ogUsername = "" + ds.child("OgUsername").getValue();
                    String ogDp = "" + ds.child("OgDp").getValue();

                    price22.setText(original_price);
                    username22.setText(ogUsername);

                    try {
                        Picasso.get().load(ogDp).placeholder(R.drawable.ic_user_icon).into(dp22);

                    } catch (Exception e) {


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

                            LinearLayoutManager layoutManager4 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            layoutManager4.setStackFromEnd(true);
                            layoutManager4.setReverseLayout(true);

                            //   LinearLayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
                            recyclerView3.setLayoutManager(layoutManager4);
                            //init comments list
                            bidsList = new ArrayList<>();
                            //path of the post,to get its comments
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(spinner).child(postId).child("Bids").child("TopBids");
                            Query query2 = ref.orderByChild("Bid_Price");
                            query2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    bidsList.clear();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        ModelBids modelBids = ds.getValue(ModelBids.class);
                                        bidsList.add(modelBids);

                                        adapterBid = new AdapterBid(getApplicationContext(), bidsList, postList);
                                        recyclerView3.setAdapter(adapterBid);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

       placeBid_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {


               final String name2=enterbid.getText().toString();
               int name3=Integer.parseInt(name2);
               String price11=price22.getText().toString();
               int price111=Integer.parseInt(price11);


               if(TextUtils.isEmpty(name2)){
                   enterbid.setError("Please enter your bid ......");

               }else if(name3<=price111){
                   enterbid.setError("Please enter higher bid ......");
               }else{
                   String timestamp=String.valueOf(System.currentTimeMillis());
                   HashMap<String,Object> result2=new HashMap<>();
                   result2.put("BidsId",timestamp);
                   result2.put("Bid_Price",name2);
                   result2.put("Bid_Username",myName);
                   result2.put("Bid_Dp",myDp);
                   result2.put("Bid_UserId",myUid);
                   result2.put("PostID",postId);
                   result2.put("Request","not");
                   DatabaseReference ref3 = FirebaseDatabase.getInstance().getReference("Posts").child(spinner).child(postId);
                   ref3.child("Bids").child("TopBids").child(timestamp).updateChildren(result2);
                   enterbid.setText("");
               }
           }
       });

       dialog.show();

    }
    public void acceptBid(){
        HashMap<String,Object> result=new HashMap<>();
        result.put("OwnerID",OwnerId);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.child(spinner).child(postId).updateChildren(result);
    }
    public void loadbids(){
        final ImageView dp22=(ImageView)findViewById(R.id.avatarIv33);
        final TextView username22=(TextView)findViewById(R.id.nameTV33);
        final TextView price22=(TextView)findViewById(R.id.uBid33);

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts").child(spinner).child(postId).child("Bids");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    //get data
                    String original_price = "" + ds.child("Original_Price").getValue();
                    String ogUsername = "" + ds.child("OgUsername").getValue();
                    String ogDp = "" + ds.child("OgDp").getValue();

                    price22.setText(original_price);
                    username22.setText(ogUsername);

                    try {
                        Picasso.get().load(ogDp).placeholder(R.drawable.ic_user_icon).into(dp22);

                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void checkUserStatues(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            //user is signed in
            myEmail=user.getEmail();
            myUid=user.getUid();
        }else
        {
            //User not signed in, go to the log in pages
            startActivity(new Intent(PostDetailActivity.this,LoginActivity.class));
            finish();
        }
    }

    public void requestPermision(){
        ActivityCompat.requestPermissions(PostDetailActivity.this, new String[]{ Manifest.permission.CALL_PHONE},1);
    }




}
