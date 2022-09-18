package com.example.mpatlisantlo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpatlisantlo.PostDetailActivity;
import com.example.mpatlisantlo.R;
import com.example.mpatlisantlo.ThereProfileActivity;
import com.example.mpatlisantlo.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterPostsRecommended extends RecyclerView.Adapter<AdapterPostsRecommended.MyHolder> {
    Context context;
    List<ModelPost> postList;
    String myUid;


    private DatabaseReference likeRef,usersDbRef;//for likes database mode
    private DatabaseReference postRef;//for of post
    FirebaseDatabase firebaseDatabase;
    boolean mProcessLike=false;
    private int limit = 0;




    public AdapterPostsRecommended(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        likeRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef=FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseDatabase=FirebaseDatabase.getInstance();
        usersDbRef=firebaseDatabase.getReference("Users");
        //search user to get that user's info



    }



    @NonNull
    @Override
    public AdapterPostsRecommended.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_post,parent,false);
        return new AdapterPostsRecommended.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterPostsRecommended.MyHolder holder, final int position) {

        final Query userUser=usersDbRef.orderByChild("uid").equalTo(myUid);
        final String PropertyType=postList.get(position).getPropertyType();
        userUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String preferred_location=""+ds.child("Location").getValue();
                    String preferred_price=""+ds.child("User_Preferred_Price").getValue();
                    //String price=""+ds.child("preferred_price").getValue();
//                    float pre_price=Float.parseFloat(preferred_price);
                    String plocation=postList.get(position).getpLocation();
                  //  final float pric= Float.parseFloat(postList.get(position).getPrice());

                        if(preferred_location.equals(plocation) ){
                            //holder.layout.setVisibility(View.VISIBLE);

                            String ploaction=postList.get(position).getpLocation();
                            String uDp=postList.get(position).getuDp();
                            final String price2=postList.get(position).getPrice();
                          //  float price1=price/1000;

                            final String pImage=postList.get(position).getpImage();

                            holder.plocation.setText(ploaction);
                            holder.pricetag.setText("P "+price2);
                          /*  if(price1>=1) {
                                String price2=String.valueOf(price1);
                                holder.pricetag.setText(price2+"K");

                            }else {
                                final String price2=postList.get(position).getPrice();
                                holder.pricetag.setText("P "+price2);
                            }*/
                            try{
                                Picasso.get().load(uDp).placeholder(R.drawable.ic_user_icon).into(holder.uPictureIV);

                            }catch (Exception e){

                            }

                            try{
                                Picasso.get().load(pImage).into(holder.pImagetIV);

                            }catch (Exception e) {

                            }
                            limit=limit+1;

                            holder.setIsRecyclable(false);
                        }else{

                            //if(holder.layout==null){
                            // holder.layout.setVisibility(View.GONE);
                            holder.itemView.setVisibility(View.GONE);
                            holder.layout.setLayoutParams(holder.params);
                            // break;

                            // }

                        }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String pid=postList.get(position).getpId();
        final String uid=postList.get(position).getUid();


        holder.pImagetIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId",pid);//will get details of the post using this id,its id of the post clicked
                intent.putExtra("PropertyType", PropertyType);
                context.startActivity(intent);
            }
        });

        holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ThereProfileActivity.class);
                intent.putExtra("uid",uid);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }



    class MyHolder extends RecyclerView.ViewHolder{

        //views from row_post
        ImageView uPictureIV,pImagetIV,chat_image;
        TextView plocation,pTime,pTitleTV,pDescription,pLikesTv,pCommentTV,pricetag;
        Button like_btn,share_btn;
        ImageButton more_btn;
        LinearLayout profileLayout;
        public RelativeLayout.LayoutParams params;
        RelativeLayout layout;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //init view
            uPictureIV=itemView.findViewById(R.id.uPictureIV2);
            pImagetIV=itemView.findViewById(R.id.pImageIV);
            pricetag=itemView.findViewById(R.id.pricetag);
            plocation=itemView.findViewById(R.id.pLocation);
            pTime=itemView.findViewById(R.id.pTimeTV);
            like_btn=itemView.findViewById(R.id.Like_button);
            more_btn=itemView.findViewById(R.id.more_button);
            profileLayout=itemView.findViewById(R.id.profileLayout);
            params = new RelativeLayout.LayoutParams(0, 0);
            layout=itemView.findViewById(R.id.postLayerView2);

        }
    }


}
