package com.example.mpatlisantlo.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpatlisantlo.AddPostActivity;
import com.example.mpatlisantlo.PostDetailActivity;
import com.example.mpatlisantlo.R;
import com.example.mpatlisantlo.ThereProfileActivity;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPostLiked extends RecyclerView.Adapter<AdapterPostLiked.MyHolder> {
    Context context;
    List<ModelPost> postList;
    String myUid;

    private DatabaseReference likeRef;//for likes database mode
    private DatabaseReference postRef;//for of post
    boolean mProcessLike=false;
    int limit =0;



    public AdapterPostLiked(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        likeRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
    }



    @NonNull
    @Override
    public AdapterPostLiked.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_post,parent,false);

        return new AdapterPostLiked.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterPostLiked.MyHolder holder, final int position) {
        //int limit = 0;

            String ploaction=postList.get(position).getpLocation();
            String uDp=postList.get(position).getuDp();
        final String PropertyType=postList.get(position).getPropertyType();
        String owner=postList.get(position).getOwnerID();
        String response=postList.get(position).getResponse();
        final String uid=postList.get(position).getUid();



           if(response.equals("Sold")){
               if(myUid.equals(uid) || myUid.equals(owner)){
                   final float price= Float.parseFloat(postList.get(position).getPrice());
                   float price1=price/1000;

                   final String pImage=postList.get(position).getpImage();


                   holder.plocation.setText(ploaction);

                   if(price1>=1) {
                       String price2=String.valueOf(price1);
                       holder.pricetag.setText(price2+"K");

                   }else {
                       final String price2=postList.get(position).getPrice();
                       holder.pricetag.setText("P "+price2);
                   }
                   try{
                       Picasso.get().load(uDp).placeholder(R.drawable.ic_user_icon).into(holder.uPictureIV);

                   }catch (Exception e){

                   }
                   try{
                       Picasso.get().load(pImage).into(holder.pImagetIV);

                   }catch (Exception e) {

                   }
               }else{
                   holder.itemView.setVisibility(View.GONE);
                   holder.layout.setLayoutParams(holder.params);
               }

           }else{
               final float price= Float.parseFloat(postList.get(position).getPrice());
               float price1=price/1000;

               final String pImage=postList.get(position).getpImage();


               holder.plocation.setText(ploaction);

               if(price1>=1) {
                   String price2=String.valueOf(price1);
                   holder.pricetag.setText(price2+"K");

               }else {
                   final String price2=postList.get(position).getPrice();
                   holder.pricetag.setText("P "+price2);
               }
               try{
                   Picasso.get().load(uDp).placeholder(R.drawable.ic_user_icon).into(holder.uPictureIV);

               }catch (Exception e){

               }
               try{
                   Picasso.get().load(pImage).into(holder.pImagetIV);

               }catch (Exception e) {

               }
           }


        final String pid=postList.get(position).getpId();

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
            // uNameTv=itemView.findViewById(R.id.uName2);
            plocation=itemView.findViewById(R.id.pLocation);
            pTime=itemView.findViewById(R.id.pTimeTV);
            //  pTitleTV=itemView.findViewById(R.id.pTitleTV);
            //   pDescription=itemView.findViewById(R.id.pDescriptionTV);
            //   pLikesTv=itemView.findViewById(R.id.pLikesTV);
            like_btn=itemView.findViewById(R.id.Like_button);
            // comment_btn=itemView.findViewById(R.id.Comment_button);
            //share_btn=itemView.findViewById(R.id.Share_button);
            more_btn=itemView.findViewById(R.id.more_button);
            //   pCommentTV=itemView.findViewById(R.id.commentTV);
            profileLayout=itemView.findViewById(R.id.profileLayout);
            // chat_image=itemView.findViewById(R.id.chatImage);
            params = new RelativeLayout.LayoutParams(0, 0);
            layout=itemView.findViewById(R.id.postLayerView2);

        }
    }
}
