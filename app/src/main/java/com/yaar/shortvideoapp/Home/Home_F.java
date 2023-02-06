package com.yaar.shortvideoapp.Home;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yaar.shortvideoapp.BuildConfig;
import com.yaar.shortvideoapp.Discover.Discover_F;
import com.yaar.shortvideoapp.Discover.Discover_Get_Set;
import com.yaar.shortvideoapp.Main_Menu.BeCreator;
import com.yaar.shortvideoapp.SearchActivity;
import com.yaar.shortvideoapp.SimpleClasses.AppUpdateDialog;
import com.yaar.shortvideoapp.Video_Recording.Video_Recoder_Duet_A;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.yaar.shortvideoapp.Accounts.Login_A;
import com.yaar.shortvideoapp.Services.Upload_Service;
import com.yaar.shortvideoapp.SimpleClasses.ApiRequest;
import com.yaar.shortvideoapp.SimpleClasses.Callback;
import com.yaar.shortvideoapp.SoundLists.VideoSound_A;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yaar.shortvideoapp.Comments.Comment_F;
import com.yaar.shortvideoapp.Main_Menu.MainMenuActivity;
import com.yaar.shortvideoapp.Main_Menu.MainMenuFragment;
import com.yaar.shortvideoapp.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.yaar.shortvideoapp.Profile.Profile_F;
import com.yaar.shortvideoapp.R;
import com.yaar.shortvideoapp.SimpleClasses.API_CallBack;
import com.yaar.shortvideoapp.SimpleClasses.Fragment_Callback;
import com.yaar.shortvideoapp.SimpleClasses.Fragment_Data_Send;
import com.yaar.shortvideoapp.SimpleClasses.Functions;
import com.yaar.shortvideoapp.SimpleClasses.Variables;
import com.yaar.shortvideoapp.Taged.Taged_Videos_F;
import com.yaar.shortvideoapp.VideoAction.VideoAction_F;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */

// this is the main view which is show all  the video in list
public class Home_F extends RootFragment implements Player.EventListener, Fragment_Data_Send {
    Animation animBlink;
    LinearLayout lnrhastag;
    View view;
    private static final String MY_PREFS_NAME = "has_tag";
    Context context;
    ImageView imghastag;
    RecyclerView recyclerView;
    ArrayList<Home_Get_Set> data_list;
    int currentPage = -1;
    LinearLayoutManager layoutManager;
    ProgressBar p_bar;
    SwipeRefreshLayout swiperefresh;
    boolean is_user_stop_video = false;

    TextView txtgame_zone, txtbea_creator, txthasmegastar;
    String type = "related";
    MainMenuActivity.onPayment onPayment;

    private static final String FB_RC_KEY_TITLE = "update_title";
    private static final String FB_RC_KEY_DESCRIPTION = "update_description";
    private static final String FB_RC_KEY_FORCE_UPDATE_VERSION = "force_update_version";
    private static final String FB_RC_KEY_LATEST_VERSION = "latest_version";

    AppUpdateDialog appUpdateDialog;
    FirebaseRemoteConfig mFirebaseRemoteConfig;

    public Home_F(MainMenuActivity.onPayment onPayment) {
        // Required empty public constructor
        this.onPayment = onPayment;
    }

    int swipe_count = 0;

    MediaSource nextpageSource;
    SimpleExoPlayer nextplayer;
    Home_Get_Set nextitem;
    int StoreCurrentPage;


    RelativeLayout upload_video_layout;
    ImageView uploading_thumb;
    ImageView uploading_icon, search;
    UploadingVideoBroadCast mReceiver;

    // GiftBSFragment giftBSFragment;


    FragmentTransaction transaction;
    SimpleExoPlayer privious_player;

    private class UploadingVideoBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Upload_Service mService = new Upload_Service();
            if (Functions.isMyServiceRunning(context, mService.getClass())) {
                upload_video_layout.setVisibility(View.VISIBLE);
                Bitmap bitmap = Functions.Base64_to_bitmap(Variables.sharedPreferences.getString(Variables.uploading_video_thumb, ""));
                if (bitmap != null)
                    uploading_thumb.setImageBitmap(bitmap);

            } else {
                upload_video_layout.setVisibility(View.GONE);
            }

        }
    }

    public static ProgressBar pagination_progress;

    // HttpProxyCacheServer proxy;
    ProgressBar progressBar;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    LinearLayout lnrfollow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        lnrfollow = view.findViewById(R.id.lnrfollow);
//        LanguageUtils.init(getActivity());


        p_bar = view.findViewById(R.id.p_bar);
        progressBar = (ProgressBar) view.findViewById(R.id.spin_kit);
        progressBar.setVisibility(View.GONE);
        Sprite doubleBounce = new Wave();
        progressBar.setIndeterminateDrawable(doubleBounce);
        checkAppUpdate();

        //proxy = MainAppClass.getProxy(context);
        getSettings();

        pagination_progress = view.findViewById(R.id.pagination_progress);
        search = view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });


        txtgame_zone = view.findViewById(R.id.txtgame_zone);
        txtgame_zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(getActivity() , Gamezone.class);
//                startActivity(intent);

                String url = "https://www.gamezop.com/?id=NyioNfbVK";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

                builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.intent.setPackage("com.android.chrome");
                customTabsIntent.launchUrl(getContext(), Uri.parse(url));


            }
        });
        txtbea_creator = view.findViewById(R.id.txtbea_creator);
        txtbea_creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BeCreator.class);
                startActivity(intent);

            }
        });

        txthasmegastar = view.findViewById(R.id.txthasmegastar);
        txthasmegastar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHasTag();

            }
        });

        recyclerView = view.findViewById(R.id.recylerview);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);
        addCategoryInView("For You");
        addCategoryInView("Trending");

        // giftBSFragment = new GiftBSFragment();

