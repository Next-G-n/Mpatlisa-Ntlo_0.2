package com.example.mpatlisantlo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpatlisantlo.ChatActivity;
import com.example.mpatlisantlo.PostDetailActivity;
import com.example.mpatlisantlo.R;
import com.example.mpatlisantlo.ThereProfileActivity;
import com.example.mpatlisantlo.models.ModelBids;
import com.example.mpatlisantlo.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterBid  extends RecyclerView.Adapter<AdapterBid.MyHolder>{
    Context context;
    List<ModelBids> bidstList;
    List<ModelPost> postList;
    String myUid;

    private DatabaseReference likeRef;//for likes database mode
    private DatabaseReference postRef;//for of post
    boolean mProcessLike=false;
    int limit =0;



    public AdapterBid(Context context, List<ModelBids> bidsList, List<ModelPost> postList) {
        this.context = context;
        this.bidstList = bidsList;
        this.postList= postList;
        myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        likeRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
    }



    @NonNull
    @Override
    public AdapterBid.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.topbids,parent,false);

        return new AdapterBid.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterBid.MyHolder holder, final int position) {
        //int limit = 0;

        String user_name=bidstList.get(position).getBid_Username();
        String uDp=bidstList.get(position).getBid_Dp();
        String date2=bidstList.get(position).getBidsId();


        Calendar calendar=Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(date2));
        String pTime= DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();



        final float price= Float.parseFloat(bidstList.get(position).getBid_Price());
        float price1=price/1000;

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
        final String uid=bidstList.get(position).getBid_UserId();

        holder.user_avator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("UserId",uid);//will get details of the post using this id,its id of the post clicked
                context.startActivity(intent);
            }
        });

       // Intent intent=new Intent(context, PostDetailActivity.class);
      //  intent.putExtra("uid",myUid);
        //context.startActivity(intent);

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
