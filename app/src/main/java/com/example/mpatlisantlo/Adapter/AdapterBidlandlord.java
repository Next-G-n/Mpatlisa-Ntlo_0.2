package com.example.mpatlisantlo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpatlisantlo.ChatActivity;
import com.example.mpatlisantlo.PostDetailActivity;
import com.example.mpatlisantlo.R;
import com.example.mpatlisantlo.models.ModelBids;
import com.example.mpatlisantlo.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterBidlandlord  extends RecyclerView.Adapter<AdapterBidlandlord.MyHolder>{
    Context context;
    List<ModelBids> bidstList;
    List<ModelPost> postList;
    String myUid,spinner;

    private DatabaseReference likeRef;//for likes database mode
    private DatabaseReference postRef;//for of post
    boolean mProcessLike=false;
    int limit =0;




    public AdapterBidlandlord(Context context, List<ModelBids> bidsList,List<ModelPost> postList) {
        this.context = context;
        this.bidstList = bidsList;
        this.postList= postList;
        myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        likeRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        ///Intent intent=getIntent();
       // spinner=intent.getStringExtra("PropertyType");
    }



    @NonNull
    @Override
    public AdapterBidlandlord.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.top_bid_landlord,parent,false);

        return new AdapterBidlandlord.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterBidlandlord.MyHolder holder, final int position) {
        //int limit = 0;

        final String user_name=bidstList.get(position).getBid_Username();
        final String uDp=bidstList.get(position).getBid_Dp();
        final String bidsId=bidstList.get(position).getBidsId();
        final String postId=bidstList.get(position).getPostID();
        final String userId=bidstList.get(position).getBid_UserId();
        final String request=bidstList.get(position).getRequest();
        //final  String qq=postList.get(position).getResponse();

        Calendar calendar=Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(bidsId));
        final String pTime= DateFormat.format("dd/MM/yyyy", calendar).toString();

        final float price= Float.parseFloat(bidstList.get(position).getBid_Price());
        final float price1=price/1000;


        final String uid=bidstList.get(position).getBid_UserId();

        DatabaseReference reference2= FirebaseDatabase.getInstance().getReference("Posts").child("Rent");
        Query query=reference2.orderByChild(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()) {
                    //get data
                    String ownerID = "" + ds.child("OwnerID").getValue();
                    String response = "" + ds.child("Response").getValue();

                    if(response.equals("Sold")){
                        if (request.equals("Accepted")){
                            holder.username.setText(user_name);
                            holder.date.setText(pTime);
                            if(price1>=1) {
                                String price2=String.valueOf(price1);
                                holder.pricetag.setText(price2+"K");

                            }else {
                                final String price2=bidstList.get(position).getBid_Price();
                                holder.pricetag.setText("P "+price2);
                            }
                            try{
                                Picasso.get().load(uDp).placeholder(R.drawable.ic_user_icon).into(holder.user_avator);

                            }catch (Exception e){

                            }
                            holder.acceptBtn.setText("Accepted");
                        } else{
                            holder.username.setText(user_name);
                            holder.date.setText(pTime);
                            if(price1>=1) {
                                String price2=String.valueOf(price1);
                                holder.pricetag.setText(price2+"K");

                            }else {
                                final String price2=bidstList.get(position).getBid_Price();
                                holder.pricetag.setText("P "+price2);
                            }
                            try{
                                Picasso.get().load(uDp).placeholder(R.drawable.ic_user_icon).into(holder.user_avator);

                            }catch (Exception e){
                            }
                            holder.acceptBtn.setVisibility(View.GONE);
                        }

                    }else if (response.equals("Sent")){
                        holder.username.setText(user_name);
                        holder.date.setText(pTime);
                        if(price1>=1) {
                            String price2=String.valueOf(price1);
                            holder.pricetag.setText(price2+"K");

                        }else {
                            final String price2=bidstList.get(position).getBid_Price();
                            holder.pricetag.setText("P "+price2);
                        }
                        try{
                            Picasso.get().load(uDp).placeholder(R.drawable.ic_user_icon).into(holder.user_avator);

                        }catch (Exception e){

                        }
                        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //Intent intent=new Intent(context, PostDetailActivity.class);
                                // intent.putExtra("uid",myUid);
                                //context.startActivity(intent);

                                //Toast.makeText(context, "Works", Toast.LENGTH_SHORT).show();
                                holder.acceptBtn.setText("Accepted");
                                HashMap<String,Object> result=new HashMap<>();
                                result.put("OwnerID",userId);
                                result.put("Response","Sold");
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                                ref.child("Rent").child(postId).updateChildren(result);

                                HashMap<String,Object> result2=new HashMap<>();
                                result2.put("Request","Accepted");
                                DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Posts").child("Rent").child(postId).child("Bids");
                                ref2.child("TopBids").child(bidsId).updateChildren(result2);
                            }
                        });
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.user_avator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("postId",uid);//will get details of the post using this id,its id of the post clicked
                context.startActivity(intent);
            }
        });



    }



    @Override
    public int getItemCount() {

        return bidstList.size();

    }


    class MyHolder extends RecyclerView.ViewHolder{

        //views from topbids
        ImageView user_avator;
        TextView username,date,pricetag;
        Button acceptBtn;



        public MyHolder(@NonNull View itemView) {
            super(itemView);
            //init view
            user_avator=itemView.findViewById(R.id.avatarIv22);
            username=itemView.findViewById(R.id.nameTV22);
            date=itemView.findViewById(R.id.timeTV22);
            pricetag=itemView.findViewById(R.id.uBid22);
            acceptBtn=itemView.findViewById(R.id.accept_btn222);

        }
    }
}