//        giftBSFragment.setStickerListener(new GiftBSFragment.StickerListener() {
//            @Override
//            public void onStickerClick(String  bitmap) {
//                Call_Api_For_BuyGifts(bitmap);
//            }
//        });

        // this is the scroll listener of recycler view which will tell the current item number
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //here we find the current item number
                final int scrollOffset = recyclerView.computeVerticalScrollOffset();
                final int height = recyclerView.getHeight();
                int page_no = scrollOffset / height;

                if (page_no != currentPage) {
                    currentPage = page_no;

                    // proxy.shutdown();

                    Release_Privious_Player();

                    Set_Player(currentPage);

                    Call_Api_For_PaginationVideos(currentPage);

                    //  DownloadnextVideos(currentPage);

                }
            }
        });

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String datevalue = prefs.getString("cur_date4", "12/06/2020");

        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate1 = df1.format(c);
        int dateDifference = (int) getDateDiff(new SimpleDateFormat("dd/MM/yyyy"), datevalue, formattedDate1);

        if (dateDifference > 0) {
            // catalog_outdated = 1;

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            String formattedDate = df.format(c);

            SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("cur_date4", formattedDate);
            editor.apply();
            showHasTag();

        } else {

            System.out.println("");

        }

        swiperefresh = view.findViewById(R.id.swiperefresh);
        swiperefresh.setProgressViewOffset(false, 0, 200);

        swiperefresh.setColorSchemeResources(R.color.black);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Call_Api_For_get_Allvideos();
            }
        });

        Call_Api_For_get_Allvideos();

        if (!Variables.is_remove_ads)
            //  Load_add();
            imghastag = view.findViewById(R.id.imghastag);
        imghastag.startAnimation(animBlink);
        lnrhastag = view.findViewById(R.id.lnrhastag);
        lnrhastag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHasTag();
            }
        });
        upload_video_layout = view.findViewById(R.id.upload_video_layout);
        uploading_thumb = view.findViewById(R.id.uploading_thumb);
        uploading_icon = view.findViewById(R.id.uploading_icon);

        mReceiver = new UploadingVideoBroadCast();
        getActivity().registerReceiver(mReceiver, new IntentFilter("uploadVideo"));

        Upload_Service mService = new Upload_Service();
        if (Functions.isMyServiceRunning(context, mService.getClass())) {
            upload_video_layout.setVisibility(View.VISIBLE);
            Bitmap bitmap = Functions.Base64_to_bitmap(Variables.sharedPreferences.getString(Variables.uploading_video_thumb, ""));
            if (bitmap != null)
                uploading_thumb.setImageBitmap(bitmap);
        }


        return view;
    }


    public void checkAppUpdate() {


        final int versionCode = BuildConfig.VERSION_CODE;

        final HashMap<String, Object> defaultMap = new HashMap<>();
        defaultMap.put(FB_RC_KEY_TITLE, "Update Available");
        defaultMap.put(FB_RC_KEY_DESCRIPTION, "A new version of the application is available please click below to update the latest version.");
        defaultMap.put(FB_RC_KEY_FORCE_UPDATE_VERSION, "" + versionCode);
        defaultMap.put(FB_RC_KEY_LATEST_VERSION, "" + versionCode);

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        mFirebaseRemoteConfig.setConfigSettings(new FirebaseRemoteConfigSettings.Builder().setDeveloperModeEnabled(BuildConfig.DEBUG).build());

        mFirebaseRemoteConfig.setDefaults(defaultMap);

        Task<Void> fetchTask = mFirebaseRemoteConfig.fetch(BuildConfig.DEBUG ? 0 : TimeUnit.HOURS.toSeconds(4));

        fetchTask.addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // After config data is successfully fetched, it must be activated before newly fetched
                    // values are returned.
                    mFirebaseRemoteConfig.activateFetched();

                    String title = getValue(FB_RC_KEY_TITLE, defaultMap);
                    String description = getValue(FB_RC_KEY_DESCRIPTION, defaultMap);
                    int forceUpdateVersion = Integer.parseInt(getValue(FB_RC_KEY_FORCE_UPDATE_VERSION, defaultMap));
                    int latestAppVersion = Integer.parseInt(getValue(FB_RC_KEY_LATEST_VERSION, defaultMap));

                    boolean isCancelable = true;

                    if (latestAppVersion > versionCode) {
                        if (forceUpdateVersion > versionCode)
                            isCancelable = false;

                        appUpdateDialog = new AppUpdateDialog(context, title, description, isCancelable);
                        appUpdateDialog.setCancelable(false);
                        appUpdateDialog.show();

                        Window window = appUpdateDialog.getWindow();
                        assert window != null;
                        window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);

                    }

                } else {
                    Toast.makeText(context, "Fetch Failed",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getValue(String parameterKey, HashMap<String, Object> defaultMap) {
        String value = mFirebaseRemoteConfig.getString(parameterKey);
        if (TextUtils.isEmpty(value))
            value = (String) defaultMap.get(parameterKey);

        return value;
    }


    public void openCategory(String category) {

        type = category.toLowerCase();
        swiperefresh.setRefreshing(true);
        setSelectedType(category);
        Call_Api_For_get_Allvideos();

    }

    private void setSelectedType(String type) {

        LinearLayout lnrfollow = this.view.findViewById(R.id.lnrfollow);

        for (int i = 0; i < lnrfollow.getChildCount(); i++) {

            View view = lnrfollow.getChildAt(i);
            TextView txtview = view.findViewById(R.id.txt_cat);

            if (txtview.getText().toString().equalsIgnoreCase(type)) {
                txtview.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            } else {
                txtview.setTextColor(ContextCompat.getColor(getContext(), R.color.gainsboro));
            }

        }

    }

    public void setCategory(ArrayList<Discover_Get_Set> datalist) {
        Log.d("101010", "datalist : " + datalist.size());
        for (int i = 0; i < datalist.size(); i++) {
            String title = datalist.get(i).title;
            Log.d("101010", "title : " + title);
            // addCategoryInView(title);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("101010", "onDestroyView :");
    }

    private void addCategoryInView(String cat) {

        View view = LayoutInflater.from(context).inflate(R.layout.cat_txtview, null);
        TextView txtview = view.findViewById(R.id.txt_cat);
        txtview.setText(cat + "");
        view.setTag(cat + "");
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag = (String) view.getTag();
                openCategory(tag);
            }
        });

        Log.d("101010", "title :" + cat + " " + view.getHeight() + " " + view.getWidth());
        lnrfollow.addView(view);


    }


    boolean is_add_show = false;
    Home_Adapter adapter;

    public void Set_Adapter() {

        adapter = new Home_Adapter(context, data_list, new Home_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, final Home_Get_Set item, View view) {

                switch (view.getId()) {

                    case R.id.user_pic:
                        onPause();
                        OpenProfile(item, false);
                        break;

                    case R.id.lnrslidimg:
                        onPause();
                        OpenProfile(item, true);
                        break;

                    case R.id.lnrhastag:
                        onPause();
                        showHasTag();
                        break;

                    case R.id.username:
                        onPause();
                        OpenProfile(item, false);
                        break;

                    case R.id.like_layout:
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            Like_Video(postion, item, "click");
                        } else {
                            Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.comment_layout:
                        OpenComment(item);
                        break;
                    case R.id.download_layout:
                        Save_Video(item, null);
                        break;

                    case R.id.duet_layout:

                        if (check_permissions()) {
                            if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                                Duet_video(item);
                            } else {
                                Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;


                    case R.id.shared_layout:
//                        if (!is_add_show ){
//                            is_add_show = true;
//                        } else {
                        is_add_show = false;
                        final VideoAction_F fragment = new VideoAction_F(item.video_id, new Fragment_Callback() {
                            @Override
                            public void Responce(Bundle bundle) {

                                String action = bundle.getString("action");

                                switch (action) {

                                    case VideoAction_F.NORMAL_SHARE:
                                    case VideoAction_F.WHATSAPP_SHARE:
                                    case VideoAction_F.FACEBOOK_SHARE:
                                    case VideoAction_F.FACEBOOK_STORY_SHARE:
                                    case VideoAction_F.YOUTUBE_SHARE:
                                    case VideoAction_F.INSTRAGRAM_SHARE:
                                    case VideoAction_F.INSTRAGRAM_STORY_SHARE:
                                        Save_Video(item, action);
                                        break;

                                    case "save":
                                        Save_Video(item, null);
                                        break;
                                    case "duet":
                                        Duet_video(item);
                                        break;

                                    case "delete":
                                        Functions.Show_loader(context, false, false);
                                        Functions.Call_Api_For_Delete_Video(getActivity(), item.video_id, new API_CallBack() {
                                            @Override
                                            public void ArrayData(ArrayList arrayList) {

                                            }

                                            @Override
                                            public void OnSuccess(String responce) {
                                                data_list.remove(currentPage);
                                                adapter.notifyDataSetChanged();

                                            }

                                            @Override
                                            public void OnFail(String responce) {

                                            }

                                        });
                                        break;

                                }

                            }
                        });

                        Bundle bundle = new Bundle();
                        bundle.putString("video_id", item.video_id);
                        bundle.putString("user_id", item.fb_id);
                        fragment.setArguments(bundle);
                        fragment.show(getChildFragmentManager(), "");
                        // }

                        break;


                    case R.id.sound_layout:

                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            if (check_permissions()) {
                                Intent intent = new Intent(getActivity(), VideoSound_A.class);
                                intent.putExtra("data", item);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                        }

                        break;

//
//
//                    case R.id.gift_layout:
//                        if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
//                            giftBSFragment.show(getFragmentManager(), giftBSFragment.getTag());
//                        }else {
//                            Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
//                        }
//                        break;

                    case R.id.follow_unfollow_txt:

                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false))
                            Follow_unFollow_User(postion, item, view);
                        else
                            Toast.makeText(context, "Please login in to app", Toast.LENGTH_SHORT).show();

                        break;
                }

            }
        });

        adapter.setHasStableIds(true);
        recyclerView.setAdapter(adapter);

    }

    public void Follow_unFollow_User(int possition, Home_Get_Set item, View view) {
        // TextView follow_unfollow_txt = view.findViewById(R.id.follow_unfollow_txt);
//        final String send_status;
//        if(follow_status.equals("0")){
//            send_status="1";
//        }else {
//            send_status="0";
//        }


        Functions.Call_Api_For_Follow_or_unFollow(getActivity(),
                Variables.sharedPreferences.getString(Variables.u_id, ""),
                item.fb_id,
                "1",
                new API_CallBack() {
                    @Override
                    public void ArrayData(ArrayList arrayList) {


                    }

                    @Override
                    public void OnSuccess(String responce) {

                        data_list.remove(possition);
                        item.followed = "1";
                        data_list.add(possition, item);
                        adapter.notifyDataSetChanged();

//                        if(send_status.equals("1")){
//                            follow_unfollow_btn.setText("UnFollow");
//                            follow_status="1";
//
//                        }
//                        else if(send_status.equals("0")){
//                            follow_unfollow_btn.setText("Follow");
//                            follow_status="0";
//                        }

                        // Call_Api_For_get_Allvideos();
                    }

                    @Override
                    public void OnFail(String responce) {

                    }

                });


    }

    // Bottom two function will call the api and get all the videos form api and parse the json data
    private void Call_Api_For_get_Allvideos() {


        Log.d(Variables.tag, MainMenuActivity.token);
        currentPage = -1;

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("token", MainMenuActivity.token);
            parameters.put("type", type);
            Log.d("TAG_response_home_pa", "Responce: " + parameters);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("TAG_response_home_link", "Responce: " + Variables.showAllVideos);


        ApiRequest.Call_Api(context, Variables.showAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Log.d("TAG_video", "Responce: " + resp);
                swiperefresh.setRefreshing(false);
                Parse_data(resp);
            }
        });


    }

    public void Parse_data(String responce) {
        Log.d("TAG_response_home", "Parse_data: " + responce);
        data_list = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");

                ArrayList<Home_Get_Set> temp_list = new ArrayList();
                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item = new Home_Get_Set();
                    item.fb_id = itemdata.optString("fb_id");

                    JSONObject user_info = itemdata.optJSONObject("user_info");
                    Log.d("TAG_response_home_us", "Parse_data: " + user_info);
                    item.username = user_info.optString("username");
                    item.first_name = user_info.optString("first_name", context.getResources().getString(R.string.app_name));
                    item.last_name = user_info.optString("last_name", "User");
                    item.profile_pic = user_info.optString("profile_pic", "null");
                    item.verified = user_info.optString("verified");
                    Log.d("TAG_response_home_us", "Parse_data: " + user_info.optString("username"));
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


                    item.video_id = itemdata.optString("id");
                    item.liked = itemdata.optString("liked");
                    item.followed = itemdata.optString("followed");
                    item.video_url = itemdata.optString("video");


                    item.video_description = itemdata.optString("description");

                    item.thum = itemdata.optString("thum");
                    item.created_date = itemdata.optString("created");

                    temp_list.add(item);
                }

                if (!temp_list.isEmpty()) {
                    data_list.addAll(temp_list);
                    Set_Adapter();
                }

                setSelectedType(type);
