package com.yaar.shortvideoapp.Video_Recording;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.yaar.shortvideoapp.BaseActivity;
import com.yaar.shortvideoapp.Main_Menu.MainMenuActivity;
import com.yaar.shortvideoapp.R;
import com.yaar.shortvideoapp.Services.ServiceCallback;
import com.yaar.shortvideoapp.Services.Upload_Service;
import com.yaar.shortvideoapp.SimpleClasses.Functions;
import com.yaar.shortvideoapp.SimpleClasses.Variables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Post_Video_A extends BaseActivity implements ServiceCallback,View.OnClickListener {


    ImageView video_thumbnail;
    String video_path;

    ServiceCallback serviceCallback;
    EditText description_edit;

    String draft_file,duet_video_id;

    TextView privcy_type_txt;
    Switch comment_switch,duet_switch;

    Bitmap bmThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);

        Intent intent=getIntent();
        if(intent!=null){
            draft_file=intent.getStringExtra("draft_file");
            duet_video_id=intent.getStringExtra("duet_video_id");
        }


        video_path = Variables.output_filter_file;
        video_thumbnail = findViewById(R.id.video_thumbnail);


        description_edit=findViewById(R.id.description_edit);

        // this will get the thumbnail of video and show them in imageview

        bmThumbnail = ThumbnailUtils.createVideoThumbnail(video_path,
                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

        if(bmThumbnail != null && duet_video_id!=null){
            Bitmap duet_video_bitmap = null;
            if(duet_video_id!=null){
                duet_video_bitmap= ThumbnailUtils.createVideoThumbnail(Variables.app_folder+duet_video_id+".mp4",
                        MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
            }
            Bitmap combined=combineImages(bmThumbnail,duet_video_bitmap);
            video_thumbnail.setImageBitmap(combined);
            String bitmapstr = Functions.Bitmap_to_base64(this,combined);
            if (bitmapstr != null){

                Variables.sharedPreferences.edit().putString(Variables.uploading_video_thumb,bitmapstr).commit();


            }
           // Variables.sharedPreferences.edit().putString(Variables.uploading_video_thumb,Functions.Bitmap_to_base64(this,combined)).commit();

        }
        else if(bmThumbnail != null){
            video_thumbnail.setImageBitmap(bmThumbnail);

            String bitmapstr = Functions.Bitmap_to_base64(this,bmThumbnail);
            if (bitmapstr != null){

                Variables.sharedPreferences.edit().putString(Variables.uploading_video_thumb,bitmapstr).commit();


            }


        }



      privcy_type_txt=findViewById(R.id.privcy_type_txt);
      comment_switch=findViewById(R.id.comment_switch);
      duet_switch=findViewById(R.id.duet_switch);


      findViewById(R.id.Goback).setOnClickListener(this);

      findViewById(R.id.privacy_type_layout).setOnClickListener(this);
      findViewById(R.id.post_btn).setOnClickListener(this);
      findViewById(R.id.save_draft_btn).setOnClickListener(this);



      if(duet_video_id!=null){
          findViewById(R.id.duet_layout).setVisibility(View.GONE);
          findViewById(R.id.save_draft_btn).setVisibility(View.GONE);
          duet_switch.setChecked(false);
      }

      else if(Variables.is_enable_duet)
          findViewById(R.id.duet_layout).setVisibility(View.VISIBLE);

      else {
          findViewById(R.id.duet_layout).setVisibility(View.GONE);
          duet_switch.setChecked(false);
      }

}


    public Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        if(c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        return cs;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.Goback:
                onBackPressed();
                break;

            case R.id.privacy_type_layout:
                Privacy_dialog();
                break;

            case R.id.save_draft_btn:
                Save_file_in_draft();
                break;

            case R.id.post_btn:
                Start_Service();
                break;
        }
    }



    private void Privacy_dialog() {
        final CharSequence[] options = new CharSequence[]{"Public","Private"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {
                privcy_type_txt.setText(options[item]);

            }

        });

        builder.show();

    }




    // this will start the service for uploading the video into database
    public void Start_Service(){

        serviceCallback=this;

        Upload_Service mService = new Upload_Service(serviceCallback);
        if (!Functions.isMyServiceRunning(this,mService.getClass())) {
            Intent mServiceIntent = new Intent(this.getApplicationContext(), mService.getClass());
            mServiceIntent.setAction("startservice");
            mServiceIntent.putExtra("draft_file",draft_file);
            mServiceIntent.putExtra("duet_video_id",duet_video_id);
            mServiceIntent.putExtra("uri",""+ video_path);
            mServiceIntent.putExtra("desc",""+description_edit.getText().toString());
            mServiceIntent.putExtra("privacy_type",privcy_type_txt.getText().toString());

            if(comment_switch.isChecked())
              mServiceIntent.putExtra("allow_comment","true");
             else
                mServiceIntent.putExtra("allow_comment","false");

             if(duet_switch.isChecked())
                 mServiceIntent.putExtra("allow_duet","1");
             else
                 mServiceIntent.putExtra("allow_duet","0");

            startService(mServiceIntent);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendBroadcast(new Intent("uploadVideo"));
                    startActivity(new Intent(Post_Video_A.this, MainMenuActivity.class));
                }
            },1000);


        }
        else {
            Toast.makeText(this, "Please wait video already in uploading progress", Toast.LENGTH_LONG).show();
        }


    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }


    // when the video is uploading successfully it will restart the appliaction
    @Override
    public void ShowResponce(final String responce) {

        if(mConnection!=null)
        unbindService(mConnection);

        Toast.makeText(this, responce, Toast.LENGTH_SHORT).show();
    }



    // this is importance for binding the service to the activity
    Upload_Service mService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

           Upload_Service.LocalBinder binder = (Upload_Service.LocalBinder) service;
            mService = binder.getService();

            mService.setCallbacks(Post_Video_A.this);



        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };


    @Override
    protected void onDestroy() {
        if(bmThumbnail!=null){
            bmThumbnail.recycle();
        }
        super.onDestroy();
    }


    public void Save_file_in_draft(){
       File source = new File(video_path);
       File destination = new File(Variables.draft_app_folder+Functions.getRandomString()+".mp4");
        try
        {
            if(source.exists()){

                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(destination);

                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

                Toast.makeText(Post_Video_A.this, "File saved in Draft", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Post_Video_A.this, MainMenuActivity.class));

            }else{
                Toast.makeText(Post_Video_A.this, "File failed to saved in Draft", Toast.LENGTH_SHORT).show();

            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }



}
