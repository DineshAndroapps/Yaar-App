package com.yaar.shortvideoapp.Main_Menu;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yaar.shortvideoapp.BaseActivity;
import com.yaar.shortvideoapp.R;

import java.util.Timer;
import java.util.TimerTask;

public class BeCreator extends BaseActivity {
    ProgressDialog progressDialog;
    Timer timer;
    WebView simpleWebView;
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
//    @Override
//    public void onBackPressed() {
//
//        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
//        builder1.setMessage("Do You Want To Logout  ");
//        builder1.setCancelable(true);
//        builder1.setPositiveButton(
//                "Yes",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//
//                        dialog.cancel();
//                        // GetTicket(gameid);
//
//                        // Ticketlist();
//
//
//                    }
//                });
//        builder1.setNegativeButton(
//                "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        //  txt_noTicket.setVisibility(View.VISIBLE);
//
//        AlertDialog alert11 = builder1.create();
//        alert11.show();
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gamezone);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
         simpleWebView = (WebView) findViewById(R.id.simpleWebView);
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                progressDialog.dismiss();

            }
        },1000);

        simpleWebView.setWebViewClient(new MyWebViewClient());
// specify the url of the web page in loadUrl function
        String url = "https://megaplaystar.com/connect/creator.php";
        simpleWebView.getSettings().setJavaScriptEnabled(true);
        simpleWebView.loadUrl(url);
        simpleWebView.canGoBack();
//        Boolean canGoForword=simpleWebView.canGoForward();
//        simpleWebView.clearHistory();
    }
    @Override
    public void onBackPressed() {
//        if(simpleWebView.canGoBack()) {
////            simpleWebView.goBack();
////        } else {
////
////            AlertDialog.Builder builder1 = new AlertDialog.Builder(BeCreator.this);
////        builder1.setMessage("Do You Want To Exit  ");
////        builder1.setCancelable(true);
////        builder1.setPositiveButton(
////                "Yes",
////                new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int id) {
////                        finish();
////                        dialog.cancel();
////                        // GetTicket(gameid);
////
////                        // Ticketlist();
////
////
////                    }
////                });
////        builder1.setNegativeButton(
////                "No",
////                new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int id) {
////                        dialog.cancel();
////                    }
////                });
////        //  txt_noTicket.setVisibility(View.VISIBLE);
////
////        AlertDialog alert11 = builder1.create();
////        alert11.show();
////        }
        finish();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url); // load the url
            return true;
        }
    }
}