//                } else if (type.equalsIgnoreCase("related")) {
//                    type = "following";
//                    related_btn.setTextColor(context.getResources().getColor(R.color.graycolor2));
//                    following_btn.setTextColor(context.getResources().getColor(R.color.white));
//                } else if (type.equalsIgnoreCase("following")) {
//                    type = "related";
//                    related_btn.setTextColor(context.getResources().getColor(R.color.white));
//                    following_btn.setTextColor(context.getResources().getColor(R.color.graycolor2));
//                }

            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void Call_Api_For_Singlevideos(final int postion) {


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("token", Variables.sharedPreferences.getString(Variables.device_token, "Null"));
            parameters.put("video_id", data_list.get(postion).video_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.Call_Api(context, Variables.showAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                swiperefresh.setRefreshing(false);
                Singal_Video_Parse_data(postion, resp);
            }
        });


    }


    private void Call_Api_For_PaginationVideos(int postion) {
        if (postion + 1 >= data_list.size()) {

            Log.d(Variables.tag, MainMenuActivity.token);

            JSONObject parameters = new JSONObject();
            try {
                parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
                parameters.put("token", MainMenuActivity.token);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApiRequest.Call_Api(context, Variables.showAllVideos, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    swiperefresh.setRefreshing(false);
                    getPaginationVideoData(resp);
                }
            });

        }
    }


    private void getSettings() {
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("token", MainMenuActivity.token);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.settings, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    String b_status = jsonObject.getString("b_status");

                    if (b_status.equals("1")) {

                        String b_url = jsonObject.getString("b_url");
                        String b_icon = jsonObject.getString("b_icon");
                        String b_name = jsonObject.getString("b_name");


                        view.findViewById(R.id.lnrSetting).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.lnrSetting).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

                                builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                                CustomTabsIntent customTabsIntent = builder.build();
                                customTabsIntent.intent.setPackage("com.android.chrome");
                                customTabsIntent.launchUrl(getContext(), Uri.parse(b_url));
                            }
                        });

                        ((TextView) view.findViewById(R.id.txt_setting)).setText(b_name);
                        ((TextView) view.findViewById(R.id.txt_setting)).setText(b_name);
                        Picasso.get().load(b_icon)
                                .resize(200, 200)
                                .placeholder(R.drawable.app_icon)
                                .centerCrop()
                                .into(((ImageView) view.findViewById(R.id.imgSetting)));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void getPaginationVideoData(String responce) {

        try {
            Log.d("101010", "respp " + responce);
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item = new Home_Get_Set();
                    item.fb_id = itemdata.optString("fb_id");

                    JSONObject user_info = itemdata.optJSONObject("user_info");

                    item.username = user_info.optString("username");
                    item.first_name = user_info.optString("first_name", context.getResources().getString(R.string.app_name));
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

                adapter.notifyDataSetChanged();

            } else {
                // Toast.makeText(context, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void Singal_Video_Parse_data(int pos, String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item = new Home_Get_Set();
                    item.fb_id = itemdata.optString("fb_id");

                    JSONObject user_info = itemdata.optJSONObject("user_info");

                    item.username = user_info.optString("username");
                    item.first_name = user_info.optString("first_name", context.getResources().getString(R.string.app_name));
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

                    data_list.remove(pos);
                    data_list.add(pos, item);
                    adapter.notifyDataSetChanged();
                }


            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }


    // this will call when swipe for another video and
    // this function will set the player to the current video


