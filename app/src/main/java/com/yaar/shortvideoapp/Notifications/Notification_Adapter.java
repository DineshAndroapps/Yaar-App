package com.yaar.shortvideoapp.Notifications;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaar.shortvideoapp.R;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;


public class Notification_Adapter extends RecyclerView.Adapter<Notification_Adapter.CustomViewHolder > {
    public Context context;

    ArrayList<Notification_Get_Set> datalist;
    public interface OnItemClickListener {
        void onItemClick(View view, int postion, Notification_Get_Set item);
    }

    public Notification_Adapter.OnItemClickListener listener;

    public Notification_Adapter(Context context, ArrayList<Notification_Get_Set> arrayList, Notification_Adapter.OnItemClickListener listener) {
        this.context = context;
        datalist= arrayList;
        this.listener=listener;
    }

    @Override
    public Notification_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification,viewGroup,false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        Notification_Adapter.CustomViewHolder viewHolder = new Notification_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
       return datalist.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView user_image;

        TextView username,message,watch_btn;
        ImageView img_play_heart;

        public CustomViewHolder(View view) {
            super(view);
            user_image=view.findViewById(R.id.user_image);
            username=view.findViewById(R.id.username);
            message=view.findViewById(R.id.message);
          //  watch_btn=view.findViewById(R.id.watch_btn);
            img_play_heart=view.findViewById(R.id.img_play_heart);

        }

        public void bind(final int pos , final Notification_Get_Set item, final Notification_Adapter.OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,pos,item);
                }
            });

            img_play_heart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,pos,item);
                }
            });

        }


    }

    @Override
    public void onBindViewHolder(final Notification_Adapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        final Notification_Get_Set item=datalist.get(i);
        holder.username.setText(item.username);

        if(item.profile_pic!=null && !item.profile_pic.equals("")) {
            Uri uri = Uri.parse(item.profile_pic);
            holder.user_image.setImageURI(uri);
        }

        if(item.type.equalsIgnoreCase("comment_video")){
            holder.message.setText(item.first_name+" have comment on your video");
            holder.img_play_heart.setVisibility(View.VISIBLE);
        }
        else if(item.type.equalsIgnoreCase("video_like")) {
            holder.message.setText(item.first_name + " liked your video");
            holder.img_play_heart.setVisibility(View.VISIBLE);
        }
        else if(item.type.equalsIgnoreCase("following_you")) {
            holder.message.setText(item.first_name + " following you");
            holder.img_play_heart.setVisibility(View.GONE);
        }


        holder.bind(i,datalist.get(i),listener);

}

}