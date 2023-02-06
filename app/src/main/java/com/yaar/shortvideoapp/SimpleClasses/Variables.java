package com.yaar.shortvideoapp.SimpleClasses;

import android.content.SharedPreferences;
import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by AQEEL on 2/15/2019.
 */

public class Variables {


    public static final String device="android";

    public static int screen_width;
    public static int screen_height;

    public static final String SelectedAudio_AAC ="SelectedAudio.aac";

    public static final String root= Environment.getExternalStorageDirectory().toString();
//    public static final String app_hided_folder =root+"/.HidedTicTic/";
    public static final String app_showing_folder =root+"/megaplay/";
    public static final String app_folder =root+"/megaplay/";
    public static final String draft_app_folder= app_showing_folder +"Draft/";


    public static int max_recording_duration=18000;
    public static int recording_duration=18000;
    public static int min_time_recording=5000;

    public static String output_frontcamera= app_showing_folder + "output_frontcamera.mp4";
    public static String outputfile= app_showing_folder + "output.mp4";
    public static String outputfile2= app_showing_folder + "output2.mp4";
    public static String output_filter_file= app_showing_folder + "output-filtered.mp4";
    public static String gallery_trimed_video= app_showing_folder + "gallery_trimed_video.mp4";
    public static String gallery_resize_video= app_showing_folder + "gallery_resize_video.mp4";



    public static SharedPreferences sharedPreferences;
    public static final String pref_name="pref_name";
    public static final String u_id="u_id";
    public static final String u_name="u_name";
    public static final String u_pic="u_pic";
    public static final String f_name="f_name";
    public static final String l_name="l_name";
    public static final String mobile="mobile";
    public static final String gender="u_gender";
    public static final String islogin="is_login";
    public static final String device_token="device_token";
    public static final String api_token="api_token";
    public static final String device_id="device_id";
    public static final String uploading_video_thumb="uploading_video_thumb";

    public static String user_id;
    public static String user_name;
    public static String user_pic;



    public static String tag="megaplay_";

    public static String Selected_sound_id="null";

    public static  boolean Reload_my_videos=false;
    public static  boolean Reload_my_videos_inner=false;
    public static  boolean Reload_my_likes_inner=false;
    public static  boolean Reload_my_notification=false;


    public static final String gif_firstpart="https://media.giphy.com/media/";
    public static final String gif_secondpart="/100w.gif";

    public static final String gif_firstpart_chat="https://media.giphy.com/media/";
    public static final String gif_secondpart_chat="/200w.gif";


    public static final SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);

    public static final SimpleDateFormat df2 =
            new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.ENGLISH);



    // if you want a user can't share a video from your app then you have to set this value to true
    public static final boolean is_secure_info=false;

    // if you want a ads did not show into your app then set the belue valuw to true.
    public static final boolean is_remove_ads=false;

    // if you want a video thumnail image show rather then a video gif then set the below value to false.
    public static final boolean is_show_gif=true;

    // if you want to add a limit that a user can watch only 6 video then change the below value to true
    // if you want to change the demo videos limit count then set the count as you want
    public static final boolean is_demo_app=false;
    public static final int demo_app_videos_count=6;


    // if you want to add a duet function into our project you have to set this value to "true"
    // and also get the extended apis
    public static final boolean is_enable_duet=false;



    public final static int permission_camera_code=786;
    public final static int permission_write_data=788;
    public final static int permission_Read_data=789;
    public final static int permission_Recording_audio=790;
    public final static int Pick_video_from_gallery=791;




   public static String gif_api_key1="giphy_api_key_here";

    public static final String privacy_policy="https://google.com";
    public static final String terms_condi="https://google.com";


//    public static final String privacy_policy="http://167.71.232.99/palanglightapp/megaplay_privacy_policy.html";
//    public static final String terms_condi="http://167.71.232.99/palanglightapp/PALANAGTNC.html";



   // public static final  String main_domain="http://167.71.232.99/palanglightapp/";
   // public static final  String main_domain="https://megaplaystar.com/palanglightapp/";
    public static final  String main_domain="http://143.110.179.120/yaar/";
  //  public static final  String main_domain="http://api.megaplaystar.com/megaplay/";
    public static final String base_url=main_domain+"aps/";
    public static final String api_domain =base_url+"indexV1.php?p=";


    public static final String HASTAG = main_domain+"panel/api/banner/list";
    public static final String DELETE_COMMENT = main_domain+"panel/api/user/delete_comment";
    public static final String IMGPATH = main_domain+"panel/upload/";

    public static final String SignUp = api_domain +"signup";
    public static final String uploadVideo = api_domain +"uploadVideo";
    public static final String showAllVideos = api_domain +"showAllVideos";
    public static final String settings = main_domain +"panel/api/Setting/list";
    public static final String showMyAllVideos= api_domain +"showMyAllVideos";
    public static final String likeDislikeVideo= api_domain +"likeDislikeVideo";
    public static final String updateVideoView= api_domain +"updateVideoView";
    public static final String allSounds= api_domain +"allSounds";
    public static final String fav_sound= api_domain +"fav_sound";
    public static final String my_FavSound= api_domain +"my_FavSound";
    public static final String my_liked_video= api_domain +"my_liked_video";
    public static final String follow_users= api_domain +"follow_users";
    public static final String discover= api_domain +"discover";
    public static final String showVideoComments= api_domain +"showVideoComments";
    public static final String postComment= api_domain +"postComment";
    public static final String edit_profile= api_domain +"edit_profile";
    public static final String get_user_data= api_domain +"get_user_data";
    public static final String get_followers= api_domain +"get_followers";
    public static final String get_followings= api_domain +"get_followings";
    public static final String SearchByHashTag= api_domain +"SearchByHashTag";
    public static final String sendPushNotification= api_domain +"sendPushNotification";
    public static final String uploadImage= api_domain +"uploadImage";
    public static final String DeleteVideo= api_domain +"DeleteVideo";
    public static final String search= api_domain +"search";
    public static final String getNotifications= api_domain +"getNotifications";
    public static final String getVerified= api_domain +"getVerified";
    public static final String downloadFile= api_domain +"downloadFile";

    public static final String GiftList = main_domain +"panel/api/gift";
    public static final String Buy_Gift = main_domain + "panel/api/gift/place_order";

    public static final String PAY_NOW = main_domain + "panel/api/gift/pay_now";

    public static final String GiftListPATH = main_domain +"panel/upload/";

    public static final String language = "language";
    public static final String languageCode = "language_code";
}
