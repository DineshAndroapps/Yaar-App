package com.cgfay.uitls.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cgfay.uitls.bean.MusicData;
import com.cgfay.uitls.utils.FileUtils;
import com.cgfay.uitls.utils.StringUtils;
import com.cgfay.uitls.utils.Variables;
import com.cgfay.utilslibrary.R;

import java.io.File;




/**
 * 本地音乐适配器
 */
public class LocalMusicAdapter extends LocalCursorAdapter<RecyclerView.ViewHolder> {

    private OnMusicItemSelectedListener mListener;
    Activity activity;


    public LocalMusicAdapter(Cursor cursor, Activity activity) {
        super(cursor);
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_select_view, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");


//        AndroidAudioConverter.load(activity, new ILoadCallback() {
//            @Override
//            public void onSuccess() {
//                // Great!
//            }
//            @Override
//            public void onFailure(Exception error) {
//                // FFmpeg is not supported by device
//            }
//        });

        MusicViewHolder viewHolder = (MusicViewHolder) holder;
        final MusicData musicData = MusicData.valueof(cursor);
        viewHolder.mTextName.setText(musicData.getName());
        viewHolder.mTexDuration.setText(StringUtils.generateMillisTime((int) musicData.getDuration()));
        viewHolder.mLayoutMusic.setOnClickListener(v -> {
            if (mListener != null) {

                String sourcePath = musicData.getPath();
                File source = new File(sourcePath);

                progressDialog.show();

                String destinationPath = Variables.app_folder+ Variables.SelectedAudio_MP3;
                File destination = new File(destinationPath);
//                FileUtils.copyFile(sourcePath, destinationPath);

                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(activity, Uri.fromFile(source));
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                final int file_duration = Integer.parseInt(durationStr);

//                if(file_duration<Variables.max_recording_duration){
                Variables.recording_duration=file_duration;

                int seconds = (int)((file_duration / 1000) % 60);

                progressDialog.dismiss();

                MusicData musicData1 = new MusicData(-1,musicData.getName(),sourcePath,seconds);
                mListener.onMusicItemSelected(musicData1);


//                IConvertCallback callback = new IConvertCallback() {
//                    @Override
//                    public void onSuccess(File convertedFile) {
//
//
//
//
////                }
////                else {
////                    Toast.makeText(activity, "Maximum 18 sec time required", Toast.LENGTH_SHORT).show();
////                }
//
//                    }
//                    @Override
//                    public void onFailure(Exception error) {
//                        // Oops! Something went wrong
//                        progressDialog.dismiss();
//                        Toast.makeText(activity, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
//
//                    }
//                };

//                AndroidAudioConverter.with(activity)
//                        // Your current audio file
//                        .setFile(destination)
//
//                        // Your desired audio format
//                        .setFormat(AudioFormat.AAC)
//
//                        // An callback to know when conversion is finished
//                        .setCallback(callback)
//
//                        // Start conversion
//                        .convert();



            }
        });
    }

    /**
     * 设置选中音乐监听器
     * @param listener
     */
    public void setOnMusicSelectedListener(OnMusicItemSelectedListener listener) {
        mListener = listener;
    }

    /**
     * 音乐选中监听器
     */
    public interface OnMusicItemSelectedListener {
        // 选中音乐
        void onMusicItemSelected(MusicData musicData);
    }


    class MusicViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mLayoutMusic;
        private TextView mTextName;
        private TextView mTexDuration;

        public MusicViewHolder(View itemView) {
            super(itemView);
            mLayoutMusic = (LinearLayout) itemView.findViewById(R.id.layout_item_music);
            mTextName = (TextView) itemView.findViewById(R.id.tv_music_name);
            mTexDuration = (TextView) itemView.findViewById(R.id.tv_music_duration);
        }
    }

}