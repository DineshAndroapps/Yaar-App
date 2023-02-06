package com.yaar.shortvideoapp.Main_Menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yaar.shortvideoapp.BaseActivity;
import com.yaar.shortvideoapp.Chat.Chat_Activity;
import com.yaar.shortvideoapp.Discover.Discover_Get_Set;
import com.yaar.shortvideoapp.Home.Home_Get_Set;
import com.yaar.shortvideoapp.R;
import com.yaar.shortvideoapp.SimpleClasses.ApiRequest;
import com.yaar.shortvideoapp.SimpleClasses.Callback;
import com.yaar.shortvideoapp.SimpleClasses.Fragment_Callback;
import com.yaar.shortvideoapp.SimpleClasses.Functions;
import com.yaar.shortvideoapp.SimpleClasses.Variables;
import com.yaar.shortvideoapp.WatchVideos.WatchVideos_F;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class MainMenuActivity extends BaseActivity {
    public static MainMenuActivity mainMenuActivity;
    private MainMenuFragment mainMenuFragment;
    long mBackPressed;


    public static String token;

    public static Intent intent;
    String order_id;

    public ArrayList<Discover_Get_Set> datalist;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void deleteCacheData() {
//        File cacheDir = this.getExternalCacheDir();
        File cacheDir = this.getExternalCacheDir();
        String cashFolder = String.valueOf(cacheDir.getAbsoluteFile() + "/video-cache");

        File newCahDir = new File(cashFolder);

        File[] files = newCahDir.listFiles();

        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    public interface onPayment {
        void onClick(String razor_id, int total_amount, String order_id);
    }

    onPayment onPayment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);

        mainMenuActivity = this;

        intent = getIntent();

        String video_id = intent.getStringExtra("video_id");

        if (video_id != null) {
            getVideoDetails(video_id);
        }


        setIntent(null);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Variables.screen_height = displayMetrics.heightPixels;
        Variables.screen_width = displayMetrics.widthPixels;

        Variables.sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);

        Variables.user_id = Variables.sharedPreferences.getString(Variables.u_id, "");
        Variables.user_name = Variables.sharedPreferences.getString(Variables.u_name, "");
        Variables.user_pic = Variables.sharedPreferences.getString(Variables.u_pic, "");


        token = FirebaseInstanceId.getInstance().getToken();
        if (token == null || (token.equals("") || token.equals("null")))
            token = Variables.sharedPreferences.getString(Variables.device_token, "null");

        // if (savedInstanceState == null) {

        Log.d("token", "tokensdsdsd " + token);

        initScreen();

//        } else {
//            mainMenuFragment = (MainMenuFragment) getSupportFragmentManager().getFragments().get(0);
//        }


        Functions.make_directry(Variables.app_showing_folder);
        Functions.make_directry(Variables.app_showing_folder);
        Functions.make_directry(Variables.draft_app_folder);

    }


    public void setCategory(ArrayList<Discover_Get_Set> datalist) {
        mainMenuFragment.setCategory(datalist);
    }


    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            String type = intent.getStringExtra("type");
            if (type != null) {

                if (type.equalsIgnoreCase("message")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Chat_Activity chat_activity = new Chat_Activity(new Fragment_Callback() {
                                @Override
                                public void Responce(Bundle bundle) {

                                }
                            });
                            FragmentTransaction transaction = MainMenuActivity.mainMenuActivity.getSupportFragmentManager().beginTransaction();
                            transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);

                            Bundle args = new Bundle();
                            args.putString("user_id", intent.getStringExtra("user_id"));
                            args.putString("user_name", intent.getStringExtra("user_name"));
                            args.putString("user_pic", intent.getStringExtra("user_pic"));

                            chat_activity.setArguments(args);
                            transaction.addToBackStack(null);
                            transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
                        }
                    }, 2000);
                }


            }

            String video_id = intent.getStringExtra("video_id");

            if (video_id != null) {
                getVideoDetails(video_id);
            }
        }

    }

    private void getVideoDetails(String video_id) {


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("token", Variables.sharedPreferences.getString(Variables.device_token, "Null"));
            parameters.put("video_id", video_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.Call_Api(this, Variables.showAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {

                try {

                    ArrayList<Home_Get_Set> data_list = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(resp);
                    String code = jsonObject.optString("code");
                    if (code.equals("200")) {
                        JSONArray msgArray = jsonObject.getJSONArray("msg");
                        for (int i = 0; i < msgArray.length(); i++) {
                            JSONObject itemdata = msgArray.optJSONObject(i);
                            Home_Get_Set item = new Home_Get_Set();
                            item.fb_id = itemdata.optString("fb_id");

                            JSONObject user_info = itemdata.optJSONObject("user_info");

                            item.username = user_info.optString("username");
                            item.first_name = user_info.optString("first_name", getResources().getString(R.string.app_name));
                            item.last_name = user_info.optString("last_name", "User");
                            item.profile_pic = user_info.optString("profile_pic", "null");
                            item.verified = user_info.optString("verified");

                            JSONObject sound_data = itemdata.optJSONObject("sound");
                            item.sound_id = sound_data.optString("id");
                            item.sound_name = sound_data.optString("sound_name");
                            item.sound_pic = sound_data.optString("thum");
                            if (sound_data != null) {
                                JSONObject audio_path = sound_data.optJSONObject("audio_path");
                                item.sound_url_mp3 = audio_path.optString("mp3");
                                item.sound_url_acc = audio_path.optString("acc");
                            }


                            JSONObject count = itemdata.optJSONObject("count");
                            item.like_count = count.optString("like_count");
                            item.video_comment_count = count.optString("video_comment_count");


                            if (itemdata.has("allow_duet"))
                                item.allow_duet = itemdata.optString("allow_duet");


                            item.video_id = itemdata.optString("id");
                            item.liked = itemdata.optString("liked");
                            item.followed = itemdata.optString("followed");
                            item.video_url = itemdata.optString("video");
                            item.video_description = itemdata.optString("description");

                            item.thum = itemdata.optString("thum");
                            item.created_date = itemdata.optString("created");

                            data_list.add(item);
                        }

                        Intent intent1 = new Intent(MainMenuActivity.this, WatchVideos_F.class);
                        intent1.putExtra("arraylist", data_list);
                        intent1.putExtra("position", 0);
                        startActivity(intent1);


                    } else {
                        Toast.makeText(MainMenuActivity.this, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        });


    }


    private void initScreen() {
        mainMenuFragment = new MainMenuFragment(onPayment);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainMenuFragment)
                .commit();

        findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (!mainMenuFragment.onBackPressed()) {
            int count = this.getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                if (mBackPressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    Toast.makeText(getBaseContext(), "Tap Again To Exit", Toast.LENGTH_SHORT).show();
                    mBackPressed = System.currentTimeMillis();

                }
            } else {
                super.onBackPressed();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Functions.deleteCache(this);
    }


}
