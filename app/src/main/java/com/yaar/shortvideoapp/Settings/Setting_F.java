package com.yaar.shortvideoapp.Settings;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yaar.shortvideoapp.Accounts.Request_Varification_F;
import com.yaar.shortvideoapp.Inbox.Inbox_F;
import com.yaar.shortvideoapp.LanguageActivity;
import com.yaar.shortvideoapp.Main_Menu.MainMenuActivity;
import com.yaar.shortvideoapp.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.yaar.shortvideoapp.R;
import com.yaar.shortvideoapp.SimpleClasses.Variables;
import com.yaar.shortvideoapp.SimpleClasses.Webview_F;

/**
 * A simple {@link Fragment} subclass.
 */
public class Setting_F extends RootFragment implements View.OnClickListener {

    View view;
    Context context;

    public Setting_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_setting, container, false);
        context = getContext();

        view.findViewById(R.id.Goback).setOnClickListener(this);
        view.findViewById(R.id.txtNotifcation).setOnClickListener(this);
        view.findViewById(R.id.request_verification_txt).setOnClickListener(this);
        view.findViewById(R.id.txtContactUs).setOnClickListener(this);
        view.findViewById(R.id.privacy_policy_txt).setOnClickListener(this);
        view.findViewById(R.id.txttermsandcondio).setOnClickListener(this);
        view.findViewById(R.id.logout_txt).setOnClickListener(this);
        view.findViewById(R.id.txtLanguage).setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.Goback:
                getActivity().onBackPressed();
                break;

            case R.id.txtNotifcation:
                Open_inbox_F();
                break;

            case R.id.txtLanguage:
                startActivity(new Intent(getActivity(), LanguageActivity.class).putExtra("isSettings", true));
                break;

            case R.id.request_verification_txt:
                Open_request_verification();
                break;
            case R.id.txtContactUs:
                Open_mail();
                break;
            case R.id.txttermsandcondio:
                Open_terms_url();
                break;

            case R.id.privacy_policy_txt:
                Open_Privacy_url();
                break;

            case R.id.logout_txt:
                Logout();
                break;
        }
    }

    private void Open_inbox_F() {
        Inbox_F inbox_f = new Inbox_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, inbox_f).commit();

    }

    public void Open_terms_url() {
        Webview_F webview_f = new Webview_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle bundle = new Bundle();
        bundle.putString("url", Variables.terms_condi);
        bundle.putString("title", "Terms and Conditions");
        webview_f.setArguments(bundle);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Setting_F, webview_f).commit();
    }

    public void Open_request_verification() {
        Request_Varification_F request_varification_f = new Request_Varification_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Setting_F, request_varification_f).commit();
    }


    public void Open_mail() {
//        Intent i = new Intent(Intent.ACTION_SEND);
//        i.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "megaplayswadeshi20@gmail.com" });
//        i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Contact Us Mega Play");
//        i.putExtra(android.content.Intent.EXTRA_TEXT, "White your Details");
//        startActivity(Intent.createChooser(i, "Send email"));

        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"yaarshortvideoapp@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "App feedback");
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            //  ToastUtil.showShortToast(getActivity(), "There are no email client installed on your device.");
        }
    }

    public void Open_Privacy_url() {
        Webview_F webview_f = new Webview_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        Bundle bundle = new Bundle();
        bundle.putString("url", Variables.privacy_policy);
        bundle.putString("title", "Privacy Policy");
        webview_f.setArguments(bundle);
        transaction.addToBackStack(null);
        transaction.replace(R.id.Setting_F, webview_f).commit();
    }

    // this will erase all the user info store in locally and logout the user
    public void Logout() {
        SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
        editor.putString(Variables.u_id, "").clear();
        editor.putString(Variables.u_name, "").clear();
        editor.putString(Variables.u_pic, "").clear();
        editor.putBoolean(Variables.islogin, false).clear();
        editor.commit();
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainMenuActivity.class));
    }


}
