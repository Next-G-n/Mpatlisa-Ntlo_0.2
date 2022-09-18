package com.example.mpatlisantlo.Adapter;

import android.app.DownloadManager;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpatlisantlo.AddPostActivity;
import com.example.mpatlisantlo.ChatActivity;
import com.example.mpatlisantlo.PostDetailActivity;
import com.example.mpatlisantlo.ProfileFragment;
import com.example.mpatlisantlo.R;
import com.example.mpatlisantlo.ThereProfileActivity;
import com.example.mpatlisantlo.models.ModelPost;
import com.example.mpatlisantlo.models.ModelUser;
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

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {
    Context context;
    List<ModelPost>postList;
    String myUid;

    private DatabaseReference likeRef;//for likes database mode
    private DatabaseReference postRef;//for of post
    boolean mProcessLike=false;
    private final int limit = 3;


    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
    }



    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_all_post,parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {

        final String uid=postList.get(position).getUid();
        String uEmail=postList.get(position).getuEmail();
        String uName=postList.get(position).getuName();
        String ploaction=postList.get(position).getpLocation();
        String uDp=postList.get(position).getuDp();
        final String pid=postList.get(position).getpId();
        final String PropertyType=postList.get(position).getPropertyType();
        //final float price= Float.parseFloat(postList.get(position).getPrice());
       // float price1=price/1000;
        String price=postList.get(position).getPrice();

        String pTitle=postList.get(position).getpTitle();
        final String pDescription=postList.get(position).getpDescr();
        final String pImage=postList.get(position).getpImage();


        String pTimeStamp=postList.get(position).getpTime();
        final String pLike=postList.get(position).getpLike();//contains total number like for a post
        String pComments=postList.get(position).getpComments();

       /// Calendar calendar=Calendar.getInstance(Locale.getDefault());
       // calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
      //  String pTime= DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();


        //holder.uNameTv.setText(uName);
 //       holder.pTime.setText(pTime);
    //    holder.pTitleTV.setText(pTitle);
        holder.plocation.setText(ploaction);
    //    holder.pDescription.setText(pDescription);
      //  holder.pLikesTv.setText(pLike+" Likes");//e.g 100 likes
    //    holder.pCommentTV.setText(pComments+" Comments");
      //  SetLikes(holder,pid);

            holder.pricetag.setText("BWP "+price);
        try{
            Picasso.get().load(uDp).placeholder(R.drawable.ic_user_icon).into(holder.uPictureIV);

        }catch (Exception e){

        }
      //  if (pImage.equals("noImage")){
            //hide imageView
            //holder.pImagetIV.setVisibility(View.GONE);
     //   }else {

           // holder.pImagetIV.setVisibility(View.VISIBLE);
            try{
                Picasso.get().load(pImage).into(holder.pImagetIV);

            }catch (Exception e) {

            }
       // }






          /*  holder.more_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMoreOptions(holder.more_btn,uid,myUid,pImage,pid);
                }
            });*/

          /*
            holder.like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int pLikes=Integer.parseInt(postList.get(position).getpLike());
                    mProcessLike=true;
                    //get id of the post click
                    final String postIde=postList.get(position).getpId();
                    likeRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(mProcessLike){
                                if(dataSnapshot.child(postIde).hasChild(myUid)){
                                    //already like, so remove
                                    postRef.child(postIde).child("pLike").setValue(""+(pLikes-1));
                                    likeRef.child(postIde).child(myUid).removeValue();
                                    mProcessLike=false;

                                }else {
                                    //not like, like it.
                                    postRef.child(postIde).child("pLike").setValue(""+(pLikes+1));
                                    likeRef.child(postIde).child(myUid).setValue("Liked");//set any value
                                    mProcessLike=false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });*/


           /* holder.comment_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postId",pid);//will get details of the post using this id,its id of the post clicked
                context.startActivity(intent);
                }
            });*/


        holder.pImagetIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId",pid);//will get details of the post using this id,its id of the post clicked
                intent.putExtra("PropertyType", PropertyType);
                context.startActivity(intent);
            }
        });
/*            holder.share_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Shared", Toast.LENGTH_SHORT).show();
                }
            });*/

            holder.profileLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context, ThereProfileActivity.class);
                    intent.putExtra("uid",uid);
                    context.startActivity(intent);
                }
            });



    }

    private void SetLikes(final MyHolder holder, final String postKey) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postKey).hasChild(myUid)){
                    //user has liked this post
                    holder.like_btn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_liked,0,0,0);
                    holder.like_btn.setText("Liked");

                }else {
                    //user has not liked this post
                    holder.like_btn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_like_black,0,0,0);
                    holder.like_btn.setText("Like");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMoreOptions(ImageButton more_btn, String uid, String myUid, final String pImage, final String pid) {
        //create popup menu
        PopupMenu popupMenu=new PopupMenu(context,more_btn, Gravity.END);
        //show delete option in only posts of currently signed in user
        ///if (uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE,0,0,"Delete");
            popupMenu.getMenu().add(Menu.NONE,1,0,"Edit");
       // }
        popupMenu.getMenu().add(Menu.NONE,2,0,"View Details");
        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id=item.getItemId();
                if(id==0){
                    beginDelete(pid,pImage);
                }else if(id==1){
                     //edit is clicked
                    //start AddPostActivity with key "editPost" and the id of the post clicked
                    Intent intent=new Intent(context, AddPostActivity.class);
                    intent.putExtra("key","editPost");
                    intent.putExtra("editPostId",pid);
                    context.startActivity(intent);
                }else if(id==0){
                    Intent intent=new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postId",pid);//will get details of the post using this id,its id of the post clicked
                    context.startActivity(intent);
                }
                return false;
            }
        });
        //show menu
        popupMenu.show();
    }

    private void beginDelete(String pid, String pImage) {
        //post can be with or without image
        if(pImage.equals("noImage")){
            //post is without image
            deleteWithoutImage(pid);
        }else {
            deleteWithImage(pid,pImage);

        }
    }

    private void deleteWithImage(final String pid, String pImage) {
        //progress bar
        final ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Delete...");
        StorageReference picRef= FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //image delete from database
                Query fquery=FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pid);
                fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                            ds.getRef().removeValue();//remove value from firebase where pid matches

                        }
                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(context, "Error when delete Image "+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void deleteWithoutImage(String pid) {
        final ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Delete...");

        Query fquery=FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pid);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue();//remove value from firebase where pid matches

                }
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        }
    }
}