//    public void Set_Player(final int currentPage){
//
//
//
//        final Home_Get_Set item= data_list.get(currentPage);
//
//        /*  Call_cache();
//
//
//         */
//        LoadControl loadControl = new DefaultLoadControl.Builder()
//                .setAllocator(new DefaultAllocator(true, 16))
//                .setBufferDurationsMs(1*1024, 1*1024, 500, 1024)
//                .setTargetBufferBytes(-1)
//                .setPrioritizeTimeOverSizeThresholds(true)
//                .createDefaultLoadControl();
//
//       // DefaultTrackSelector trackSelector = new DefaultTrackSelector();
//      //  final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context, trackSelector,loadControl);
//        SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
////        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
////                Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));
//
//        DataSource.Factory dataSourceFactory =
//                new DefaultHttpDataSourceFactory(Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));
//
////        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
////                .createMediaSource(Uri.parse(item.video_url));
//
//        HlsMediaSource hlsMediaSource =
//                new HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(Uri.parse(item.video_url));
//
//       /* MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(Uri.parse(proxyUrl));
//
//            Log.d(Variables.tag,item.video_url);
//            Log.d(Variables.tag,proxyUrl);*/
//
//      //  player.seekTo(currentPage,1);
//        player.prepare(hlsMediaSource);
//
//        player.setRepeatMode(Player.REPEAT_MODE_ALL);
//        player.addListener(this);
//
//
//        View layout=layoutManager.findViewByPosition(currentPage);
//        final PlayerView playerView=layout.findViewById(R.id.playerview);
//        playerView.setPlayer(player);
//
//
//        player.setPlayWhenReady(is_visible_to_user);
//        privious_player=player;
//
//        final RelativeLayout mainlayout = layout.findViewById(R.id.mainlayout);
//        playerView.setOnTouchListener(new View.OnTouchListener() {
//            private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//
//                @Override
//                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                    super.onFling(e1, e2, velocityX, velocityY);
//                    float deltaX = e1.getX() - e2.getX();
//                    float deltaXAbs = Math.abs(deltaX);
//                    // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
//                    if((deltaXAbs > 100) && (deltaXAbs < 1000)) {
//                        if(deltaX > 0)
//                        {
//                            OpenProfile(item,true);
//                        }
//                    }
//
//
//                    return true;
//                }
//
//                @Override
//                public boolean onSingleTapUp(MotionEvent e) {
//                    super.onSingleTapUp(e);
//                    if(!player.getPlayWhenReady()){
//                        is_user_stop_video=false;
//                        privious_player.setPlayWhenReady(true);
//                    }else{
//                        is_user_stop_video=true;
//                        privious_player.setPlayWhenReady(false);
//                    }
//
//
//                    return true;
//                }
//
//                @Override
//                public void onLongPress(MotionEvent e) {
//                    super.onLongPress(e);
//                    Show_video_option(item);
//
//                }
//
//                @Override
//                public boolean onDoubleTap(MotionEvent e) {
//
//                    if(!player.getPlayWhenReady()){
//                        is_user_stop_video=false;
//                        privious_player.setPlayWhenReady(true);
//                    }
//
//
//                    if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
//                        Show_heart_on_DoubleTap(item, mainlayout, e);
//                        Like_Video(currentPage, item,"double");
//                    }else {
//                        Toast.makeText(context, "Please Login into app", Toast.LENGTH_SHORT).show();
//                    }
//                    return super.onDoubleTap(e);
//
//                }
//            });
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                gestureDetector.onTouchEvent(event);
//                return true;
//            }
//        });
//
//        TextView desc_txt=layout.findViewById(R.id.desc_txt);
//        HashTagHelper.Creator.create(context.getResources().getColor(R.color.maincolor), new HashTagHelper.OnHashTagClickListener() {
//            @Override
//            public void onHashTagClicked(String hashTag) {
//
//                onPause();
//                OpenHashtag(hashTag);
//
//            }
//        }).handle(desc_txt);
//
//
//
//        LinearLayout soundimage = (LinearLayout)layout.findViewById(R.id.sound_image_layout);
//        Animation sound_animation = AnimationUtils.loadAnimation(context,R.anim.d_clockwise_rotation);
//        soundimage.startAnimation(sound_animation);
//
//        if(Variables.sharedPreferences.getBoolean(Variables.islogin,false))
//            Functions.Call_Api_For_update_view(getActivity(),item.video_id);
//
//
//        swipe_count++;
//        if(swipe_count>6){
//            Show_add();
//            swipe_count=0;
//        }
//
//
//        if(Variables.is_demo_app && currentPage==data_list.size()-1){
//            Toast.makeText(context, "Only allow "+data_list.size()+" Videos in demo app", Toast.LENGTH_LONG).show();
//        }
//
//
//        Call_Api_For_Singlevideos(currentPage);
//
//    }

    String videoid;
    ImageView img_temp;
    RelativeLayout rlt_temb;

    // String videoid;
