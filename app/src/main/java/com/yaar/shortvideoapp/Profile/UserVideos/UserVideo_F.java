package com.yaar.shortvideoapp.Profile.UserVideos;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yaar.shortvideoapp.Home.Home_Get_Set;
import com.yaar.shortvideoapp.Profile.MyVideos_Adapter;
import com.yaar.shortvideoapp.R;
import com.yaar.shortvideoapp.SimpleClasses.ApiRequest;
import com.yaar.shortvideoapp.SimpleClasses.Callback;
import com.yaar.shortvideoapp.SimpleClasses.Functions;
import com.yaar.shortvideoapp.SimpleClasses.Variables;
import com.yaar.shortvideoapp.Video_Recording.Video_Recoder_A;
import com.yaar.shortvideoapp.WatchVideos.WatchVideos_F;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserVideo_F extends Fragment {

    public RecyclerView recyclerView;
    ArrayList<Home_Get_Set> data_list;
    MyVideos_Adapter adapter;
    View view;
    Context context;
    String user_id;
    TextView whoops;

    LinearLayout no_data_layout;
    public static int myvideo_count = 0;
    NewVideoBroadCast mReceiver;
    String paidstatus = "0";

    public UserVideo_F() {

    }


    boolean is_my_profile = true;

    @SuppressLint("ValidFragment")
    public UserVideo_F(boolean is_my_profile, String user_id, String paidstatus) {
        this.is_my_profile = is_my_profile;
        this.user_id = user_id;
        this.paidstatus = paidstatus;
    }


    private class NewVideoBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Variables.Reload_my_videos_inner = false;
            Call_Api_For_get_Allvideos();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_video, container, false);

        context = getContext();


        recyclerView = view.findViewById(R.id.recylerview);
        whoops = view.findViewById(R.id.whoops);
        final GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        whoops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_permissions()) {

                    //  previewCamera(Variables.Selected_sound_id, "name"); //open advanced camera

                    Functions.make_directry(Variables.app_folder);
                    Functions.make_directry(Variables.draft_app_folder);

                    if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {

                        Intent intent = new Intent(getActivity(), Video_Recoder_A.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                    } else {
                        Toast.makeText(context, "You have to login First", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        data_list = new ArrayList<>();
        adapter = new MyVideos_Adapter(context, data_list, paidstatus, new MyVideos_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, Home_Get_Set item, View view) {


                OpenWatchVideo(postion);


            }
        });

        recyclerView.setAdapter(adapter);

        no_data_layout = view.findViewById(R.id.no_data_layout);


        Call_Api_For_get_Allvideos();


        mReceiver = new NewVideoBroadCast();
        getActivity().registerReceiver(mReceiver, new IntentFilter("newVideo"));

        return view;

    }

    Boolean isVisibleToUser = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        if (view != null && isVisibleToUser) {
            Call_Api_For_get_Allvideos();
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean check_permissions() {

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(context, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, 2);
        } else {

            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((view != null && isVisibleToUser) && !is_api_run) {
            Call_Api_For_get_Allvideos();
        } else if ((view != null && Variables.Reload_my_videos_inner) && !is_api_run) {
            Variables.Reload_my_videos_inner = false;
            Call_Api_For_get_Allvideos();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }


    Boolean is_api_run = false;

    //this will get the all videos data of user and then parse the data
    private void Call_Api_For_get_Allvideos() {
        is_api_run = true;
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("my_fb_id", Variables.sharedPreferences.getString(Variables.u_id, ""));
            if (is_my_profile) {
                parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id, ""));
            } else
                parameters.put("fb_id", user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.showMyAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                is_api_run = false;
                Parse_data(resp);
            }
        });


    }

    public void Parse_data(String responce) {
        Log.d("TAG_userVideo", "Parse_data: " + responce);
        data_list.clear();

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                JSONObject data = msgArray.getJSONObject(0);
                JSONObject user_info = data.optJSONObject("user_info");


                JSONArray user_videos = data.getJSONArray("user_videos");
                if (!user_videos.toString().equals("[" + "0" + "]")) {

                    no_data_layout.setVisibility(View.GONE);

                    for (int i = 0; i < user_videos.length(); i++) {
                        JSONObject itemdata = user_videos.optJSONObject(i);

                        Home_Get_Set item = new Home_Get_Set();
                        item.fb_id = user_id;

                        item.first_name = user_info.optString("first_name");
                        item.last_name = user_info.optString("last_name");
                        item.profile_pic = user_info.optString("profile_pic");
                        item.verified = user_info.optString("verified");


                        Log.d("resp", item.fb_id + " " + item.first_name);

                        JSONObject count = itemdata.optJSONObject("count");
                        item.like_count = count.optString("like_count");
                        item.video_comment_count = count.optString("video_comment_count");
                        item.views = count.optString("view");
                        Log.d("resp_views", count.optString("view"));
                        JSONObject sound_data = itemdata.optJSONObject("sound");
                        item.sound_id = sound_data.optString("id");
                        item.sound_name = sound_data.optString("sound_name");
                        item.sound_pic = sound_data.optString("thum");
                        if (sound_data != null) {
                            JSONObject audio_path = sound_data.optJSONObject("audio_path");
                            item.sound_url_mp3 = audio_path.optString("mp3");
                            item.sound_url_acc = audio_path.optString("acc");
                        }


                        item.video_id = itemdata.optString("id");
                        item.liked = itemdata.optString("liked");
                        item.gif = itemdata.optString("gif");
                        item.video_url = itemdata.optString("video");
                        item.paidtype = user_info.optString("paid");
                        item.thum = itemdata.optString("thum");
                        item.created_date = itemdata.optString("created");

                        item.video_description = itemdata.optString("description");


                        data_list.add(item);
                    }

                    myvideo_count = data_list.size();

                } else {
                    no_data_layout.setVisibility(View.VISIBLE);
                }


                adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void OpenWatchVideo(int postion) {

        Intent intent = new Intent(getActivity(), WatchVideos_F.class);
        intent.putExtra("arraylist", data_list);
        intent.putExtra("position", postion);
        startActivity(intent);

    }


}
