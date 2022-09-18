package com.example.mpatlisantlo.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpatlisantlo.ChatActivity;
import com.example.mpatlisantlo.ThereProfileActivity;
import com.example.mpatlisantlo.models.ModelUser;
import com.example.mpatlisantlo.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUser extends RecyclerView.Adapter<AdapterUser.MyHolder>{
    Context context;
    List<ModelUser>userList;

    public AdapterUser(Context context, List<ModelUser> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout(row_user.xml)
        View view= LayoutInflater.from(context).inflate(R.layout.row_user, parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        final String hisUID=userList.get(position).getUid();
        String image =userList.get(position).getUser_Image();
        String name=userList.get(position).getUser_Name();
         final String useremail=userList.get(position).getEmail();

        //set data
        holder.nameTV.setText(name);
        holder.emailTV.setText(useremail);
        try{
            Picasso.get().load(image)
                    .placeholder(R.drawable.ic_user_icon)
                    .into(holder.avatarOth);
        }catch (Exception e){
            Log.d("TAG", "onBindViewHolder: error with loading image"+e.getMessage());
            Picasso.get().load(R.drawable.ic_user_icon);

        }

        //handle item click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid",hisUID);
                context.startActivity(intent);
                 */


                //show dialog
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Profile", "chat"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            //profile clicked
                                Intent intent=new Intent(context, ThereProfileActivity.class);
                                intent.putExtra("uid",hisUID);
                                context.startActivity(intent);

                        }
                        if(which==1){
                            //chat clicked
                            Intent intent=new Intent(context, ChatActivity.class);
                            intent.putExtra("hisUid",hisUID);
                            context.startActivity(intent);

                        }

                    }
                });
                builder.create().show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder{
        ImageView avatarOth;
        TextView nameTV,emailTV;



        public MyHolder(@NonNull View itemView) {
            super(itemView);

            avatarOth=itemView.findViewById(R.id.Other_user_avatar);
            nameTV=itemView.findViewById(R.id.Other_user_name);
            emailTV=itemView.findViewById(R.id.Other_user_email);
        }
    }
}
