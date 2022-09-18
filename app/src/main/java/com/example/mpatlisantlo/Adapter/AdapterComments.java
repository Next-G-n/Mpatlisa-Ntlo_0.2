package com.example.mpatlisantlo.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mpatlisantlo.R;
import com.example.mpatlisantlo.models.ModelComments;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterComments extends  RecyclerView.Adapter<AdapterComments.MyHolder> {
    Context context;
    List<ModelComments>commentsList;

    public AdapterComments(Context context, List<ModelComments> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //bind the row-comments layout
        View view= LayoutInflater.from(context).inflate(R.layout.row_comments,parent,false);


        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get the data
        String uid=commentsList.get(position).getUid();
        String email=commentsList.get(position).getuEmail();
        String name=commentsList.get(position).getuName();
        String comments=commentsList.get(position).getComment();
        String image=commentsList.get(position).getuDp();
        String pTimeStamp=commentsList.get(position).getTimestamp();
        String cid=commentsList.get(position).getcId();

        Calendar calendar=Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime= DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();


        //set the data
        holder.nameTv.setText(name);
        holder.commentTv.setText(comments);
        holder.timeTv.setText(pTime);
        try {
            Picasso.get().load(image).placeholder(R.drawable.ic_user_icon).into(holder.avatarIv);


        }catch (Exception e){
            Picasso.get().load(R.drawable.ic_user_icon).into(holder.avatarIv);

        }
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    class  MyHolder extends RecyclerView.ViewHolder{
        ImageView avatarIv;
        TextView nameTv,commentTv,timeTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            avatarIv=itemView.findViewById(R.id.avatarIv);
            nameTv=itemView.findViewById(R.id.nameTV);
            timeTv=itemView.findViewById(R.id.timeTV);
            commentTv=itemView.findViewById(R.id.commentTv);

        }
    }
}

