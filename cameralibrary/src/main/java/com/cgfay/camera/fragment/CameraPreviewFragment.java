package com.cgfay.camera.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.SurfaceTexture;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cgfay.camera.loader.impl.CameraMediaLoader;
import com.cgfay.camera.presenter.CameraPreviewPresenter;
import com.cgfay.camera.widget.CainTextureView;
import com.cgfay.camera.widget.CameraPreviewTopbar;
import com.cgfay.camera.widget.PreviewMeasureListener;
import com.cgfay.camera.widget.RecordButton;
import com.cgfay.camera.widget.RecordCountDownView;
import com.cgfay.camera.widget.RecordProgressView;
import com.cgfay.cameralibrary.R;
import com.cgfay.camera.camera.CameraParam;
import com.cgfay.camera.model.GalleryType;
import com.cgfay.camera.widget.CameraMeasureFrameLayout;
import com.cgfay.camera.widget.RecordSpeedLevelBar;
import com.cgfay.media.CainCommandEditor;
import com.cgfay.media.recorder.SpeedMode;
import com.cgfay.picker.MediaPicker;
import com.cgfay.picker.loader.AlbumDataLoader;
import com.cgfay.picker.model.AlbumData;
import com.cgfay.uitls.bean.MusicData;
import com.cgfay.uitls.dialog.DialogBuilder;
import com.cgfay.uitls.fragment.MusicPickerFragment;
import com.cgfay.uitls.fragment.PermissionErrorDialogFragment;
import com.cgfay.uitls.utils.BrightnessUtils;
import com.cgfay.uitls.utils.PermissionUtils;
import com.cgfay.uitls.utils.Variables;
import com.cgfay.uitls.widget.RoundOutlineProvider;
import com.cgfay.widget.CameraTabView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * ??????????????????
 */
