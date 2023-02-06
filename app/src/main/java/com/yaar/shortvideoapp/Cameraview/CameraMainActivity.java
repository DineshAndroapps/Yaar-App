package com.yaar.shortvideoapp.Cameraview;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yaar.shortvideoapp.BaseActivity;
import com.yaar.shortvideoapp.R;
import com.yaar.shortvideoapp.SegmentProgress.ProgressBarListener;
import com.yaar.shortvideoapp.SegmentProgress.SegmentedProgressBar;
import com.yaar.shortvideoapp.SimpleClasses.Functions;
import com.yaar.shortvideoapp.SimpleClasses.Variables;
import com.yaar.shortvideoapp.SoundLists.SoundList_Main_A;
import com.yaar.shortvideoapp.Video_Recording.Merge_Video_Audio;
import com.yaar.shortvideoapp.Video_Recording.Post_Video_A;
import com.yaar.shortvideoapp.Video_Recording.Preview_Video_A;
import com.coremedia.iso.boxes.Container;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.controls.Preview;
import com.otaliastudios.cameraview.filter.Filters;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class CameraMainActivity extends BaseActivity implements View.OnClickListener, OptionView.Callback {

    private final static CameraLogger LOG = CameraLogger.create("DemoApp");
    private final static boolean USE_FRAME_PROCESSOR = true;
    private final static boolean DECODE_BITMAP = false;

    private CameraView camera;
    private ViewGroup controlPanel;
    private long mCaptureTime;

    private int mCurrentFilter = 0;
    private final Filters[] mAllFilters = Filters.values();

    ImageView record_image,cut_video_btn;

    public static int Sounds_list_Request_code=1;
    TextView add_sound_txt;
    View lnr_addsound;



    int sec_passed=0;
    long time_in_milis=0;
    SegmentedProgressBar video_progress;
    boolean is_recording_timer_enable;
    int recording_time=3;
    int number=0;
    boolean is_recording=false;
    ArrayList<String> videopaths=new ArrayList<>();
    ImageView done_btn;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cameramain);
        CameraLogger.setLogLevel(CameraLogger.LEVEL_VERBOSE);

        Variables.Selected_sound_id="null";
        Variables.recording_duration=Variables.max_recording_duration;

        add_sound_txt=findViewById(R.id.add_sound_txt);
        lnr_addsound=findViewById(R.id.lnr_addsound);
        record_image=findViewById(R.id.record_image);
        cut_video_btn=findViewById(R.id.cut_video_btn);

        done_btn=findViewById(R.id.done);
        done_btn.setEnabled(false);
        done_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                append();
            }
        });

        lnr_addsound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(CameraMainActivity.this, SoundList_Main_A.class);
                startActivityForResult(intent,Sounds_list_Request_code);
                overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
            }
        });

        camera = findViewById(R.id.camera);
        camera = findViewById(R.id.camera);
        camera.setLifecycleOwner(this);
        camera.addCameraListener(new Listener());

        ((View) findViewById(R.id.Goback)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (USE_FRAME_PROCESSOR) {
            camera.addFrameProcessor(new FrameProcessor() {
                private long lastTime = System.currentTimeMillis();

                @Override
                public void process(@NonNull Frame frame) {
                    long newTime = frame.getTime();
                    long delay = newTime - lastTime;
                    lastTime = newTime;
                    LOG.v("Frame delayMillis:", delay, "FPS:", 1000 / delay);
                    if (DECODE_BITMAP) {
                        if (frame.getFormat() == ImageFormat.NV21
                                && frame.getDataClass() == byte[].class) {
                            byte[] data = frame.getData();
                            YuvImage yuvImage = new YuvImage(data,
                                    frame.getFormat(),
                                    frame.getSize().getWidth(),
                                    frame.getSize().getHeight(),
                                    null);
                            ByteArrayOutputStream jpegStream = new ByteArrayOutputStream();
                            yuvImage.compressToJpeg(new Rect(0, 0,
                                    frame.getSize().getWidth(),
                                    frame.getSize().getHeight()), 100, jpegStream);
                            byte[] jpegByteArray = jpegStream.toByteArray();
                            Bitmap bitmap = BitmapFactory.decodeByteArray(jpegByteArray,
                                    0, jpegByteArray.length);
                            //noinspection ResultOfMethodCallIgnored
                            bitmap.toString();
                        }
                    }
                }
            });
        }

        findViewById(R.id.edit).setOnClickListener(this);
        findViewById(R.id.capturePicture).setOnClickListener(this);
        findViewById(R.id.capturePictureSnapshot).setOnClickListener(this);
        findViewById(R.id.captureVideo).setOnClickListener(this);
        findViewById(R.id.captureVideoSnapshot).setOnClickListener(this);
        findViewById(R.id.toggleCamera).setOnClickListener(this);
        findViewById(R.id.changeFilter).setOnClickListener(this);

        initlize_Video_progress();

        controlPanel = findViewById(R.id.controls);
        ViewGroup group = (ViewGroup) controlPanel.getChildAt(0);
        final View watermark = findViewById(R.id.watermark);

        List<Option<?>> options = Arrays.asList(
                // Layout
                new Option.Width(), new Option.Height(),
                // Engine and preview
                new Option.Mode(), new Option.Engine(), new Option.Preview(),
                // Some controls
                new Option.Flash(), new Option.WhiteBalance(), new Option.Hdr(),
                new Option.PictureMetering(), new Option.PictureSnapshotMetering(),
                new Option.PictureFormat(),
                // Video recording
                new Option.PreviewFrameRate(), new Option.VideoCodec(), new Option.Audio(), new Option.AudioCodec(),
                // Gestures
                new Option.Pinch(), new Option.HorizontalScroll(), new Option.VerticalScroll(),
                new Option.Tap(), new Option.LongTap(),
                // Watermarks
                new Option.OverlayInPreview(watermark),
                new Option.OverlayInPictureSnapshot(watermark),
                new Option.OverlayInVideoSnapshot(watermark),
                // Frame Processing
                new Option.FrameProcessingFormat(),
                // Other
                new Option.Grid(), new Option.GridColor(), new Option.UseDeviceOrientation()
        );

        List<Boolean> dividers = Arrays.asList(
                // Layout
                false, true,
                // Engine and preview
                false, false, true,
                // Some controls
                false, false, false, false, false, true,
                // Video recording
                false, false, false, true,
                // Gestures
                false, false, false, false, true,
                // Watermarks
                false, false, true,
                // Frame Processing
                true,
                // Other
                false, false, true
        );
        for (int i = 0; i < options.size(); i++) {
            OptionView view = new OptionView(this);
            //noinspection unchecked
            view.setOption(options.get(i), this);
            view.setHasDivider(dividers.get(i));
            group.addView(view,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        controlPanel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
                b.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

        // Animate the watermark just to show we record the animation in video snapshots
        ValueAnimator animator = ValueAnimator.ofFloat(1F, 0.8F);
        animator.setDuration(300);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (float) animation.getAnimatedValue();
                watermark.setScaleX(scale);
                watermark.setScaleY(scale);
                watermark.setRotation(watermark.getRotation() + 2);
            }
        });
        animator.start();
    }

    public void initlize_Video_progress(){
        sec_passed=0;
        video_progress=findViewById(R.id.video_progress);
        video_progress.enableAutoProgressView(Variables.recording_duration);
        video_progress.setDividerColor(Color.WHITE);
        video_progress.setDividerEnabled(true);
        video_progress.setDividerWidth(4);
        video_progress.setShader(new int[]{Color.CYAN, Color.CYAN, Color.CYAN});

        video_progress.SetListener(new ProgressBarListener() {
            @Override
            public void TimeinMill(long mills) {
                time_in_milis=mills;
                sec_passed = (int) (mills/1000);

                if(sec_passed>(Variables.recording_duration/1000)-1){
                    Start_or_Stop_Recording();
                }

                if(is_recording_timer_enable && sec_passed>=recording_time){
                    is_recording_timer_enable=false;
                    Start_or_Stop_Recording();
                }

            }
        });
    }


    public void Start_or_Stop_Recording(){

        if (!camera.isTakingVideo() && sec_passed<(Variables.recording_duration/1000)-1) {
            number=number+1;

            is_recording=true;

            File file = new File(Variables.app_folder +  "myvideo"+(number)+".mp4");
            videopaths.add(Variables.app_folder +  "myvideo"+(number)+".mp4");
//            camera.takeVideoSnapshot(new File(/*getFilesDir(), */Variables.output_filter_file));
            camera.takeVideoSnapshot(file);


            if(audio!=null)
                audio.start();


            done_btn.setBackgroundResource(R.drawable.ic_not_done);
            done_btn.setEnabled(false);

            video_progress.resume();

            record_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_recoding_yes));

            cut_video_btn.setVisibility(View.GONE);


            add_sound_txt.setClickable(false);


        }

        else if (is_recording) {

            is_recording=false;

            video_progress.pause();
            video_progress.addDivider();

            if(audio!=null)
                audio.pause();

            camera.stopVideo();


            Check_done_btn_enable();

            cut_video_btn.setVisibility(View.VISIBLE);

            record_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_recoding_no));


        }

        else if(sec_passed>(Variables.recording_duration/1000)){
            Functions.Show_Alert(this,"Alert","Video only can be a "+(int)Variables.recording_duration/1000+" S");
        }



    }

    public void Check_done_btn_enable(){
        if(sec_passed>(Variables.min_time_recording/1000)) {
            done_btn.setBackgroundResource(R.drawable.ic_done);
            done_btn.setEnabled(true);
        }else {
            done_btn.setBackgroundResource(R.drawable.ic_not_done);
            done_btn.setEnabled(false);
        }
    }

    // this will apped all the videos parts in one  fullvideo
    private boolean append() {
        final ProgressDialog progressDialog=new ProgressDialog(CameraMainActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {


                runOnUiThread(new Runnable() {
                    public void run() {

                        progressDialog.setMessage("Please wait..");
                        progressDialog.show();
                    }
                });

                ArrayList<String> video_list=new ArrayList<>();
                for (int i=0;i<videopaths.size();i++){

                    File file=new File(videopaths.get(i));
                    if(file.exists()) {
                        try {
                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(CameraMainActivity.this, Uri.fromFile(file));
                            String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                            boolean isVideo = "yes".equals(hasVideo);

                            if (isVideo && file.length() > 3000) {
                                Log.d("resp", videopaths.get(i));
                                video_list.add(videopaths.get(i));
                            }
                        }catch (Exception e){
                            Log.d(Variables.tag,e.toString());
                        }
                    }
                }

                try {

                    Movie[] inMovies = new Movie[video_list.size()];

                    for (int i=0;i<video_list.size();i++){
                        inMovies[i]= MovieCreator.build(video_list.get(i));
                    }


                    List<Track> videoTracks = new LinkedList<Track>();
                    List<Track> audioTracks = new LinkedList<Track>();
                    for (Movie m : inMovies) {
                        for (Track t : m.getTracks()) {
                            if (t.getHandler().equals("soun")) {
                                audioTracks.add(t);
                            }
                            if (t.getHandler().equals("vide")) {
                                videoTracks.add(t);
                            }
                        }
                    }
                    Movie result = new Movie();
                    if (audioTracks.size() > 0) {
                        result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                    }
                    if (videoTracks.size() > 0) {
                        result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                    }

                    Container out = new DefaultMp4Builder().build(result);

                    String outputFilePath=null;
                    if(audio!=null){
                        outputFilePath=Variables.outputfile;
                    }else {
                        outputFilePath=Variables.outputfile2;
                    }

                    FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
                    out.writeContainer(fos.getChannel());
                    fos.close();

                    runOnUiThread(new Runnable() {
                        public void run() {

                            progressDialog.dismiss();
                            if(audio!=null)
                                Merge_withAudio();
                            else {
//                                camera.stopVideo();
                                Go_To_preview_Activity();
                            }

                        }
                    });



                } catch (Exception e) {
                    Toast.makeText(CameraMainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }
        }).start();



        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == Sounds_list_Request_code) {
                if (data != null) {

                    if (data.getStringExtra("isSelected").equals("yes")) {
                        add_sound_txt.setText(data.getStringExtra("sound_name"));
                        Variables.Selected_sound_id = data.getStringExtra("sound_id");
                        PreparedAudio();
                    }

                }

            }
        }

    }

    // this will play the sound with the video when we select the audio
    MediaPlayer audio;
    public  void PreparedAudio(){

        File file=new File(Variables.app_folder+ Variables.SelectedAudio_AAC);
        if(file.exists()) {
            audio = new MediaPlayer();
            try {
                audio.setDataSource(Variables.app_folder + Variables.SelectedAudio_AAC);
                audio.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, Uri.fromFile(file));
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            if(file_duration<Variables.max_recording_duration){
                Variables.recording_duration=file_duration;
//                initlize_Video_progress();
            }

        }

    }

    public void Merge_withAudio(){

        String audio_file;
        audio_file =Variables.app_folder+Variables.SelectedAudio_AAC;


        Merge_Video_Audio merge_video_audio=new Merge_Video_Audio(CameraMainActivity.this);
        merge_video_audio.doInBackground(audio_file,Variables.outputfile,Variables.outputfile2);

    }



    private void message(@NonNull String content, boolean important) {
        if (important) {
            LOG.w(content);
            Toast.makeText(this, content, Toast.LENGTH_LONG).show();
        } else {
            LOG.i(content);
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
        }
    }

    private class Listener extends CameraListener {

        @Override
        public void onCameraOpened(@NonNull CameraOptions options) {
            ViewGroup group = (ViewGroup) controlPanel.getChildAt(0);

            for (int i = 0; i < group.getChildCount(); i++) {
                OptionView view = (OptionView) group.getChildAt(i);
                view.onCameraOpened(camera, options);
            }
        }

        @Override
        public void onCameraError(@NonNull CameraException exception) {
            super.onCameraError(exception);
            message("Got CameraException #" + exception.getReason(), true);
        }

        @Override
        public void onPictureTaken(@NonNull PictureResult result) {
            super.onPictureTaken(result);
            if (camera.isTakingVideo()) {
                message("Captured while taking video. Size=" + result.getSize(), false);
                return;
            }

            // This can happen if picture was taken with a gesture.
            long callbackTime = System.currentTimeMillis();
            if (mCaptureTime == 0) mCaptureTime = callbackTime - 300;
            LOG.w("onPictureTaken called! Launching activity. Delay:", callbackTime - mCaptureTime);
//            PicturePreviewActivity.setPictureResult(result);
//            Intent intent = new Intent(CameraActivity.this, PicturePreviewActivity.class);
//            intent.putExtra("delay", callbackTime - mCaptureTime);
//            startActivity(intent);
            mCaptureTime = 0;
            LOG.w("onPictureTaken called! Launched activity.");
        }

        @Override
        public void onVideoTaken(@NonNull VideoResult result) {
            super.onVideoTaken(result);
            LOG.w("onVideoTaken called! Launching activity.");
//            VideoPreviewActivity.setVideoResult(result);
//            Intent intent = new Intent(CameraActivity.this, VideoPreviewActivity.class);
//            startActivity(intent);
//            GotopostScreen(result);
            LOG.w("onVideoTaken called! Launched activity.");
        }

        public void GotopostScreen(VideoResult result){

            Intent intent =new Intent(CameraMainActivity.this, Post_Video_A.class);
            intent.putExtra("draft_file",Variables.gallery_resize_video);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);

        }


        @Override
        public void onVideoRecordingStart() {
            super.onVideoRecordingStart();
            LOG.w("onVideoRecordingStart!");
        }

        @Override
        public void onVideoRecordingEnd() {
            super.onVideoRecordingEnd();
//            message("Video taken. Processing...", false);
            LOG.w("onVideoRecordingEnd!");
        }

        @Override
        public void onExposureCorrectionChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onExposureCorrectionChanged(newValue, bounds, fingers);
