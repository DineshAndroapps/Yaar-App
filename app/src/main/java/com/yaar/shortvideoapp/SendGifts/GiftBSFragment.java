package com.yaar.shortvideoapp.SendGifts;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yaar.shortvideoapp.Main_Menu.MainMenuActivity;
import com.yaar.shortvideoapp.R;
import com.yaar.shortvideoapp.SimpleClasses.ApiRequest;
import com.yaar.shortvideoapp.SimpleClasses.Callback;
import com.yaar.shortvideoapp.SimpleClasses.Variables;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GiftBSFragment extends BottomSheetDialogFragment {


    ArrayList<giftmodel> giftmodels = new ArrayList<>();
    RecyclerView rvEmoji;

    public GiftBSFragment() {
        // Required empty public constructor
    }

    private StickerListener mStickerListener;

    public void setStickerListener(StickerListener stickerListener) {
        mStickerListener = stickerListener;
    }

    public interface StickerListener {
        void onStickerClick(String bitmap);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sticker_emoji_dialog, null);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        final CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
         rvEmoji = contentView.findViewById(R.id.rvEmoji);

        ((View) contentView.findViewById(R.id.Goback)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BottomSheetBehavior) behavior).setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        Call_Api_For_get_AllGifts();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvEmoji.setLayoutManager(gridLayoutManager);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public class StickerAdapter extends RecyclerView.Adapter<StickerAdapter.ViewHolder> {

        ArrayList<giftmodel> giftmodels;
        GiftBSFragment giftBSFragment;

        public StickerAdapter(GiftBSFragment giftBSFragment, ArrayList<giftmodel> giftmodels) {
            this.giftmodels = giftmodels;
            this.giftBSFragment = giftBSFragment;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_sticker, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

//            Glide.with(giftBSFragment).load(giftmodels.get(position).getImage()).placeholder(R.drawable.image_placeholder).into(holder.imgSticker);
            Picasso.get().load(giftmodels.get(position).getImage()).placeholder(R.drawable.image_placeholder).into(holder.imgSticker);



        }

        @Override
        public int getItemCount() {
            return giftmodels.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgSticker;
            TextView txtprice;

            ViewHolder(View itemView) {
                super(itemView);
                imgSticker = itemView.findViewById(R.id.imgSticker);
                txtprice = itemView.findViewById(R.id.txtprice);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mStickerListener != null) {
                            mStickerListener.onStickerClick(giftmodels.get(getAdapterPosition()).getId());
                        }
                        dismiss();
                    }
                });
            }
        }
    }

    private void Call_Api_For_get_AllGifts() {


        Log.d(Variables.tag, MainMenuActivity.token);


        JSONObject parameters = new JSONObject();
        try {
//            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id,"0"));
            parameters.put("token", MainMenuActivity.token);
//            parameters.put("type",type);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(getActivity(), Variables.GiftList, parameters, new Callback() {
            @Override
            public void Responce(String resp) {

                Parse_data(resp);
            }
        });



    }

    public void Parse_data(String responce){

        giftmodels.clear();
        Log.e("tag","Gifts List : "+ responce);

        try {
            JSONObject jsonObject=new JSONObject(responce);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msgArray=jsonObject.getJSONArray("List");

                for (int i = 0; i <msgArray.length() ; i++) {

                    JSONObject jsonObject1 = msgArray.getJSONObject(i);
                    giftmodel giftmodel = new giftmodel();

                    giftmodel.setId(jsonObject1.getString("id"));
                    giftmodel.setName(jsonObject1.getString("name"));
                    giftmodel.setImage(Variables.GiftListPATH + jsonObject1.getString("image"));

                    giftmodels.add(giftmodel);
                }

                StickerAdapter stickerAdapter = new StickerAdapter(this,giftmodels);
                rvEmoji.setAdapter(stickerAdapter);

            }else {
                Toast.makeText(getActivity(), ""+jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }
}