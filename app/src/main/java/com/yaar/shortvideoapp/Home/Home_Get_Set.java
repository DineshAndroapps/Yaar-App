package com.yaar.shortvideoapp.Home;

import java.io.Serializable;

/**
 * Created by AQEEL on 2/18/2019.
 */

public class Home_Get_Set implements Serializable {
    public String fb_id,username,first_name,last_name,profile_pic,verified,paidtype;
    public String video_id,video_description,video_url,gif,thum,created_date;

    public String sound_id,sound_name,sound_pic,sound_url_acc,sound_url_mp3;

    public String privacy_type,allow_comments,allow_duet,liked,like_count,video_comment_count,views,followed;

}
