package com.yaar.shortvideoapp.SoundLists;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yaar.shortvideoapp.R;
import com.yaar.shortvideoapp.SimpleClasses.Variables;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by AQEEL on 3/20/2018.
 */

public class Sounds_Adapter extends RecyclerView.Adapter<Sounds_Adapter.CustomViewHolder> {
    public Context context;

    ArrayList<Sound_catagory_Get_Set> datalist;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, Sounds_GetSet item);
    }

    public Sounds_Adapter.OnItemClickListener listener;

    public Sounds_Adapter(Context context, ArrayList<Sound_catagory_Get_Set> arrayList, Sounds_Adapter.OnItemClickListener listener) {
        this.context = context;
        datalist = arrayList;
        this.listener = listener;
    }


    @Override
    public Sounds_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_sound_layout_utils, viewGroup, false);
        Sounds_Adapter.CustomViewHolder viewHolder = new Sounds_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }


    @Override
    public void onBindViewHolder(final Sounds_Adapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        Sound_catagory_Get_Set item = datalist.get(i);
        holder.title.setText(item.catagory);

        holder.img_arrow.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24));

        if (item.isExpanded) {
            holder.recyclerView.setVisibility(View.VISIBLE);
            holder.img_arrow.setRotation(0);
        } else {
            holder.recyclerView.setVisibility(View.GONE);
            holder.img_arrow.setRotation(-90);
        }

        holder.rel_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (item.isExpanded) {

                    item.isExpanded = false;
                    holder.recyclerView.setVisibility(View.GONE);
                    holder.img_arrow.setRotation(-90);

                } else {
                    item.isExpanded = true;
                    holder.recyclerView.setVisibility(View.VISIBLE);
                    holder.img_arrow.setRotation(0);

                }

            }
        });

        Sound_Items_Adapter adapter = new Sound_Items_Adapter(context, item.sound_list, new Sound_Items_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, Sounds_GetSet item) {
                listener.onItemClick(view, postion, item);
            }
        });

        GridLayoutManager gridLayoutManager;
        gridLayoutManager = new GridLayoutManager(context, item.sound_list.size());

        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.recyclerView.setLayoutManager(gridLayoutManager);
        holder.recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.findSnapView(gridLayoutManager);
        snapHelper.attachToRecyclerView(holder.recyclerView);


    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        RecyclerView recyclerView;
        ImageView img_arrow;
        View rel_header;

        public CustomViewHolder(View view) {
            super(view);
            //  image=view.findViewById(R.id.image);
            img_arrow = view.findViewById(R.id.img_arrow);
            rel_header = view.findViewById(R.id.rel_header);
            title = view.findViewById(R.id.title);
            recyclerView = view.findViewById(R.id.horizontal_recylerview);


        }


    }


}


class Sound_Items_Adapter extends RecyclerView.Adapter<Sound_Items_Adapter.CustomViewHolder> {
    public Context context;

    ArrayList<Sounds_GetSet> datalist;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, Sounds_GetSet item);
    }

    public Sound_Items_Adapter.OnItemClickListener listener;


    public Sound_Items_Adapter(Context context, ArrayList<Sounds_GetSet> arrayList, Sound_Items_Adapter.OnItemClickListener listener) {
        this.context = context;
        datalist = arrayList;
        this.listener = listener;
    }

    @Override
    public Sound_Items_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sound_layout_utils,viewGroup,false);
        view.setLayoutParams(new RecyclerView.LayoutParams(Variables.screen_width-50, RecyclerView.LayoutParams.WRAP_CONTENT));
        Sound_Items_Adapter.CustomViewHolder viewHolder = new Sound_Items_Adapter.CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return datalist.size();
    }


    @Override
    public void onBindViewHolder(final Sound_Items_Adapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        Sounds_GetSet item = datalist.get(i);
        try {

            holder.bind(i, datalist.get(i), listener);

            holder.sound_name.setText(item.sound_name);
            holder.description_txt.setText(item.description);

            if (item.fav.equals("1"))
                holder.fav_btn.setImageDrawable(context.getDrawable(R.drawable.ic_my_favourite));
            else
                holder.fav_btn.setImageDrawable(context.getDrawable(R.drawable.ic_my_un_favourite));


            if (item.thum.equals("")) {
                item.thum = "Null";
            }


            if (item.thum != null && !item.thum.equals("")) {
                Log.d(Variables.tag, item.thum);
                Uri uri = Uri.parse(item.thum);
                holder.sound_image.setImageURI(uri);
            }else {
                Uri uri = Uri.parse("android.resource://ads.megaplay/drawable/app_icon");
                holder.sound_image.setImageURI(uri);

            }


        } catch (Exception e) {

        }

    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageButton done, fav_btn;
        TextView sound_name, description_txt,txtAdd;
        SimpleDraweeView sound_image;

        public CustomViewHolder(View view) {
            super(view);

            done = view.findViewById(R.id.done);
            txtAdd = view.findViewById(R.id.txtAdd);
            fav_btn = view.findViewById(R.id.fav_btn);


            sound_name = view.findViewById(R.id.sound_name);
            description_txt = view.findViewById(R.id.description_txt);
            sound_image = view.findViewById(R.id.sound_image);

        }

        public void bind(final int pos, final Sounds_GetSet item, final Sound_Items_Adapter.OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, pos, item);
                }
            });

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, pos, item);
                }
            });

            txtAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, pos, item);
                }
            });


            fav_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, pos, item);
                }
            });

        }


    }


}


