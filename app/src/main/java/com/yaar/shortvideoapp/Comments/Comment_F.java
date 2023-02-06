package com.yaar.shortvideoapp.Comments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yaar.shortvideoapp.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.yaar.shortvideoapp.Profile.Profile_F;
import com.yaar.shortvideoapp.R;
import com.yaar.shortvideoapp.SimpleClasses.API_CallBack;
import com.yaar.shortvideoapp.SimpleClasses.ApiRequest;
import com.yaar.shortvideoapp.SimpleClasses.Fragment_Callback;
import com.yaar.shortvideoapp.SimpleClasses.Fragment_Data_Send;
import com.yaar.shortvideoapp.SimpleClasses.Functions;
import com.yaar.shortvideoapp.SimpleClasses.Variables;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Comment_F extends RootFragment {

    View view;
    Context context;

    RecyclerView recyclerView;

    Comments_Adapter adapter;

    ArrayList<Comment_Get_Set> data_list;

    String video_id;
    String user_id;
    String from;

    EditText message_edit;
    ImageButton send_btn;
    ProgressBar send_progress;

    TextView comment_count_txt;

    FrameLayout comment_screen;

    public static int comment_count=0;
    public Comment_F() {

    }

    Fragment_Data_Send fragment_data_send;
    @SuppressLint("ValidFragment")
    public Comment_F(int count, Fragment_Data_Send fragment_data_send){
        comment_count=count;
        this.fragment_data_send=fragment_data_send;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_comment, container, false);
        context=getContext();


        comment_screen=view.findViewById(R.id.comment_screen);
        comment_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().onBackPressed();

            }
        });

        view.findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().onBackPressed();
            }
        });


        Bundle bundle=getArguments();
        if(bundle!=null){
            video_id=bundle.getString("video_id");
            user_id=bundle.getString("user_id");
            from=bundle.getString("from");
        }


        comment_count_txt=view.findViewById(R.id.comment_count);

        recyclerView=view.findViewById(R.id.recylerview);
        LinearLayoutManager layoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(false);


        data_list=new ArrayList<>();
        adapter=new Comments_Adapter(context, data_list, new Comments_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, Comment_Get_Set item, View view) {
                switch(view.getId()) {
                    case R.id.imgdeletecoment:
                        deletecomment(item.comment_id);
                    break;
                    case R.id.user_pic:
                       OpenProfile(item , false);

                        break;

                    case R.id.username:
                        OpenProfile(item , false);

                        break;
                }

            }
        });

        recyclerView.setAdapter(adapter);


        message_edit=view.findViewById(R.id.message_edit);


        send_progress=view.findViewById(R.id.send_progress);
        send_btn=view.findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message=message_edit.getText().toString();
                if(!TextUtils.isEmpty(message)){
                    if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)){
                    Send_Comments(video_id,message);
                    message_edit.setText(null);
                    send_progress.setVisibility(View.VISIBLE);
                    send_btn.setVisibility(View.GONE);
                    }
                    else {
                        Toast.makeText(context, "Please Login into the app", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });




        Get_All_Comments();


        return view;
    }


    // this will open the profile of user which have uploaded the currenlty running video
    private void OpenProfile(Comment_Get_Set item, boolean from_right_to_left) {

        //pagination_progress.setVisibility(View.VISIBLE);

//        if(Variables.sharedPreferences.getString(Variables.u_id,"0").equals(item.video_fb_id)){
//
//            TabLayout.Tab profile= MainMenuFragment.tabLayout.getTabAt(4);
//            profile.select();

//        }else {
            Profile_F profile_f = new Profile_F(new Fragment_Callback() {
                @Override
                public void Responce(Bundle bundle) {
                  //  Call_Api_For_Singlevideos(currentPage);
                }
            });
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            if(from_right_to_left)
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            else
                transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);

            Bundle args = new Bundle();
            args.putString("user_id", item.fb_id);
            args.putString("user_name",item.first_name+" "+item.last_name);
            args.putString("user_pic",item.profile_pic);
            profile_f.setArguments(args);
            transaction.addToBackStack(null);

            if (from.equals("watch")){
                transaction.replace(R.id.WatchVideo_F, profile_f).commit();
            }else {
                transaction.replace(R.id.MainMenuFragment, profile_f).commit();
            }


       // }

    }

    private void deletecomment(String comment_id) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Variables.DELETE_COMMENT,
                new Response.Listener<String>() {


                    @Override
                    public void onResponse(String response) {
                        // progressDialog.dismiss();
                        System.out.println("");
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String message = jsonObject.getString("message");


                            if (code.equalsIgnoreCase("200")) {
                                Toast.makeText(context , message +"Your request has been accepted We will delete your comment in few minuts. ", Toast.LENGTH_LONG).show();


                            } else {
                                Toast.makeText(context ,"Your request has been accepted We will delete your comment in few minuts. ", Toast.LENGTH_LONG).show();


                            }

                            getActivity().onBackPressed();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println("");
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                params.put("id",comment_id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // headers.put("token", Const.TOKEN);
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);

    }

    @Override
    public void onDetach() {
        Functions.hideSoftKeyboard(getActivity());

        super.onDetach();
    }

    // this funtion will get all the comments against post
    public void Get_All_Comments(){

        Functions.Call_Api_For_get_Comment(getActivity(), video_id, new API_CallBack() {
            @Override
            public void ArrayData(ArrayList arrayList) {
                ArrayList<Comment_Get_Set> arrayList1=arrayList;
                for(Comment_Get_Set item:arrayList1){
                     data_list.add(item);
                }
                comment_count_txt.setText(data_list.size()+" comments");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void OnSuccess(String responce) {

            }

            @Override
            public void OnFail(String responce) {

            }

        });

    }




    // this function will call an api to upload your comment
    public void Send_Comments(String video_id, final String comment){

        Functions.Call_Api_For_Send_Comment(getActivity(), video_id,comment ,new API_CallBack() {
            @Override
            public void ArrayData(ArrayList arrayList) {
                send_progress.setVisibility(View.GONE);
                send_btn.setVisibility(View.VISIBLE);

                ArrayList<Comment_Get_Set> arrayList1=arrayList;
                for(Comment_Get_Set item:arrayList1){
                    data_list.add(data_list.size(),item);
                    comment_count++;

                    SendPushNotification(getActivity(),user_id,comment);

                    comment_count_txt.setText(comment_count+" comments");

                    if(fragment_data_send!=null)
                        fragment_data_send.onDataSent(""+comment_count);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void OnSuccess(String responce) {

            }

            @Override
            public void OnFail(String responce) {

            }
        });

    }



    public  void SendPushNotification(Activity activity,String user_id,String comment){

        JSONObject notimap= new JSONObject();
        try {
            notimap.put("title",Variables.sharedPreferences.getString(Variables.u_name,"")+" Comment on your video");
            notimap.put("message",comment);
            notimap.put("icon",Variables.sharedPreferences.getString(Variables.u_pic,""));
            notimap.put("senderid",Variables.sharedPreferences.getString(Variables.u_id,""));
            notimap.put("receiverid", user_id);
            notimap.put("action_type","comment");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context,Variables.sendPushNotification,notimap,null);

    }




}