//    public void Set_Player(final int currentPage){
//
//
//        final SimpleExoPlayer player;
//        MediaSource videoSource;
//        final Home_Get_Set item;
//
//        try {
//
//            if(nextpageSource == null || StoreCurrentPage > currentPage)
//            {
//                item = data_list.get(currentPage);
//
//                Call_cache();
//                proxy = MainAppClass.getProxy(context);
//                String proxyUrl = proxy.getProxyUrl(item.video_url);
//
//
//                // DefaultLoadControl loadControl = new DefaultLoadControl.Builder().setBufferDurationsMs(1*1024, 1*1024, 500, 1024).createDefaultLoadControl();
//
//                LoadControl loadControl = new DefaultLoadControl.Builder()
//                        .setAllocator(new DefaultAllocator(true, 16))
//                        .setBufferDurationsMs(1*1024, 1*1024, 500, 1024)
//                        .setTargetBufferBytes(-1)
//                        .setPrioritizeTimeOverSizeThresholds(true)
//                        .createDefaultLoadControl();
//
//                DefaultTrackSelector trackSelector = new DefaultTrackSelector();
//                player = ExoPlayerFactory.newSimpleInstance(context, trackSelector,loadControl);
//
//                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
//                        Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));
//
//                videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
//                        .createMediaSource(Uri.parse(proxyUrl));
//
//                player.prepare(videoSource);
//                player.setRepeatMode(Player.REPEAT_MODE_ALL);
//                player.addListener(this);
//
//
//                Log.d(Variables.tag,item.video_url);
//                Log.d(Variables.tag,proxyUrl);
//            }
//            else {
//                player = nextplayer;
//                videoSource = nextpageSource;
//                item = nextitem;
//            }
//
//            videoid = data_list.get(currentPage).video_id;
//
//            StoreCurrentPage = currentPage;
//            View layout=layoutManager.findViewByPosition(currentPage);
//            final PlayerView playerView=layout.findViewById(R.id.playerview);
//            img_temp = layout.findViewById(R.id.img_temp);
//            playerView.setPlayer(player);
//
//
//            player.setPlayWhenReady(is_visible_to_user);
//            privious_player = player;
//
//
//
//            final RelativeLayout mainlayout = layout.findViewById(R.id.mainlayout);
//            playerView.setOnTouchListener(new View.OnTouchListener() {
//                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
//
//                    @Override
//                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
//                        super.onFling(e1, e2, velocityX, velocityY);
//                        float deltaX = e1.getX() - e2.getX();
//                        float deltaXAbs = Math.abs(deltaX);
//                        // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
//                        if((deltaXAbs > 100) && (deltaXAbs < 1000)) {
//                            if(deltaX > 0)
//                            {
//                                OpenProfile(item,true);
//                            }
//                        }
//
//
//                        return true;
//                    }
//
//                    @Override
//                    public boolean onSingleTapUp(MotionEvent e) {
//                        super.onSingleTapUp(e);
//                        if(!player.getPlayWhenReady()){
//                            is_user_stop_video=false;
//                            privious_player.setPlayWhenReady(true);
//                        }else{
//                            is_user_stop_video=true;
//                            privious_player.setPlayWhenReady(false);
//                        }
//
//
//                        return true;
//                    }
//
//                    @Override
//                    public void onLongPress(MotionEvent e) {
//                        super.onLongPress(e);
//                        Show_video_option(item);
//
//                    }
//
//                    @Override
//                    public boolean onDoubleTap(MotionEvent e) {
//
//                        if(!player.getPlayWhenReady()){
//                            is_user_stop_video=false;
//                            privious_player.setPlayWhenReady(true);
//                        }
//
////
//                        if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
//                            Show_heart_on_DoubleTap(item, mainlayout, e);
//                            Like_Video(currentPage, item,"double");
//                        }else {
//                            Toast.makeText(context, "Please Login into app", Toast.LENGTH_SHORT).show();
//                        }
//                        return super.onDoubleTap(e);
//
//                    }
//                });
//
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    gestureDetector.onTouchEvent(event);
//                    return true;
//                }
//            });
//
//            TextView desc_txt=layout.findViewById(R.id.desc_txt);
//            HashTagHelper.Creator.create(context.getResources().getColor(R.color.maincolor), new HashTagHelper.OnHashTagClickListener() {
//                @Override
//                public void onHashTagClicked(String hashTag) {
//
//                    onPause();
//                    OpenHashtag(hashTag);
//
//                }
//            }).handle(desc_txt);
//
//
//
//            LinearLayout soundimage = (LinearLayout)layout.findViewById(R.id.sound_layout);
//            Animation sound_animation = AnimationUtils.loadAnimation(context,R.anim.fade_in);
//            soundimage.startAnimation(sound_animation);
//
//            if(Variables.sharedPreferences.getBoolean(Variables.islogin,false))
//                Functions.Call_Api_For_update_view(getActivity(),item.video_id);
//
//
////            swipe_count++;
////            if(swipe_count>6){
////            //    Show_add();
////                swipe_count=0;
////            }
//
//
//        }
//        catch (Exception e)
//        {
//            Log.e("","Exception Set Player  :"+e.getMessage());
//        }
//
//
////        Call_Api_For_Singlevideos(currentPage);
//
//    }

    public void Set_Player(final int currentPage) {


        final SimpleExoPlayer player;
        MediaSource videoSource;
        final Home_Get_Set item;

        try {

            if (nextpageSource == null || StoreCurrentPage > currentPage) {
                item = data_list.get(currentPage);

                //  Call_cache();
                //proxy = MainAppClass.getProxy(context);
                // String proxyUrl = proxy.getProxyUrl(item.video_url);

                LoadControl loadControl = new DefaultLoadControl.Builder()
                        .setAllocator(new DefaultAllocator(true, 16))
                        .setBufferDurationsMs(1 * 1024, 1 * 1024, 500, 1024)
                        .setTargetBufferBytes(-1)
                        .setPrioritizeTimeOverSizeThresholds(true)
                        .createDefaultLoadControl();

                DefaultTrackSelector trackSelector = new DefaultTrackSelector(context);
                player = new SimpleExoPlayer.Builder(context)
                        .setTrackSelector(trackSelector).setLoadControl(loadControl)
                        .build();
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                        Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));

                videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(item.video_url));

                player.prepare(videoSource);
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
                player.addListener(this);


                Log.d(Variables.tag, item.video_url);
                Log.d(Variables.tag, item.video_url);
            } else {
                player = nextplayer;
                videoSource = nextpageSource;
                item = nextitem;
            }

            videoid = data_list.get(currentPage).video_id;

            StoreCurrentPage = currentPage;
            View layout = layoutManager.findViewByPosition(currentPage);
            final PlayerView playerView = layout.findViewById(R.id.playerview);
            img_temp = layout.findViewById(R.id.img_temp);
            playerView.setPlayer(player);


            player.setPlayWhenReady(is_visible_to_user);
            privious_player = player;


            final RelativeLayout mainlayout = layout.findViewById(R.id.mainlayout);
            playerView.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        super.onFling(e1, e2, velocityX, velocityY);
                        float deltaX = e1.getX() - e2.getX();
                        float deltaXAbs = Math.abs(deltaX);
                        // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
                        if ((deltaXAbs > 100) && (deltaXAbs < 1000)) {
                            if (deltaX > 0) {
                                OpenProfile(item, true);
                            }
                        }


                        return true;
                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        super.onSingleTapUp(e);
                        if (!player.getPlayWhenReady()) {
                            is_user_stop_video = false;
                            privious_player.setPlayWhenReady(true);
                        } else {
                            is_user_stop_video = true;
                            privious_player.setPlayWhenReady(false);
                        }


                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        super.onLongPress(e);
                        Show_video_option(item);

                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        if (!player.getPlayWhenReady()) {
                            is_user_stop_video = false;
                            privious_player.setPlayWhenReady(true);
                        }

//
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            Show_heart_on_DoubleTap(item, mainlayout, e);
                            Like_Video(currentPage, item, "double");
                        } else {
                            Toast.makeText(context, "Please Login into app", Toast.LENGTH_SHORT).show();
                        }
                        return super.onDoubleTap(e);

                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });

            TextView desc_txt = layout.findViewById(R.id.desc_txt);
            HashTagHelper.Creator.create(context.getResources().getColor(R.color.maincolor), new HashTagHelper.OnHashTagClickListener() {
                @Override
                public void onHashTagClicked(String hashTag) {

                    onPause();
                    OpenHashtag(hashTag);

                }
            }).handle(desc_txt);


            LinearLayout soundimage = (LinearLayout) layout.findViewById(R.id.sound_layout);
            Animation sound_animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
            soundimage.startAnimation(sound_animation);

            if (Variables.sharedPreferences.getBoolean(Variables.islogin, false))
                Functions.Call_Api_For_update_view(getActivity(), item.video_id);


//            swipe_count++;
//            if(swipe_count>6){
//            //    Show_add();
//                swipe_count=0;
//            }


        } catch (Exception e) {
            Log.e("", "Exception Set Player  :" + e.getMessage());
        }


//        Call_Api_For_Singlevideos(currentPage);

    }


