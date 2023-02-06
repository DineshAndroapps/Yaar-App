package com.yaar.shortvideoapp.SoundLists;

import android.content.res.Resources;
import android.os.Bundle;

import com.yaar.shortvideoapp.BaseActivity;
import com.yaar.shortvideoapp.Search.SoundList_video;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.yaar.shortvideoapp.Main_Menu.Custom_ViewPager;
import com.yaar.shortvideoapp.R;
import com.yaar.shortvideoapp.SoundLists.FavouriteSounds.Favourite_Sound_F;

public class SoundList_Main_A extends BaseActivity implements View.OnClickListener{

    protected TabLayout tablayout;

    protected Custom_ViewPager pager;

    private ViewPagerAdapter adapter;
    EditText search_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_list_main);

        tablayout = (TabLayout) findViewById(R.id.groups_tab);
        search_edit=findViewById(R.id.search_edit);
        search_edit.setOnClickListener(this);
        findViewById(R.id.search_layout).setOnClickListener(this);

        pager = findViewById(R.id.viewpager);
        pager.setOffscreenPageLimit(2);
        pager.setPagingEnabled(false);

        // Note that we are passing childFragmentManager, not FragmentManager
        adapter = new ViewPagerAdapter(getResources(), getSupportFragmentManager());
        pager.setAdapter(adapter);
        tablayout.setupWithViewPager(pager);
        findViewById(R.id.Goback).setOnClickListener(this);


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Goback:
                onBackPressed();
                break;
            case R.id.search_layout:
                Open_search();
                break;
            case R.id.search_edit:
                Open_search();
                break;
        }
    }

    public void Open_search(){
        SoundList_video search_main_f = new SoundList_video();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.soundfragment, search_main_f).commit();

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {


        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


        public ViewPagerAdapter(final Resources resources, FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            final Fragment result;
            switch (position) {
                case 0:
                    result = new Discover_SoundList_F();
                    break;
                case 1:
                    result = new Favourite_Sound_F();
                    break;
                default:
                    result = null;
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            switch (position) {
                case 0:
                    return "Discover";
                case 1:
                    return "My Favorites";

                default:
                    return null;

            }


        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }


        /**
         * Get the Fragment by position
         *
         * @param position tab position of the fragment
         * @return
         */


        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


    }

}
