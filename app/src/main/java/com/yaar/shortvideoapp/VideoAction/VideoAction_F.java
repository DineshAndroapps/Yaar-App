package com.yaar.shortvideoapp.VideoAction;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.yaar.shortvideoapp.Accounts.Login_A;
import com.yaar.shortvideoapp.Home.Home_Get_Set;
import com.yaar.shortvideoapp.SimpleClasses.Functions;
import com.yaar.shortvideoapp.SimpleClasses.Variables;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.yaar.shortvideoapp.R;
import com.yaar.shortvideoapp.SimpleClasses.Fragment_Callback;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoAction_F extends BottomSheetDialogFragment implements View.OnClickListener {

    public static final String NORMAL_SHARE = "NORMAL_SHARE";
    public static final String WHATSAPP_SHARE = "WHATSAPP_SHARE";
    public static final String FACEBOOK_SHARE = "FACEBOOK_SHARE";
    public static final String FACEBOOK_STORY_SHARE = "FACEBOOK_STORY_SHARE";
    public static final String YOUTUBE_SHARE = "YOUTUBE_SHARE";
    public static final String INSTRAGRAM_SHARE = "INSTRAGRAM_SHARE";
    public static final String INSTRAGRAM_STORY_SHARE = "INSTRAGRAM_STORY_SHARE";

    View view;
    Context context;
    RecyclerView recyclerView;

    Fragment_Callback fragment_callback;

    String video_id, user_id;

    ProgressBar progressBar;
    Home_Get_Set item;


    public VideoAction_F() {
    }

    @SuppressLint("ValidFragment")
    public VideoAction_F(String id, Fragment_Callback fragment_callback) {
        video_id = id;
        this.fragment_callback = fragment_callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_video_action, container, false);
        context = getContext();

        Bundle bundle = getArguments();
        if (bundle != null) {
            item = (Home_Get_Set) bundle.getSerializable("data");
            video_id = bundle.getString("video_id");
            user_id = bundle.getString("user_id");
        }

        progressBar = view.findViewById(R.id.progress_bar);

        view.findViewById(R.id.instagram_story).setOnClickListener(this);
        view.findViewById(R.id.instagram).setOnClickListener(this);
        view.findViewById(R.id.youtube).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        view.findViewById(R.id.facebook_story).setOnClickListener(this);
        view.findViewById(R.id.facebook).setOnClickListener(this);
        view.findViewById(R.id.whatsApp).setOnClickListener(this);
        view.findViewById(R.id.save_video_layout).setOnClickListener(this);
        view.findViewById(R.id.copy_layout).setOnClickListener(this);
        view.findViewById(R.id.save_layout).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);

//        if (user_id != null && user_id.equals(Variables.sharedPreferences.getString(Variables.u_id, "")))
//            view.findViewById(R.id.delete_layout).setVisibility(View.VISIBLE);
//        else
//            view.findViewById(R.id.delete_layout).setVisibility(View.GONE);


        if (Variables.is_secure_info) {
            view.findViewById(R.id.share_notice_txt).setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            view.findViewById(R.id.copy_layout).setVisibility(View.GONE);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    //Get_Shared_app();

                }
            }, 1000);
        }


//        if(Variables.is_enable_duet && (item.allow_duet!=null && item.allow_duet.equalsIgnoreCase("1"))) {
        // view.findViewById(R.id.duet_layout).setVisibility(View.VISIBLE);
        view.findViewById(R.id.duet_layout).setOnClickListener(this);
//        }
//        else {
//            view.findViewById(R.id.duet_layout).setVisibility(View.GONE);
//        }

        return view;
    }

//    VideoSharingApps_Adapter adapter;
//    public void Get_Shared_app(){
//        recyclerView = (RecyclerView) view.findViewById(R.id.recylerview);
//        final GridLayoutManager layoutManager = new GridLayoutManager(context, 5);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(false);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//
//                    PackageManager pm=getActivity().getPackageManager();
//                    Intent intent = new Intent(Intent.ACTION_SEND);
//                    intent.setType("text/plain");
//                    intent.putExtra(Intent.EXTRA_TEXT, "https://google.com");
//
//                    List<ResolveInfo> launchables=pm.queryIntentActivities(intent, 0);
//
//                    for (int i=0; i<launchables.size(); i++){
//
//                        if(launchables.get(i).activityInfo.name.contains("SendTextToClipboardActivity")){
//                            launchables.remove(i);
//                            break;
//                        }
//
//                    }
//
//                    Collections.sort(launchables,
//                            new ResolveInfo.DisplayNameComparator(pm));
//
//                    adapter=new VideoSharingApps_Adapter(context, launchables, new VideoSharingApps_Adapter.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(int positon, ResolveInfo item, View view) {
//                            Toast.makeText(context, ""+item.activityInfo.name, Toast.LENGTH_SHORT).show();
//                            Open_App(item);
//                        }
//                    });
//
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            recyclerView.setAdapter(adapter);
//                            progressBar.setVisibility(View.GONE);
//                        }
//                    });
//
//
//                }
//                catch (Exception e){
//
//                }
//            }
//        }).start();
//
//
//
//    }


//    public void Open_App(ResolveInfo resolveInfo) {
//        try {
//
//            ActivityInfo activity = resolveInfo.activityInfo;
//            ComponentName name = new ComponentName(activity.applicationInfo.packageName,
//                    activity.name);
//            Intent i = new Intent(Intent.ACTION_MAIN);
//
//            i.addCategory(Intent.CATEGORY_LAUNCHER);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
//                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//            i.setComponent(name);
//
//            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
//            intent.setType("text/plain");
//            intent.putExtra(Intent.EXTRA_TEXT, Variables.main_domain+video_id);
//            intent.setComponent(name);
//            startActivity(intent);
//        }catch (Exception e){
//
//        }
//    }

    private void callback(String action) {

        if (Functions.Checkstoragepermision(getActivity())) {

            Bundle bundle = new Bundle();
            bundle.putString("action", action);
            dismiss();
            fragment_callback.Responce(bundle);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.save_video_layout:

                callback(NORMAL_SHARE);

                break;

            case R.id.whatsApp:

                callback(WHATSAPP_SHARE);

                break;

            case R.id.instagram:

                callback(INSTRAGRAM_SHARE);

                break;

            case R.id.instagram_story:

                callback(INSTRAGRAM_STORY_SHARE);

                break;

            case R.id.facebook:

                callback(NORMAL_SHARE);

                break;

            case R.id.facebook_story:

                callback(NORMAL_SHARE);

                break;

                case R.id.youtube:

                callback(NORMAL_SHARE);

                break;

            case R.id.duet_layout:

                if (!Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                    Open_Login();
                } else {
                    callback("duet");
                }

                break;

            case R.id.copy_layout:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Don't miss the chance to share your talent with world!!Just hit the below link and become star.Download the Yaar app now " + " https://play.google.com/store/apps/details?id=com.yaar.shortvideoapp");

                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                break;

            case R.id.save_layout:

                    callback("save");

                break;

        }
    }

    public void Open_Login() {
        Intent intent = new Intent(getActivity(), Login_A.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }


}