//    private void DownloadnextVideos(int currentpage){
//
//        int nextPage = currentpage +1;
//
//        if(nextPage < data_list.size()) {
//
//            try {
//                final Home_Get_Set item = data_list.get(nextPage);
//                nextitem = item;
//
////                if(proxy == null)
////                    proxy = MainAppClass.getProxy(context);
//
//
//             //   String proxyUrl = proxy.getProxyUrl(item.video_url);
//
////                DefaultLoadControl loadControl = new DefaultLoadControl.Builder().setBufferDurationsMs(1 * 1024, 1 * 1024, 500, 1024).createDefaultLoadControl();
//
//                LoadControl loadControl = new DefaultLoadControl.Builder()
//                        .setAllocator(new DefaultAllocator(true, 16))
//                        .setBufferDurationsMs(1*1024, 1*1024, 500, 1024)
//                        .setTargetBufferBytes(-1)
//                        .setPrioritizeTimeOverSizeThresholds(true)
//                        .createDefaultLoadControl();
//                DefaultTrackSelector trackSelector = new DefaultTrackSelector(context);
//
//               // SimpleExoPlayer player = new SimpleExoPlayer.Builder(context).build();
//                SimpleExoPlayer   player = new SimpleExoPlayer.Builder(context)
//                        .setTrackSelector(trackSelector).setLoadControl(loadControl)
//                        .build();
//                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
//                        Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));
////                HlsMediaSource hlsMediaSource =
////                        new HlsMediaSource.Factory(dataSourceFactory).setAllowChunklessPreparation(true).createMediaSource(Uri.parse(item.video_url));
//                MediaSource  videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
//                        .createMediaSource(Uri.parse(item.video_url));
//                player.prepare(videoSource);
//
//                player.setRepeatMode(Player.REPEAT_MODE_ALL);
//                player.addListener(this);
//
////                nextpageSource = videoSource;
//
//                nextplayer = player;
//
//                Log.d("nextVideosList", item.video_url);
//                Log.d("nextVideosproxtURL", item.video_url);
//            }
//            catch (Exception e)
//            {
//                Log.e("","DownlaodNext Expection : "+e.getMessage());
//            }
//        }
//        else {
//            nextpageSource = null;
//        }
//
//
//    }


