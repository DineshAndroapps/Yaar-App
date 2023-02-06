package com.yaar.shortvideoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.yaar.shortvideoapp.Discover.Discover_Adapter;
import com.yaar.shortvideoapp.Discover.Discover_Get_Set;
import com.yaar.shortvideoapp.Home.Home_Get_Set;
import com.yaar.shortvideoapp.Main_Menu.MainMenuActivity;
import com.yaar.shortvideoapp.Search.Search_Main_F;
import com.yaar.shortvideoapp.SimpleClasses.ApiRequest;
import com.yaar.shortvideoapp.SimpleClasses.Callback;
import com.yaar.shortvideoapp.SimpleClasses.Variables;
import com.yaar.shortvideoapp.WatchVideos.WatchVideos_F;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    Context context;

    RecyclerView recyclerView;
    EditText search_edit;
    Discover_Adapter adapter;
    ImageView imgBack;
    ArrayList<Discover_Get_Set> datalist;

    SwipeRefreshLayout swiperefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //Open_search();


        search_edit = findViewById(R.id.search_edit);
        search_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String query = search_edit.getText().toString();
                if (adapter != null)
                    adapter.getFilter().filter(query);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        swiperefresh = findViewById(R.id.swiperefresh);
        swiperefresh.setColorSchemeResources(R.color.black);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

              //  Call_Api_For_get_Allvideos();
            }
        });

        findViewById(R.id.search_layout).setOnClickListener(this);
        findViewById(R.id.search_edit).setOnClickListener(this);

      //  Call_Api_For_get_Allvideos();


    }

    private void Call_Api_For_get_Allvideos() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("fb_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("resp", parameters.toString());

        ApiRequest.Call_Api(getApplicationContext(), Variables.discover, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Parse_data(resp);
                swiperefresh.setRefreshing(false);
            }
        });


    }

    public void Parse_data(String responce) {

        datalist.clear();

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                for (int d = 0; d < msgArray.length(); d++) {

                    Discover_Get_Set discover_get_set = new Discover_Get_Set();
                    JSONObject discover_object = msgArray.optJSONObject(d);
                    discover_get_set.title = discover_object.optString("section_name");

                    JSONArray video_array = discover_object.optJSONArray("sections_videos");

                    ArrayList<Home_Get_Set> video_list = new ArrayList<>();
                    for (int i = 0; i < video_array.length(); i++) {
                        JSONObject itemdata = video_array.optJSONObject(i);
                        Home_Get_Set item = new Home_Get_Set();


                        JSONObject user_info = itemdata.optJSONObject("user_info");
                        item.fb_id = user_info.optString("fb_id");
                        item.username = user_info.optString("username");
                        item.first_name = user_info.optString("first_name");
                        item.last_name = user_info.optString("last_name");
                        item.profile_pic = user_info.optString("profile_pic");
                        item.verified = user_info.optString("verified");

                        JSONObject count = itemdata.optJSONObject("count");
                        item.like_count = count.optString("like_count");
                        item.video_comment_count = count.optString("video_comment_count");


                        JSONObject sound_data = itemdata.optJSONObject("sound");
                        item.sound_id = sound_data.optString("id");
                        item.sound_name = sound_data.optString("sound_name");
                        item.sound_pic = sound_data.optString("thum");
                        if (sound_data != null) {
                            JSONObject audio_path = sound_data.optJSONObject("audio_path");
                            item.sound_url_mp3 = audio_path.optString("mp3");
                            item.sound_url_acc = audio_path.optString("acc");
                        }


                        item.video_id = itemdata.optString("id");
                        item.liked = itemdata.optString("liked");


                        item.video_url = itemdata.optString("video");


                        item.thum = itemdata.optString("thum");
                        item.gif = itemdata.optString("gif");
                        item.created_date = itemdata.optString("created");
                        item.video_description = itemdata.optString("description");

                        video_list.add(item);
                    }

                    discover_get_set.arrayList = video_list;

                    datalist.add(discover_get_set);

                }

                adapter.notifyDataSetChanged();

//                try{
                ((MainMenuActivity) getApplicationContext()).setCategory(datalist);
//                }catch (Exception e){
//                    Log.d("101010","Exception :"+e.getMessage());
//
//                    e.printStackTrace();
//                }

            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // When you click on any Video a new activity is open which will play the Clicked video
    private void OpenWatchVideo(int postion, ArrayList<Home_Get_Set> data_list) {

        Intent intent = new Intent(getApplicationContext(), WatchVideos_F.class);
        intent.putExtra("arraylist", data_list);
        intent.putExtra("position", postion);
        startActivity(intent);

    }


    public void Open_search() {
        Search_Main_F search_main_f = new Search_Main_F();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, search_main_f).commit();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_layout:
                Open_search();
                break;
            case R.id.search_edit:
                Open_search();
                break;

        }
    }
}