public class CameraPreviewFragment extends Fragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "CameraPreviewFragment";
    private static final boolean VERBOSE = true;

    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";
    private static final String FRAGMENT_DIALOG = "dialog";

    private static final int ALBUM_LOADER_ID = 1;

    // Preview parameters
    private CameraParam mCameraParam;

    // Fragment?????????
    private View mContentView;
    // ????????????
    private CameraMeasureFrameLayout mPreviewLayout;
    private CainTextureView mCameraTextureView;
    // fps??????
    private TextView mFpsView;

    // ???????????????
    private RecordProgressView mProgressView;
    // ???????????????
    private RecordCountDownView mCountDownView;

    // ??????topbar
    private CameraPreviewTopbar mPreviewTopbar;

    // ???????????????
    private RecordSpeedLevelBar mSpeedBar;
    private boolean mSpeedBarShowing;
    // ????????????
    private LinearLayout mBtnStickers;
    // ????????????
    private RecordButton mBtnRecord;

    private View mLayoutMedia;
    // ???????????????
    private ImageView mBtnMedia;

    // ????????????
    private LinearLayout mLayoutDelete;
    // ??????????????????
    private Button mBtnDelete;
    // ??????????????????
    private Button mBtnNext;
    // ???????????????
    private CameraTabView mCameraTabView;
    private View mTabIndicator;

    private boolean mFragmentAnimating;
    private FrameLayout mFragmentContainer;
    // ??????????????????
    private PreviewResourceFragment mResourcesFragment;
    // ????????????
    private PreviewEffectFragment mEffectFragment;
    // ??????????????????
    private PreviewSettingFragment mSettingFragment;

    private final Handler mMainHandler;
    private Activity mActivity;

    private CameraPreviewPresenter mPreviewPresenter;

    // ????????????????????????
    private LoaderManager mLocalImageLoader;

    // ???????????????
    private Dialog mDialog;
    String name,song_id;

    public CameraPreviewFragment(String name,String song_id) {
        mCameraParam = CameraParam.getInstance();
        mMainHandler = new Handler(Looper.getMainLooper());
        mPreviewPresenter = new CameraPreviewPresenter(this);
        this.song_id = song_id;
        this.name = name;
        System.out.println(""+name);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            mActivity = getActivity();
        }
        mPreviewPresenter.onAttach(mActivity);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreviewPresenter.onCreate();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_camera_preview, container, false);
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isCameraEnable()) {
            initView(mContentView);
        } else {
            PermissionUtils.requestCameraPermission(this);
        }

        if (PermissionUtils.permissionChecking(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            mLocalImageLoader = LoaderManager.getInstance(this);
            mLocalImageLoader.initLoader(ALBUM_LOADER_ID, null, this);
        }
    }

    /**
     * ???????????????
     * @param view
     */
    private void initView(View view) {
        initPreviewSurface();
        initPreviewTopbar();
        initBottomLayout(view);
        initCameraTabView();


        if(!name.equals("name"))
        {
            mPreviewTopbar.setSelectedMusic(name);
            PreparedAudio(name);

        }

    }

    public  void PreparedAudio(String name){

        File file=new File(Variables.app_folder+ Variables.SelectedAudio_AAC);
        if(file.exists()) {
            audio = new MediaPlayer();
            try {
                audio.setDataSource(Variables.app_folder + Variables.SelectedAudio_AAC);
                audio.prepare();
                mPreviewPresenter.audio= audio;
            } catch (IOException e) {
                e.printStackTrace();
            }

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(getActivity(), Uri.fromFile(file));
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            if(file_duration<Variables.max_recording_duration){
                Variables.recording_duration=file_duration;

                int seconds = (int)((file_duration / 1000) % 60);

                mPreviewPresenter.setMusicPath(String.valueOf(file));
                mPreviewTopbar.setSelectedMusic(name);
                mPreviewPresenter.setRecordSeconds((int) seconds);


            }

        }

    }


    private void initPreviewSurface() {
        mFpsView = mContentView.findViewById(R.id.tv_fps);
        mPreviewLayout = mContentView.findViewById(R.id.layout_camera_preview);
        mCameraTextureView = new CainTextureView(mActivity);
        mCameraTextureView.addOnTouchScroller(mTouchScroller);
        mCameraTextureView.addMultiClickListener(mMultiClickListener);
        mCameraTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        mPreviewLayout.addView(mCameraTextureView);

        // ??????????????????
        if (Build.VERSION.SDK_INT >= 21) {
            mCameraTextureView.setOutlineProvider(new RoundOutlineProvider(getResources().getDimension(R.dimen.dp7)));
            mCameraTextureView.setClipToOutline(true);
        }
        mPreviewLayout.setOnMeasureListener(new PreviewMeasureListener(mPreviewLayout));
        mProgressView = mContentView.findViewById(R.id.record_progress);
        mCountDownView = mContentView.findViewById(R.id.count_down_view);
    }

    /**
         *Initialize the top topbar
     */
    private void initPreviewTopbar() {
        mPreviewTopbar = mContentView.findViewById(R.id.camera_preview_topbar);
        mPreviewTopbar.addOnCameraCloseListener(this::closeCamera)
                .addOnCameraSwitchListener(this::switchCamera)
                .addOnShowPanelListener(type -> {
                    switch (type) {
                        case CameraPreviewTopbar.PanelMusic: {
                            openMusicPicker();
                            break;
                        }

                        case CameraPreviewTopbar.PanelSpeedBar: {
                            setShowingSpeedBar(mSpeedBar.getVisibility() != View.VISIBLE);
                            break;
                        }

                        case CameraPreviewTopbar.PanelFilter: {
                            showEffectFragment();
                            break;
                        }

                        case CameraPreviewTopbar.PanelSetting: {
                            showSettingFragment();
                            break;
                        }
                    }
                });
    }

    /**
     * ?????????????????????
     * @param view
     */
    private void initBottomLayout(@NonNull View view) {
        mFragmentContainer = view.findViewById(R.id.fragment_bottom_container);
        mSpeedBar = view.findViewById(R.id.record_speed_bar);
        mSpeedBar.setOnSpeedChangedListener((speed) -> {
            mPreviewPresenter.setSpeedMode(SpeedMode.valueOf(speed.getSpeed()));
        });

        mBtnStickers = view.findViewById(R.id.btn_stickers);
        mBtnStickers.setOnClickListener(this);
        mLayoutMedia = view.findViewById(R.id.layout_media);
        mBtnMedia = view.findViewById(R.id.btn_media);
        mBtnMedia.setOnClickListener(this);

        mBtnRecord = view.findViewById(R.id.btn_record);
        mBtnRecord.setOnClickListener(this);
        mBtnRecord.addRecordStateListener(mRecordStateListener);

        mLayoutDelete = view.findViewById(R.id.layout_delete);
        mBtnDelete = view.findViewById(R.id.btn_record_delete);
        mBtnDelete.setOnClickListener(this);
        mBtnNext = view.findViewById(R.id.btn_goto_edit);
        mBtnNext.setOnClickListener(this);

        setShowingSpeedBar(mSpeedBarShowing);
    }



    /**
     * ?????????????????????tab view
     */
    private void initCameraTabView() {
        mTabIndicator = mContentView.findViewById(R.id.iv_tab_indicator);
        mCameraTabView = mContentView.findViewById(R.id.tl_camera_tab);

//        mCameraTabView.addTab(mCameraTabView.newTab().setText(R.string.tab_picture));
//        mCameraTabView.addTab(mCameraTabView.newTab().setText(R.string.tab_video_60s));
        mCameraTabView.addTab(mCameraTabView.newTab().setText(R.string.tab_video_15s), true);
//        mCameraTabView.addTab(mCameraTabView.newTab().setText(R.string.tab_video_picture));

        mCameraTabView.setIndicateCenter(true);
        mCameraTabView.setScrollAutoSelected(true);
        mCameraTabView.addOnTabSelectedListener(new CameraTabView.OnTabSelectedListener() {
            @Override
            public void onTabSelected(CameraTabView.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    mCameraParam.mGalleryType = GalleryType.PICTURE;
                    if (!isStorageEnable()) {
                        PermissionUtils.requestRecordSoundPermission(CameraPreviewFragment.this);
                    }
                    if (mBtnRecord != null) {
                        mBtnRecord.setRecordEnable(false);
                    }
                } else if (position == 1) {
                    mCameraParam.mGalleryType = GalleryType.VIDEO_60S;
                    // Request recording permission
                    if (!isRecordAudioEnable()) {
                        PermissionUtils.requestRecordSoundPermission(CameraPreviewFragment.this);
                    }

                    if (mBtnRecord != null) {
                        mBtnRecord.setRecordEnable(true);
                    }
                    mPreviewPresenter.setRecordSeconds(60);
                } else if (position == 2) {
                    mCameraParam.mGalleryType = GalleryType.VIDEO_15S;
                    // ??????????????????
                    if (!isRecordAudioEnable()) {
                        PermissionUtils.requestRecordSoundPermission(CameraPreviewFragment.this);
                    }
                    if (mBtnRecord != null) {
                        mBtnRecord.setRecordEnable(true);
                    }
                    mPreviewPresenter.setRecordSeconds(18);
                } else if (position == 3) {
                    showVideoPicture();
                }
            }

            @Override
            public void onTabUnselected(CameraTabView.Tab tab) {

            }

            @Override
            public void onTabReselected(CameraTabView.Tab tab) {

            }
        });
        mPreviewPresenter.setRecordSeconds(15);
    }

    /**
     * ??????????????????
     */
    private void showVideoPicture() {
        // TODO ?????????????????????
    }

    @Override
    public void onStart() {
        super.onStart();
        mPreviewPresenter.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        enhancementBrightness();
        mPreviewPresenter.onResume();
        Log.d(TAG, "onResume: ");
    }

    /**
     * ????????????
     */
    private void enhancementBrightness() {
        BrightnessUtils.setWindowBrightness(mActivity, mCameraParam.luminousEnhancement
                ? BrightnessUtils.MAX_BRIGHTNESS : mCameraParam.brightness);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPreviewPresenter.onPause();
        if (audio != null) {
            audio.stop();
        }
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        mPreviewPresenter.onStop();
        if (audio != null) {
            audio.stop();
        }
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        if (mCountDownView != null) {
            mCountDownView.cancel();
            mCountDownView = null;
        }
        mContentView = null;
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        destroyImageLoader();
        mPreviewPresenter.onDestroy();
        dismissDialog();
        mMainHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        mPreviewPresenter.onDetach();
        mPreviewPresenter = null;
        mActivity = null;
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    /**
     * ????????????????????????
     * @return ??????????????????????????????
     */
    public boolean onBackPressed() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            hideFragmentAnimating();
            return true;
        }

        // ?????????
        if (mCountDownView != null && mCountDownView.isCountDowning()) {
            mCountDownView.cancel();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_stickers) {
            showStickers();
        } else if (i == R.id.btn_media) {
            openMediaPicker();
        } else if (i == R.id.btn_record) {
            takePicture();
        } else if (i == R.id.btn_record_delete) {
            deleteRecordedVideo();
        } else if (i == R.id.btn_goto_edit) {
            stopRecordOrPreviewVideo();
        }
    }

    /**
     * ????????????????????????
     */
    private void dismissDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * ????????????
     */
    private void closeCamera() {
        if (mActivity != null) {
            mActivity.finish();
            mActivity.overridePendingTransition(0, R.anim.anim_slide_down);
        }
    }

    /**
     * ????????????
     */
    private void switchCamera() {
        if (!isCameraEnable()) {
            PermissionUtils.requestCameraPermission(this);
            return;
        }
        mPreviewPresenter.switchCamera();
    }

    /**
     * ?????????????????????
     * @param show
     */
    private void setShowingSpeedBar(boolean show) {
        mSpeedBarShowing = show;
        mSpeedBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mPreviewTopbar.setSpeedBarOpen(show);
    }

    /**
     * ??????????????????
     */
    private void showSettingFragment() {
        if (mFragmentAnimating) {
            return;
        }
        if (mSettingFragment == null) {
            mSettingFragment = new PreviewSettingFragment();
        }
        mSettingFragment.addStateChangedListener(mStateChangedListener);
        mSettingFragment.setEnableChangeFlash(mCameraParam.supportFlash);
        if (!mSettingFragment.isAdded()) {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_bottom_container, mSettingFragment, FRAGMENT_TAG)
                    .addToBackStack(FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        } else {
            getChildFragmentManager()
                    .beginTransaction()
                    .show(mSettingFragment)
                    .commitAllowingStateLoss();
        }
        showFragmentAnimating();
    }

    /**
     * ????????????????????????
     */
    private void showStickers() {
        if (mFragmentAnimating) {
            return;
        }
        if (mResourcesFragment == null) {
            mResourcesFragment = new PreviewResourceFragment();
        }
        mResourcesFragment.addOnChangeResourceListener((data) -> {
            mPreviewPresenter.changeResource(data);
        });
        if (!mResourcesFragment.isAdded()) {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_bottom_container, mResourcesFragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        } else {
            getChildFragmentManager()
                    .beginTransaction()
                    .show(mResourcesFragment)
                    .commitAllowingStateLoss();
        }
        showFragmentAnimating(false);
    }

    /**
     * ??????????????????
     */
    private void showEffectFragment() {
        if (mFragmentAnimating) {
            return;
        }
        if (mEffectFragment == null) {
            mEffectFragment = new PreviewEffectFragment();
        }
        mEffectFragment.addOnCompareEffectListener(compare -> {
            mPreviewPresenter.showCompare(compare);
        });
        mEffectFragment.addOnFilterChangeListener(color -> {
            mPreviewPresenter.changeDynamicFilter(color);
        });
        mEffectFragment.addOnMakeupChangeListener(makeup -> {
            mPreviewPresenter.changeDynamicMakeup(makeup);
        });
        mEffectFragment.scrollToCurrentFilter(mPreviewPresenter.getFilterIndex());
        if (!mEffectFragment.isAdded()) {
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_bottom_container, mEffectFragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
        } else {
            getChildFragmentManager()
                    .beginTransaction()
                    .show(mEffectFragment)
                    .commitAllowingStateLoss();
        }
        showFragmentAnimating();
    }

    /**
     * ??????Fragment??????
     */
    private void showFragmentAnimating() {
        showFragmentAnimating(true);
    }

    /**
     * ??????Fragment??????
     */
    private void showFragmentAnimating(boolean hideAllLayout) {
        if (mFragmentAnimating) {
            return;
        }
        mFragmentAnimating = true;
        mFragmentContainer.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.preview_slide_up);
        mFragmentContainer.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFragmentAnimating = false;
                if (hideAllLayout) {
                    hideAllLayout();
                } else {
                    hideWithoutSwitch();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * ??????Fragment??????
     */
    private void hideFragmentAnimating() {
        if (mFragmentAnimating) {
            return;
        }
        mFragmentAnimating = true;
        Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.preivew_slide_down);
        mFragmentContainer.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                resetAllLayout();
                removeFragment();
                mFragmentAnimating = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * ??????Fragment
     */
    private void removeFragment() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commitAllowingStateLoss();
        }
    }

    /**
     * ??????????????????
     */
    private void hideAllLayout() {
        mMainHandler.post(()-> {
            if (mPreviewTopbar != null) {
                mPreviewTopbar.hideAllView();
            }
            if (mSpeedBar != null) {
                mSpeedBar.setVisibility(View.GONE);
            }
            if (mBtnStickers != null) {
                mBtnStickers.setVisibility(View.GONE);
            }
            if (mBtnRecord != null) {
                mBtnRecord.setVisibility(View.GONE);
            }
            if (mLayoutMedia != null) {
                mLayoutMedia.setVisibility(View.GONE);
            }
            if (mLayoutDelete != null) {
                mLayoutDelete.setVisibility(View.GONE);
            }
            if (mCameraTabView != null) {
                mCameraTabView.setVisibility(View.GONE);
            }
            if (mTabIndicator != null) {
                mTabIndicator.setVisibility(View.GONE);
            }
        });
    }

    /**
     * ?????????????????????????????????????????????
     */
    private void hideWithoutSwitch() {
        mMainHandler.post(() -> {
            if (mPreviewTopbar != null) {
                mPreviewTopbar.hideWithoutSwitch();
            }
            if (mSpeedBar != null) {
                mSpeedBar.setVisibility(View.GONE);
            }
            if (mBtnStickers != null) {
                mBtnStickers.setVisibility(View.GONE);
            }
            if (mBtnRecord != null) {
                mBtnRecord.setVisibility(View.GONE);
            }
            if (mLayoutMedia != null) {
                mLayoutMedia.setVisibility(View.GONE);
            }
            if (mLayoutDelete != null) {
                mLayoutDelete.setVisibility(View.GONE);
            }
            if (mCameraTabView != null) {
                mCameraTabView.setVisibility(View.GONE);
            }
            if (mTabIndicator != null) {
                mTabIndicator.setVisibility(View.GONE);
            }
        });
    }

    /**
     * ??????????????????
     */
    public void hideOnRecording() {
        mMainHandler.post(()-> {
            if (mPreviewTopbar != null) {
                mPreviewTopbar.hideAllView();
            }
            if (mSpeedBar != null) {
                mSpeedBar.setVisibility(View.GONE);
            }
            if (mBtnStickers != null) {
                mBtnStickers.setVisibility(View.GONE);
            }
            if (mLayoutMedia != null) {
                mLayoutMedia.setVisibility(View.GONE);
            }
            if (mCameraTabView != null) {
                mCameraTabView.setVisibility(View.GONE);
            }
            if (mTabIndicator != null) {
                mTabIndicator.setVisibility(View.GONE);
            }
            if (mBtnDelete != null) {
                mBtnDelete.setVisibility(View.GONE);
            }
        });
    }

    /**
     * ??????????????????
     */
    public void resetAllLayout() {
        mMainHandler.post(()-> {
            if (mPreviewTopbar != null) {
                mPreviewTopbar.resetAllView();
            }
            setShowingSpeedBar(mSpeedBarShowing);
            if (mBtnStickers != null) {
                mBtnStickers.setVisibility(View.VISIBLE);
            }
            if (mBtnRecord != null) {
                mBtnRecord.setVisibility(View.VISIBLE);
            }
            if (mLayoutDelete != null) {
                mLayoutDelete.setVisibility(View.VISIBLE);
            }
            if (mCameraTabView != null) {
                mCameraTabView.setVisibility(View.VISIBLE);
            }
            if (mTabIndicator != null) {
                mTabIndicator.setVisibility(View.VISIBLE);
            }
            resetDeleteButton();
            if (mBtnRecord != null) {
                mBtnRecord.reset();
            }
        });
    }

    /**
     * ??????????????????
     */
    private void resetDeleteButton() {
        boolean hasRecordVideo = (mPreviewPresenter.getRecordedVideoSize() > 0);
        if (mBtnNext != null) {
            mBtnNext.setVisibility(hasRecordVideo ? View.VISIBLE : View.GONE);
        }
        if (mBtnDelete != null) {
            mBtnDelete.setVisibility(hasRecordVideo ? View.VISIBLE : View.GONE);
        }
        if (mLayoutMedia != null) {
            mLayoutMedia.setVisibility(hasRecordVideo ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * ??????
     */
    private void takePicture() {
        if (isStorageEnable()) {
            if (mCameraParam.mGalleryType == GalleryType.PICTURE) {
                if (mCameraParam.takeDelay) {
                    mCountDownView.addOnCountDownListener(new RecordCountDownView.OnCountDownListener() {
                        @Override
                        public void onCountDownEnd() {
                            mPreviewPresenter.takePicture();
                            resetAllLayout();
                        }

                        @Override
                        public void onCountDownCancel() {
                            resetAllLayout();
                        }
                    });
                    mCountDownView.start();
                    hideAllLayout();
                } else {
                    mPreviewPresenter.takePicture();
                }
            }
        } else {
            PermissionUtils.requestStoragePermission(this);
        }
    }

    /**
     * ????????????
     */
    public void cancelRecordIfNeeded() {
        // ????????????
        if (mPreviewPresenter.isRecording()) {
            // ????????????
            mPreviewPresenter.cancelRecord();
        }
    }

    /**
     * ????????????????????????
     */



    public void openMusicPicker() {
        MusicPickerFragment fragment = new MusicPickerFragment();
        fragment.addOnMusicSelectedListener(
                new MusicPickerFragment.OnMusicSelectedListener() {
            @Override
            public void onMusicSelectClose() {
                Fragment currentFragment = getChildFragmentManager().findFragmentByTag(MusicPickerFragment.TAG);
                if (currentFragment != null) {
                    getChildFragmentManager()
                            .beginTransaction()
                            .remove(currentFragment)
                            .commitNowAllowingStateLoss();
                }
            }

            @Override
            public void onMusicSelected(MusicData musicData) {
                if(musicData.getId() == -1)
                {
                    ProgressDialog progressDialog = new ProgressDialog(mActivity);
                    progressDialog.setMessage("Loading.....");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    String destinationPath = Variables.app_folder+ Variables.SelectedAudio_AAC;
                    mPreviewPresenter.mCommandEditor.execCommand(CainCommandEditor.mp3ToConvertAAC(musicData.getPath(),destinationPath),result -> {

                        if(result == 0)
                        {
                            resetAllLayout();
                            mPreviewPresenter.setMusicPath(destinationPath);
                            mPreviewTopbar.setSelectedMusic(musicData.getName());
                            mPreviewPresenter.setRecordSeconds((int) musicData.getDuration());

//                mPreviewPresenter.setRecordAudioEnable(true);
                            audio = new MediaPlayer();
                            try {
                                audio.setDataSource(destinationPath);
                                audio.prepare();
                                mPreviewPresenter.audio= audio;
                            } catch (IOException e) {
                                e.printStackTrace();

                            }
                        }

                        progressDialog.dismiss();


                    });
                }
                else {
                    resetAllLayout();
                    mPreviewPresenter.setMusicPath(musicData.getPath());
                    mPreviewTopbar.setSelectedMusic(musicData.getName());
                    mPreviewPresenter.setRecordSeconds((int) musicData.getDuration());

//                mPreviewPresenter.setRecordAudioEnable(true);
                    audio = new MediaPlayer();
                    try {
                        audio.setDataSource(musicData.getPath());
                        audio.prepare();
                        mPreviewPresenter.audio= audio;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
                });
        getChildFragmentManager()
                .beginTransaction()
                .add(fragment, MusicPickerFragment.TAG)
                .commitAllowingStateLoss();
    }

    /**
     * ???????????????????????????
     */
    private void openMediaPicker() {
        mMainHandler.post(()-> {
            MediaPicker.from(this)
                    .showImage(true)
                    .showVideo(true)
                    .setMediaSelector(new NormalMediaSelector())
                    .show();
        });
    }

    // ------------------------------- TextureView ????????????????????? ----------------------------------
    private CainTextureView.OnTouchScroller mTouchScroller = new CainTextureView.OnTouchScroller() {

        @Override
        public void swipeBack() {
            //mPreviewPresenter.nextFilter();
        }

        @Override
        public void swipeFrontal() {
          //  mPreviewPresenter.previewFilter();
        }

        @Override
        public void swipeUpper(boolean startInLeft, float distance) {
            if (VERBOSE) {
                Log.d(TAG, "swipeUpper, startInLeft ? " + startInLeft + ", distance = " + distance);
            }
        }

        @Override
        public void swipeDown(boolean startInLeft, float distance) {
            if (VERBOSE) {
                Log.d(TAG, "swipeDown, startInLeft ? " + startInLeft + ", distance = " + distance);
            }
        }

    };

    /**
     * ?????????????????????
     */
    private CainTextureView.OnMultiClickListener mMultiClickListener = new CainTextureView.OnMultiClickListener() {

        @Override
        public void onSurfaceSingleClick(final float x, final float y) {
            // Deal with floating window Fragment
            if (onBackPressed()) {
                return;
            }

            // If you are in the state of taking photos on the touch screen, take pictures directly without focusing
            if (mCameraParam.touchTake) {
                takePicture();
                return;
            }

            // todo ??????????????????????????????

        }

        @Override
        public void onSurfaceDoubleClick(float x, float y) {
            switchCamera();
        }

    };

    // ---------------------------- TextureView SurfaceTexture?????? ---------------------------------
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            mPreviewPresenter.onSurfaceCreated(surface);
            mPreviewPresenter.onSurfaceChanged(width, height);
            Log.d(TAG, "onSurfaceTextureAvailable: ");
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            mPreviewPresenter.onSurfaceChanged(width, height);
            Log.d(TAG, "onSurfaceTextureSizeChanged: ");
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mPreviewPresenter.onSurfaceDestroyed();
            Log.d(TAG, "onSurfaceTextureDestroyed: ");
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };



    // ----------------------------------- Click callback on the top status bar ------------------------------------
    private PreviewSettingFragment.StateChangedListener mStateChangedListener = new PreviewSettingFragment.StateChangedListener() {

        @Override
        public void flashStateChanged(boolean flashOn) {
            // todo ???????????????

        }

        @Override
        public void onOpenCameraSetting() {
            mPreviewPresenter.onOpenCameraSettingPage();
        }

        @Override
        public void delayTakenChanged(boolean enable) {
            mCameraParam.takeDelay = enable;
        }

        @Override
        public void luminousCompensationChanged(boolean enable) {
            mCameraParam.luminousEnhancement = enable;
            enhancementBrightness();
        }

        @Override
        public void touchTakenChanged(boolean touchTake) {
            mCameraParam.touchTake = touchTake;
        }

        @Override
        public void changeEdgeBlur(boolean enable) {
            mPreviewPresenter.enableEdgeBlurFilter(enable);
        }
    };

    /**
     * ??????fps
     * @param fps
     */
    public void showFps(final float fps) {
        mMainHandler.post(() -> {
            if (mCameraParam.showFps) {
                mFpsView.setText("fps = " + fps);
                mFpsView.setVisibility(View.GONE);
            } else {
                mFpsView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * ??????????????????
     * @param duration
     */
    public void updateRecordProgress(final float duration) {
        mMainHandler.post(() -> {
            mProgressView.setProgress(duration);
        });
    }

    /**
     * ??????????????????
     * @param progress
     */
    public void addProgressSegment(float progress) {
        mMainHandler.post(() -> {
            mProgressView.addProgressSegment(progress);
        });
    }

    /**
     * ??????????????????
     */
    public void deleteProgressSegment() {
        mMainHandler.post(() -> {
            mProgressView.deleteProgressSegment();
            resetDeleteButton();
        });
    }

    /**
     * ????????????????????????
     */
    private void deleteRecordedVideo() {
        dismissDialog();
        mDialog = DialogBuilder.from(mActivity, R.layout.dialog_two_button)
                .setText(R.id.tv_dialog_title, R.string.delete_last_video_tips)
                .setText(R.id.btn_dialog_cancel, R.string.btn_dialog_cancel)
                .setDismissOnClick(R.id.btn_dialog_cancel, true)
                .setText(R.id.btn_dialog_ok, R.string.btn_delete)
                .setDismissOnClick(R.id.btn_dialog_ok, true)
                .setOnClickListener(R.id.btn_dialog_ok, v -> {
                    mPreviewPresenter.deleteLastVideo();
                })
                .show();
    }

    /**
     * ??????????????????????????????
     */
    private void stopRecordOrPreviewVideo() {
        mPreviewPresenter.mergeAndEdit();
    }

    /**
     * ?????????????????????
     */

    MediaPlayer audio;

    private RecordButton.RecordStateListener mRecordStateListener = new RecordButton.RecordStateListener() {
        @Override
        public void onRecordStart() {
            mPreviewPresenter.startRecord();
            if(audio!=null)
            {
//                mPreviewPresenter.setRecordAudioEnable(true);
                audio.start();
            }
            else {
//                mPreviewPresenter.setRecordAudioEnable(false);
            }
        }

        @Override
        public void onRecordStop() {
            mPreviewPresenter.stopRecord();
            if(audio!=null)
            {
//                mPreviewPresenter.setRecordAudioEnable(true);
                audio.pause();
            }
            else {
//                mPreviewPresenter.setRecordAudioEnable(false);
            }
        }

        @Override
        public void onZoom(float percent) {

        }
    };


    // -------------------------------------- ???????????? --------------------------------------
    private Dialog mProgressDialog;
    /**
     * ??????????????????
     */
    public void showConcatProgressDialog() {
        mMainHandler.post(() -> {
            mProgressDialog = ProgressDialog.show(mActivity, "Composing", "Composing");
        });
    }

    /**
     * ??????????????????
     */
    public void hideConcatProgressDialog() {
        mMainHandler.post(() -> {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        });
    }

    private Toast mToast;
    /**
     * ??????Toast??????
     * @param msg
     */
    public void showToast(String msg) {
        mMainHandler.post(() -> {
            if (mToast != null) {
                mToast.cancel();
            }
            mToast = Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT);
            mToast.show();
        });
    }

    // -------------------------------------- ?????????????????? ---------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                PermissionErrorDialogFragment.newInstance(getString(R.string.request_camera_permission), PermissionUtils.REQUEST_CAMERA_PERMISSION, true)
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            } else {
                initView(mContentView);
            }
        } else if (requestCode == PermissionUtils.REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                PermissionErrorDialogFragment.newInstance(getString(R.string.request_storage_permission), PermissionUtils.REQUEST_STORAGE_PERMISSION)
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else if (requestCode == PermissionUtils.REQUEST_SOUND_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                PermissionErrorDialogFragment.newInstance(getString(R.string.request_sound_permission), PermissionUtils.REQUEST_SOUND_PERMISSION)
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * ??????????????????
     * @return
     */
    private boolean isCameraEnable() {
        return PermissionUtils.permissionChecking(mActivity, Manifest.permission.CAMERA);
    }

    /**
     * ????????????????????????
     * @return
     */
    private boolean isRecordAudioEnable() {
        return PermissionUtils.permissionChecking(mActivity, Manifest.permission.RECORD_AUDIO);
    }

    /**
     * ????????????????????????????????????
     * @return
     */
    private boolean isStorageEnable() {
        return PermissionUtils.permissionChecking(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    // -------------------------------------- Thumbnail loading logic start ---------------------------------
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        return AlbumDataLoader.getImageLoaderWithoutBucketSort(mActivity);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.moveToFirst();
            AlbumData albumData = AlbumData.valueOf(cursor);
            if (mBtnMedia != null) {
                new CameraMediaLoader().loadThumbnail(mActivity, mBtnMedia, albumData.getCoverUri(),
                        R.drawable.ic_camera_thumbnail_placeholder,
                        (int)getResources().getDimension(R.dimen.dp4));
            }
            cursor.close();
            destroyImageLoader();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    /**
     * ???????????????
     */
    private void destroyImageLoader() {
        if (mLocalImageLoader != null) {
            mLocalImageLoader.destroyLoader(ALBUM_LOADER_ID);
            mLocalImageLoader = null;
        }
    }
    // -------------------------------------- ????????????????????? end -----------------------------------
}