//    public void Call_cache(){
//        if(currentPage+1<data_list.size()){
//
//            if(proxy == null)
//                proxy = MainAppClass.getProxy(context);
//
//            proxy.getProxyUrl(data_list.get(currentPage+1).video_url);
//
//        }
//    }


    public void Show_heart_on_DoubleTap(Home_Get_Set item, final RelativeLayout mainlayout, MotionEvent e) {

        int x = (int) e.getX() - 100;
        int y = (int) e.getY() - 100;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        final ImageView iv = new ImageView(getApplicationContext());
        lp.setMargins(x, y, 0, 0);
        iv.setLayoutParams(lp);
        if (item.liked.equals("1"))
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.ic_like));
        else
            iv.setImageDrawable(getResources().getDrawable(
                    R.drawable.ic_like_fill));

        mainlayout.addView(iv);
        Animation fadeoutani = AnimationUtils.loadAnimation(context, R.anim.fade_out);

        fadeoutani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mainlayout.removeView(iv);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        iv.startAnimation(fadeoutani);

    }


    @Override
    public void onDataSent(String yourData) {
        int comment_count = Integer.parseInt(yourData);
        Home_Get_Set item = data_list.get(currentPage);
        item.video_comment_count = "" + comment_count;
        data_list.remove(currentPage);
        data_list.add(currentPage, item);
        adapter.notifyDataSetChanged();
    }


    // this will call when go to the home tab From other tab.
    // this is very importent when for video play and pause when the focus is changes
    boolean is_visible_to_user;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        is_visible_to_user = isVisibleToUser;

        if (privious_player != null && (isVisibleToUser && !is_user_stop_video)) {
            privious_player.setPlayWhenReady(true);
        } else if (privious_player != null && !isVisibleToUser) {
            privious_player.setPlayWhenReady(false);
        }
    }


    // when we swipe for another video this will relaese the privious player

    public void Release_Privious_Player() {
        if (privious_player != null) {
            privious_player.removeListener(this);
            privious_player.release();
        }
    }


    // this function will call for like the video and Call an Api for like the video
    public void Like_Video(final int position, final Home_Get_Set home_get_set, String tag) {
        String action = home_get_set.liked;

        if (action.equals("1")) {

            //     Dis-like fuction
            if (!tag.equals("double")) {
                action = "0";
                home_get_set.like_count = "" + (Integer.parseInt(home_get_set.like_count) - 1);
            }

        } else {

            //     like fuction

            action = "1";
            home_get_set.like_count = "" + (Integer.parseInt(home_get_set.like_count) + 1);
        }


        data_list.remove(position);
        home_get_set.liked = action;
        data_list.add(position, home_get_set);
        adapter.notifyDataSetChanged();

        Functions.Call_Api_For_like_video(getActivity(), home_get_set.video_id, action, new API_CallBack() {

            @Override
            public void ArrayData(ArrayList arrayList) {

            }

            @Override
            public void OnSuccess(String responce) {

            }

            @Override
            public void OnFail(String responce) {

            }
        });

    }


    // this will open the comment screen
    private void OpenComment(Home_Get_Set item) {

        int comment_counnt = Integer.parseInt(item.video_comment_count);

        Fragment_Data_Send fragment_data_send = this;

        Comment_F comment_f = new Comment_F(comment_counnt, fragment_data_send);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        Bundle args = new Bundle();
        args.putString("video_id", item.video_id);
        args.putString("user_id", item.fb_id);
        args.putString("from", "home");
        comment_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, comment_f).commit();


    }


    public void Open_Login() {
        Intent intent = new Intent(getActivity(), Login_A.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    // this will open the profile of user which have uploaded the currenlty running video
    private void OpenProfile(Home_Get_Set item, boolean from_right_to_left) {

        pagination_progress.setVisibility(View.VISIBLE);

        if (Variables.sharedPreferences.getString(Variables.u_id, "0").equals(item.fb_id)) {

            TabLayout.Tab profile = MainMenuFragment.tabLayout.getTabAt(4);
            profile.select();

        } else {
            Profile_F profile_f = new Profile_F(new Fragment_Callback() {
                @Override
                public void Responce(Bundle bundle) {
                    Call_Api_For_Singlevideos(currentPage);
                }
            });
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            if (from_right_to_left)
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            else
                transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);

            Bundle args = new Bundle();
            args.putString("user_id", item.fb_id);
            args.putString("user_name", item.first_name + " " + item.last_name);
            args.putString("user_pic", item.profile_pic);
            profile_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, profile_f).commit();
        }

    }


    // this will open the profile of user which have uploaded the currenlty running video
    private void OpenHashtag(String tag) {

        Taged_Videos_F taged_videos_f = new Taged_Videos_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        Bundle args = new Bundle();
        args.putString("tag", tag);
        taged_videos_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, taged_videos_f).commit();


    }


    private void Show_video_option(final Home_Get_Set home_get_set) {

        final CharSequence[] options = {"Save Video", "Cancel"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Save Video")) {
                    if (Functions.Checkstoragepermision(getActivity()))
                        Save_Video(home_get_set, null);

                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }


    public void Save_Video(final Home_Get_Set item, String actionAfterDownload) {

        JSONObject params = new JSONObject();
        try {
            params.put("video_id", item.video_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context, false, false);
        ApiRequest.Call_Api(context, Variables.downloadFile, params, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    JSONObject responce = new JSONObject(resp);
                    String code = responce.optString("code");
                    if (code.equals("200")) {
                        JSONArray msg = responce.optJSONArray("msg");
                        JSONObject jsonObject = msg.optJSONObject(0);
                        String download_url = jsonObject.getString("download_url");

                        if (download_url != null) {

                            Functions.Show_determinent_loader(context, false, false);
                            PRDownloader.initialize(getActivity().getApplicationContext());
                            DownloadRequest prDownloader = PRDownloader.download(download_url, Variables.app_folder, item.video_id + ".mp4")
                                    .build()
                                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                        @Override
                                        public void onStartOrResume() {

                                        }
                                    })
                                    .setOnPauseListener(new OnPauseListener() {
                                        @Override
                                        public void onPause() {

                                        }
                                    })
                                    .setOnCancelListener(new OnCancelListener() {
                                        @Override
                                        public void onCancel() {

                                        }
                                    })
                                    .setOnProgressListener(new OnProgressListener() {
                                        @Override
                                        public void onProgress(Progress progress) {

                                            int prog = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                                            Functions.Show_loading_progress(prog);

                                        }
                                    });


                            prDownloader.start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
                                    Functions.cancel_determinent_loader();
                                    if (actionAfterDownload != null) {

                                        Functions.cancel_determinent_loader();
                                        Scan_file(item);
                                        String path = Variables.app_folder + item.video_id + ".mp4"; //should be local path of downloaded video
                                        int unicode = 0x1f447;
                                        MediaScannerConnection.scanFile(getActivity(), new String[]{path},

                                                null, new MediaScannerConnection.OnScanCompletedListener() {
                                                    public void onScanCompleted(String path, Uri uri) {


                                                        switch (actionAfterDownload) {


                                                            case VideoAction_F.NORMAL_SHARE: {
                                                                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                                                                shareIntent.setType("video/*");
                                                                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Yaar Video App");
                                                                shareIntent.putExtra(android.content.Intent.EXTRA_TITLE, "Yaar Video App");
                                                                shareIntent.putExtra(Intent.EXTRA_TEXT, "WOW! Super video.To watch such Great Videos !!Just hit the below link and Install Yaar App!!  \n" + getEmojiByUnicode(unicode) + getEmojiByUnicode(unicode) + getEmojiByUnicode(unicode) + "\n" + " PlayStore Link- https://play.google.com/store/apps/details?id=com.yaar.shortvideoap");
                                                                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                                                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                context.startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));

                                                            }
                                                            break;
                                                            case VideoAction_F.WHATSAPP_SHARE:

                                                                if (isPackageInstalled("com.whatsapp")) {
                                                                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                                                                    shareIntent.setType("video/*");
                                                                    shareIntent.setPackage("com.whatsapp");
                                                                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Yaar Video App");
                                                                    shareIntent.putExtra(android.content.Intent.EXTRA_TITLE, "Yaar Video App");
                                                                    shareIntent.putExtra(Intent.EXTRA_TEXT, " WOW! Super video.To watch such Great Videos !!Just hit the below link and Install Yaar App!!  \n" + getEmojiByUnicode(unicode) + getEmojiByUnicode(unicode) + getEmojiByUnicode(unicode) + "\n" + "  PlayStore Link- https://play.google.com/store/apps/details?id=com.yaar.shortvideoap");
                                                                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                                                                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                    context.startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                                                                }
                                                                break;

                                                            case VideoAction_F.YOUTUBE_SHARE:

                                                                if (isPackageInstalled("com.google.android.youtube")) {
//                                                                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
//                                                                    shareIntent.setType("video/*");
//                                                                    shareIntent.setPackage("com.google.android.youtube");
//                                                                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Yaar Video App");
//                                                                    shareIntent.putExtra(android.content.Intent.EXTRA_TITLE, "Yaar Video App");
//                                                                    shareIntent.putExtra(Intent.EXTRA_TEXT, "वाह !! क्या वीडियो है ये !! ऐसे और वीडियो देखने के  लिए निचे लिंक पर क्लिक करे || \n WOW! Super video.To watch such Great Videos !!Just hit the below link and Install Yaar App!!  \n" + getEmojiByUnicode(unicode) + getEmojiByUnicode(unicode) + getEmojiByUnicode(unicode) + "\n" + " https://play.google.com/store/apps/details?id=com.yaar.shortvideoap");
//                                                                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
//                                                                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                                                    context.startActivity(Intent.createChooser(shareIntent, "Share Video"));

                                                                    Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                                                                    shareIntent.setType("video/*");
                                                                    shareIntent.setPackage("com.google.android.youtube");
                                                                    shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                    shareIntent.setDataAndType(uri, "video/*");
                                                                    Activity activity = getActivity();

                                                                    if (activity.getPackageManager().resolveActivity(shareIntent, 0) != null) {
                                                                        activity.startActivityForResult(shareIntent, 0);
                                                                    }

                                                                    ContentValues content = new ContentValues(4);
                                                                    content.put(MediaStore.Video.VideoColumns.DATE_ADDED,
                                                                            System.currentTimeMillis() / 1000);
                                                                    content.put(MediaStore.Video.Media.MIME_TYPE, "video/*");
                                                                    content.put(MediaStore.Video.Media.DATA, uri.getPath());
                                                                    ContentResolver resolver = getActivity().getContentResolver();
                                                                    Uri uri1 = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, content);

                                                                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                                                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Title");
                                                                    sharingIntent.putExtra(android.content.Intent.EXTRA_STREAM, uri1);
                                                                    sharingIntent.setPackage("com.google.android.youtube");
                                                                    sharingIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                                                    startActivity(Intent.createChooser(sharingIntent, "share:"));

                                                                }
                                                                break;

                                                            case VideoAction_F.INSTRAGRAM_SHARE:

                                                                if (isPackageInstalled("com.instagram.android")) {
                                                                    Intent shareIntent = new Intent("com.instagram.share.ADD_TO_FEED");
                                                                    shareIntent.setType("video/*");
                                                                    shareIntent.setPackage("com.instagram.android");
                                                                    shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                    shareIntent.setDataAndType(uri, "video/*");
                                                                    Activity activity = getActivity();
                                                                    if (activity.getPackageManager().resolveActivity(shareIntent, 0) != null) {
                                                                        activity.startActivityForResult(shareIntent, 0);
                                                                    }
                                                                }
                                                                break;

                                                            case VideoAction_F.INSTRAGRAM_STORY_SHARE:

                                                                if (isPackageInstalled("com.instagram.android")) {
                                                                    Intent shareIntent = new Intent("com.instagram.share.ADD_TO_STORY");
                                                                    shareIntent.setType("video/*");
                                                                    shareIntent.setPackage("com.instagram.android");
                                                                    shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                    shareIntent.setDataAndType(uri, "video/*");
                                                                    Activity activity = getActivity();
                                                                    if (activity.getPackageManager().resolveActivity(shareIntent, 0) != null) {
                                                                        activity.startActivityForResult(shareIntent, 0);
                                                                    }
                                                                }
                                                                break;


                                                            case VideoAction_F.FACEBOOK_SHARE:

                                                                if (isPackageInstalled("com.facebook.katana")) {
                                                                    Intent intent = new Intent("com.facebook.feeds.ADD_TO_FEED");
                                                                    intent.setDataAndType(uri, "video/*");
                                                                    intent.setPackage("com.facebook.katana");

                                                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                    intent.putExtra("com.facebook.platform.extra.APPLICATION_ID", getContext().getPackageName());
                                                                    Activity activity = getActivity();
                                                                    if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
                                                                        activity.startActivityForResult(intent, 0);
                                                                    }

                                                                }
                                                                break;


                                                            case VideoAction_F.FACEBOOK_STORY_SHARE:

                                                                if (isPackageInstalled("com.facebook.katana")) {
                                                                    Intent intent = new Intent("com.facebook.stories.ADD_TO_STORY");
                                                                    intent.setDataAndType(uri, "video/*");
                                                                    intent.setPackage("com.facebook.katana");
                                                                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                                                    intent.putExtra("com.facebook.platform.extra.APPLICATION_ID", getContext().getPackageName());
//                                                                    intent.putExtra("content_url", attributionLinkUrl);

// Instantiate activity and verify it will resolve implicit intent
                                                                    Activity activity = getActivity();
                                                                    if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
                                                                        activity.startActivityForResult(intent, 0);
                                                                    }

                                                                }
                                                                break;

                                                        }


                                                    }
                                                });

                                    }


                                }

                                public boolean isPackageInstalled(String packagename) {
                                    PackageManager pm = getContext().getPackageManager();
                                    try {
                                        pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
                                        return true;
                                    } catch (PackageManager.NameNotFoundException e) {
                                        Toast.makeText(context, "Not installed", Toast.LENGTH_SHORT).show();
                                        return false;

                                    }

                                }

                                @Override
                                public void onError(Error error) {
                                    Delete_file_no_watermark(item);
                                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                    Functions.cancel_determinent_loader();
                                }


                            });

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });


    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public void Delete_file_no_watermark(Home_Get_Set item) {
        File file = new File(Variables.app_folder + item.video_id + ".mp4");
        if (file.exists()) {
            file.delete();
        }
    }

    public void Scan_file(Home_Get_Set item) {
        MediaScannerConnection.scanFile(getActivity(),
                new String[]{Variables.app_folder + item.video_id + ".mp4"},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {

                        Log.i("ExternalStorage", "Scanned " + path + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
    }

    public void Duet_video(final Home_Get_Set item) {

        Log.d(Variables.tag, item.video_url);
        if (item.video_url != null) {

            Functions.Show_determinent_loader(context, false, false);
            PRDownloader.initialize(getActivity().getApplicationContext());
            DownloadRequest prDownloader = PRDownloader.download(item.video_url, Variables.app_folder, item.video_id + ".mp4")
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {

                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {

                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {

                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {
                            int prog = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                            Functions.Show_loading_progress(prog);

                        }
                    });


            prDownloader.start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    Functions.cancel_determinent_loader();

                    Open_duet_Recording(item);

                }

                @Override
                public void onError(Error error) {

                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    Functions.cancel_determinent_loader();
                }


            });

        }

    }


    public void Open_duet_Recording(Home_Get_Set item) {

        Intent intent = new Intent(getActivity(), Video_Recoder_Duet_A.class);
        intent.putExtra("data", item);
        startActivity(intent);

    }


    public boolean is_fragment_exits() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            return false;
        } else {
            return true;
        }

    }

    // this is lifecyle of the Activity which is importent for play,pause video or relaese the player
    @Override
    public void onResume() {
        super.onResume();
        if ((privious_player != null && (is_visible_to_user && !is_user_stop_video)) && !is_fragment_exits()) {
            privious_player.setPlayWhenReady(true);
        }

//        if(proxy == null)
//        {
//            proxy = MainAppClass.getProxy(context);
//        }

    }


    @Override
    public void onPause() {
        super.onPause();
        if (privious_player != null) {
            privious_player.setPlayWhenReady(false);
        }

//        if(proxy != null)
//        {
//            proxy.shutdown();
//        }

    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d("101010", "onStop :");

        if (privious_player != null) {
            privious_player.setPlayWhenReady(false);
        }

//        if(proxy != null)
//        {
//            proxy.shutdown();
//        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (privious_player != null) {
            privious_player.release();
        }

        if (mReceiver != null) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }

