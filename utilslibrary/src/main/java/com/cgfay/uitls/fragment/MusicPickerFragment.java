package com.cgfay.uitls.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cgfay.uitls.adapter.Custom_ViewPager_utils;
import com.cgfay.uitls.adapter.FavouriteSounds.Favourite_Sound_F;
import com.cgfay.uitls.adapter.LocalMusicAdapter;
import com.cgfay.uitls.bean.MusicData;
import com.cgfay.uitls.scanner.LocalMusicScanner;
import com.cgfay.uitls.utils.FileUtils;
import com.cgfay.uitls.utils.Variables;
import com.cgfay.utilslibrary.R;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.IOException;

/**
 * 音乐选择页面
 */
public class MusicPickerFragment extends AppCompatDialogFragment  {

    public static final String TAG = "MusicPickerFragment";

    private FragmentActivity mActivity;

    private LocalMusicScanner mMusicScanner;
    private RecyclerView mRecyclerView;
    TextView txtlocal,txtserver;
   // private LocalMusicAdapter mAdapter;
    private OnMusicSelectedListener mMusicSelectedListener;
    RelativeLayout rlt_pager;
    EditText search_edit;
    LocalMusicAdapter.OnMusicItemSelectedListener onMusicItemSelectedListener;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity) {
            mActivity = (FragmentActivity)context;
        } else if (getActivity() != null) {
            mActivity = getActivity();
        }
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.MusicDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_music_select, container, false);
        initView(view);


        return view;
    }

    private void initView(@NonNull View view) {
        rlt_pager = view.findViewById(R.id.rlt_pager);
        txtlocal = view.findViewById(R.id.txtlocal);
        txtserver = view.findViewById(R.id.txtserver);
        mRecyclerView = view.findViewById(R.id.music_list);
        search_edit=view.findViewById(R.id.search_edit);
        search_edit.setOnClickListener(v -> {
            Open_search();
        });


        // Note that we are passing childFragmentManager, not FragmentManager


        view.findViewById(R.id.iv_close).setOnClickListener(v -> {
            if (mMusicSelectedListener != null) {
                mMusicSelectedListener.onMusicSelectClose();
            }
        });

        txtlocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                txtlocal.setTextColor(getResources().getColor(R.color.blue));
//                txtserver.setTextColor(getResources().getColor(R.color.black));
//
//                mRecyclerView.setVisibility(View.VISIBLE);
//                rlt_pager.setVisibility(View.GONE);

            }
        });

     onMusicItemSelectedListener = new LocalMusicAdapter.OnMusicItemSelectedListener() {
            @Override
            public void onMusicItemSelected(MusicData musicData) {
                if (mMusicSelectedListener != null) {
                    mMusicSelectedListener.onMusicSelected(musicData);
                    mMusicSelectedListener.onMusicSelectClose();
                }
            }
        };
        mRecyclerView.setVisibility(View.GONE);
        rlt_pager.setVisibility(View.VISIBLE);
        DiscoverTestFragment discoverTestFragment = new DiscoverTestFragment(onMusicItemSelectedListener);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_contener,discoverTestFragment)
                .commit();
        txtserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtlocal.setTextColor(getResources().getColor(R.color.black));
                txtserver.setTextColor(getResources().getColor(R.color.blue));

                mRecyclerView.setVisibility(View.GONE);
                rlt_pager.setVisibility(View.VISIBLE);
                DiscoverTestFragment discoverTestFragment = new DiscoverTestFragment(onMusicItemSelectedListener);
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_contener,discoverTestFragment)
                        .commit();

            }
        });
    }


    public void Open_search(){
//        SoundList_video search_main_f = new SoundList_video();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.addToBackStack(null);
//        transaction.replace(R.id.soundfragment, search_main_f).commit();

        SoundList_video discoverTestFragment = new SoundList_video(onMusicItemSelectedListener);
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.frglutsoundsearch,discoverTestFragment)
                .commit();


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        mAdapter = new LocalMusicAdapter(null,getActivity());
//        mAdapter.setOnMusicSelectedListener(this);
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
//                LinearLayoutManager.VERTICAL, false));
//        mRecyclerView.setAdapter(mAdapter);
//        mMusicScanner = new LocalMusicScanner(getActivity(), this);
//        mMusicScanner.scanLocalMusic();
    }

    @Override
    public void onResume() {
        super.onResume();
        initDismissListener();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // mMusicScanner.destroy();
    }

    private void initDismissListener() {
        if (getDialog() != null) {
            getDialog().setOnDismissListener(dialog -> {
                closeFragment();
            });
        }
    }

    /**
     * 关闭当前页面
     */
    private void closeFragment() {
        if (getParentFragment() != null) {
            getParentFragment()
                    .getChildFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commitAllowingStateLoss();
        } else if (mActivity != null) {
            mActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commitAllowingStateLoss();
        }
    }

//    @Override
//    public void onMusicScanFinish(Cursor cursor) {
//        mAdapter.setCursor(cursor);
//    }
//
//    @Override
//    public void onMusicScanReset() {
//        mAdapter.setCursor(null);
//    }
//
//    @Override
//    public void onMusicItemSelected(MusicData musicData) {
//        if (mMusicSelectedListener != null) {
//
//            mMusicSelectedListener.onMusicSelected(musicData);
//            mMusicSelectedListener.onMusicSelectClose();
//        }
//    }

    /**
     * 音乐选中监听器
     */
    public interface OnMusicSelectedListener {

        void onMusicSelectClose();

        void onMusicSelected(MusicData musicData);
    }

    /**
     * 添加音乐选中监听器
     * @param listener
     */
    public void addOnMusicSelectedListener(OnMusicSelectedListener listener) {
        mMusicSelectedListener = listener;
    }


}
