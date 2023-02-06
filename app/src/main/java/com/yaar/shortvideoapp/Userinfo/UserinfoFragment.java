package com.yaar.shortvideoapp.Userinfo;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yaar.shortvideoapp.Inbox.Inbox_F;
import com.yaar.shortvideoapp.Profile.Edit_Profile_F;
import com.yaar.shortvideoapp.R;
import com.yaar.shortvideoapp.Settings.Setting_F;
import com.yaar.shortvideoapp.SimpleClasses.Fragment_Callback;
import com.yaar.shortvideoapp.SimpleClasses.Variables;
import com.yaar.shortvideoapp.Video_Recording.DraftVideos.DraftVideos_A;

import java.io.File;


public class UserinfoFragment extends Fragment {

    public UserinfoFragment(boolean b, String string) {
    }

    TextView draft_count_txt;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_userinfo, container, false);

        draft_count_txt = view.findViewById(R.id.draft_count_txt);

        view.findViewById(R.id.lnr_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Open_Edit_profile();
            }
        });

        view.findViewById(R.id.lnr_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Open_inbox_F();
            }
        });


        view.findViewById(R.id.lnr_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Open_setting();
            }
        });

        view.findViewById(R.id.draft_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent upload_intent=new Intent(getActivity(), DraftVideos_A.class);
                startActivity(upload_intent);
                getActivity().overridePendingTransition(R.anim.in_from_bottom,R.anim.out_to_top);
            }
        });

        Show_draft_count();


        return view;
    }

    public void Show_draft_count(){
        try {

            String path = Variables.draft_app_folder;
            File directory = new File(path);
            File[] files = directory.listFiles();
            draft_count_txt.setText("" + files.length);
            if (files.length <= 0) {
                view.findViewById(R.id.draft_btn).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.draft_btn).setVisibility(View.VISIBLE);

            }
        }catch (Exception e){
            view.findViewById(R.id.draft_btn).setVisibility(View.GONE);
        }
    }


    public void Open_setting(){
        Setting_F setting_f = new Setting_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, setting_f).commit();
    }

    public void Open_Edit_profile(){
        Edit_Profile_F edit_profile_f = new Edit_Profile_F(new Fragment_Callback() {
            @Override
            public void Responce(Bundle bundle) {
            }
        });
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, edit_profile_f).commit();
    }

    private void Open_inbox_F() {
        Inbox_F inbox_f = new Inbox_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, inbox_f).commit();

    }



}