//        if(proxy != null)
//        {
//            proxy.shutdown();
//        }

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


    // Bottom all the function and the Call back listener of the Expo player
    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }


    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }


    @Override
    public void onLoadingChanged(boolean isLoading) {

    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if (playbackState == Player.STATE_BUFFERING) {
            progressBar.setVisibility(View.GONE);
//           img_thumb.setVisibility(View.VISIBLE);
//            rlt_temb.setVisibility(View.VISIBLE);
        } else if (playbackState == Player.STATE_READY) {
            progressBar.setVisibility(View.GONE);
//            img_thumb.setVisibility(View.GONE);
//            rlt_temb.setVisibility(View.GONE);
        }


    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }


    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying) {
        if (isPlaying) {
            // p_bar.setVisibility(View.GONE);

            //  img_thumb.animate().alpha(0.0f).setDuration(200);
            img_temp.animate().alpha(0.0f);
            // img_thumb.setVisibility(View.GONE);
            //  rlt_temb.setVisibility(View.GONE);
        } else {
            //  p_bar.setVisibility(View.VISIBLE);

        }
    }

    public void DialogUserHasTag(String imgUrl) {

        final Dialog dialog = new Dialog(context,
                android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_hastag);
        dialog.setTitle("Title...");

        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView imgclosetop = (ImageView) dialog.findViewById(R.id.imgclosetop);
        imgclosetop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        imgclosetop.startAnimation(animBlink);


        ImageView img_diaProfile = dialog.findViewById(R.id.img_diaProfile);
        final View lnr_progress = dialog.findViewById(R.id.lnr_progress);
        String imagepath = "";

        if (imgUrl != null && imgUrl.contains("http"))
            imagepath = imgUrl;
        else
            imagepath = Variables.IMGPATH + imgUrl;


        Picasso.get().load(imagepath).into(img_diaProfile, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {

                if (lnr_progress.getVisibility() == View.VISIBLE) {
                    lnr_progress.setVisibility(View.GONE);
                }


            }

            @Override
            public void onError(Exception e) {

                if (lnr_progress.getVisibility() == View.VISIBLE) {
                    lnr_progress.setVisibility(View.GONE);
                }
            }
        });

        dialog.show();

    }

    private void showHasTag() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Variables.HASTAG,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");


                            if (code.equalsIgnoreCase("200")) {
                                //  imghastag.setVisibility(View.VISIBLE);
                                JSONArray jsonArray = jsonObject.getJSONArray("List");

                                if (jsonArray.length() > 0) {

                                    for (int i = 0; i < jsonArray.length(); i++) {

                                        JSONObject dataObject = jsonArray.getJSONObject(i);

                                        String imgurl = dataObject.getString("img");
                                        DialogUserHasTag(imgurl);

                                    }
                                }

                                // Toast.makeText(Homepage.this, ""+message, Toast.LENGTH_SHORT).show();

                            } else {

                                // imghastag.setVisibility(View.GONE);
                                if (jsonObject.has("message")) {

//                                    Toast.makeText(Homepage.this, message,
//                                            Toast.LENGTH_LONG).show();


                                }


                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  progressDialog.dismiss();
                // Toast.makeText(Homepage.this, "Something went wrong", Toast.LENGTH_LONG).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                params.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // headers.put("token", Const.TOKEN);
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);

    }

    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