//            message("Exposure correction:" + newValue, false);
        }

        @Override
        public void onZoomChanged(float newValue, @NonNull float[] bounds, @Nullable PointF[] fingers) {
            super.onZoomChanged(newValue, bounds, fingers);
            message("Zoom:" + newValue, false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit: edit(); break;
            case R.id.capturePicture: capturePicture(); break;
            case R.id.capturePictureSnapshot: capturePictureSnapshot(); break;
            case R.id.captureVideo: captureVideo(); break;
            case R.id.captureVideoSnapshot: Start_or_Stop_Recording(); break;
            case R.id.toggleCamera: toggleCamera(); break;
            case R.id.changeFilter: changeCurrentFilter(); break;
        }
    }

    @Override
    public void onBackPressed() {
        BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
        if (b.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            b.setState(BottomSheetBehavior.STATE_HIDDEN);
            return;
        }
        super.onBackPressed();
    }

    private void edit() {
        BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
        b.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void capturePicture() {
        if (camera.getMode() == Mode.VIDEO) {
            message("Can't take HQ pictures while in VIDEO mode.", false);
            return;
        }
        if (camera.isTakingPicture()) return;
        mCaptureTime = System.currentTimeMillis();
        message("Capturing picture...", false);
        camera.takePicture();
    }

    private void capturePictureSnapshot() {
        if (camera.isTakingPicture()) return;
        if (camera.getPreview() != Preview.GL_SURFACE) {
            message("Picture snapshots are only allowed with the GL_SURFACE preview.", true);
            return;
        }
        mCaptureTime = System.currentTimeMillis();
        message("Capturing picture snapshot...", false);
        camera.takePictureSnapshot();
    }

    private void captureVideo() {
        if (camera.getMode() == Mode.PICTURE) {
            message("Can't record HQ videos while in PICTURE mode.", false);
            return;
        }
        if (camera.isTakingPicture() || camera.isTakingVideo()) return;
        message("Recording for 5 seconds...", true);
        camera.takeVideo(new File(getFilesDir(), "video.mp4"), 5000);
    }

    private void captureVideoSnapshot() {
        if (camera.isTakingVideo()) {
            message("Already taking video.", false);
            return;
        }
        if (camera.getPreview() != Preview.GL_SURFACE) {
            message("Video snapshots are only allowed with the GL_SURFACE preview.", true);
            return;
        }
        message("Recording snapshot for 18 seconds...", true);
        camera.takeVideoSnapshot(new File(/*getFilesDir(), */Variables.output_filter_file), 18000);
    }

    private void StopTakenVideos(){

        camera.stopVideo();

    }

    public void Go_To_preview_Activity(){
        Intent intent =new Intent(this,Preview_Video_A.class);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }


    private void toggleCamera() {
        if (camera.isTakingPicture() || camera.isTakingVideo()) return;
        switch (camera.toggleFacing()) {
            case BACK:
                message("Switched to back camera!", false);
                break;

            case FRONT:
                message("Switched to front camera!", false);
                break;
        }
    }

    private void changeCurrentFilter() {
        if (camera.getPreview() != Preview.GL_SURFACE) {
            message("Filters are supported only when preview is Preview.GL_SURFACE.", true);
            return;
        }
        if (mCurrentFilter < mAllFilters.length - 1) {
            mCurrentFilter++;
        } else {
            mCurrentFilter = 0;
        }
        Filters filter = mAllFilters[mCurrentFilter];
        message(filter.toString(), false);

        // Normal behavior:
        camera.setFilter(filter.newInstance());

        // To test MultiFilter:
        // DuotoneFilter duotone = new DuotoneFilter();
        // duotone.setFirstColor(Color.RED);
        // duotone.setSecondColor(Color.GREEN);
        // camera.setFilter(new MultiFilter(duotone, filter.newInstance()));
    }

    @Override
    public <T> boolean onValueChanged(@NonNull Option<T> option, @NonNull T value, @NonNull String name) {
        if ((option instanceof Option.Width || option instanceof Option.Height)) {
            Preview preview = camera.getPreview();
            boolean wrapContent = (Integer) value == ViewGroup.LayoutParams.WRAP_CONTENT;
            if (preview == Preview.SURFACE && !wrapContent) {
                message("The SurfaceView preview does not support width or height changes. " +
                        "The view will act as WRAP_CONTENT by default.", true);
                return false;
            }
        }
        option.set(camera, value);
        BottomSheetBehavior b = BottomSheetBehavior.from(controlPanel);
        b.setState(BottomSheetBehavior.STATE_HIDDEN);
//        message("Changed " + option.getName() + " to " + name, false);
        return true;
    }

    //region Permissions

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean valid = true;
        for (int grantResult : grantResults) {
            valid = valid && grantResult == PackageManager.PERMISSION_GRANTED;
        }
        if (valid && !camera.isOpened()) {
            camera.open();
        }
    }

    //endregion
}
