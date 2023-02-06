package com.yaar.shortvideoapp.Profile;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaar.shortvideoapp.SimpleClasses.Variables;
import com.yaar.shortvideoapp.Home.Home_Get_Set;
import com.yaar.shortvideoapp.R;
import com.yaar.shortvideoapp.SimpleClasses.Functions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Created by AQEEL on 3/20/2018.
 */

public class MyVideos_Adapter extends RecyclerView.Adapter<MyVideos_Adapter.CustomViewHolder > {

    public Context context;
    private MyVideos_Adapter.OnItemClickListener listener;
    private ArrayList<Home_Get_Set> dataList;
    String paidstatus = "0";



    public interface OnItemClickListener {
        void onItemClick(int postion, Home_Get_Set item, View view);
    }

    public MyVideos_Adapter(Context context, ArrayList<Home_Get_Set> dataList,String paidstatus, MyVideos_Adapter.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;
        this.paidstatus = paidstatus;

    }

    @Override
    public MyVideos_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_myvideo_layout,null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        MyVideos_Adapter.CustomViewHolder viewHolder = new MyVideos_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {


        ImageView thumb_image;
        TextView view_txt;
        View cd_item;

        public CustomViewHolder(View view) {
            super(view);

            cd_item=view.findViewById(R.id.cd_item);
            thumb_image=view.findViewById(R.id.thumb_image);
            view_txt=view.findViewById(R.id.view_txt);

        }

        public void bind(final int position,final Home_Get_Set item, final MyVideos_Adapter.OnItemClickListener listener) {
            cd_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(position,item,view);
                }
            });

        }

    }




    @Override
    public void onBindViewHolder(final MyVideos_Adapter.CustomViewHolder holder, final int i) {
        final Home_Get_Set item= dataList.get(i);
        holder.setIsRecyclable(false);

        if(Variables.sharedPreferences.getString(Variables.u_id,"0").equals(item.fb_id))
        {

            if (item.paidtype == null){

            }else {

            }


        }
        else
        {

        }






        try {


            Picasso.get().load(item.thum).placeholder(R.drawable.image_placeholder) .resize(400,500).centerCrop().into(holder.thumb_image);


        }catch (Exception e){
            Log.d("resp_views_user_error", e.toString());

        }

        Log.d("resp_views_user_ad", item.views);


        holder.view_txt.setText(item.views);
        holder.view_txt.setText(Functions.GetSuffix(item.views));
        holder.bind(i,item,listener);

    }

}