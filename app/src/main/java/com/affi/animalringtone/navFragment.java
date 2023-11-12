package com.affi.animalringtone;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.util.Objects;


public class navFragment extends Fragment {
    String heading,page;
    ProgressDialog dialog;
    TextView text,back,about1,about2;

    public navFragment() {
        // Required empty public constructor
    }
    public navFragment(String heading,String page){
        this.heading = heading;
        this.page = page;
        dialog = new ProgressDialog(MainActivity.getInstance());
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview;
        if (page == "privacy") {
            // Inflate the layout for this fragment
            rootview = inflater.inflate(R.layout.fragment_nav1, container, false);
            WebView myWebView = rootview.findViewById(R.id.webview1);

            //in the next 2 functions, we display progressDialog on which loads as long as it takes for the web page to load
            myWebView.setWebViewClient(new WebViewClient() {
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    dialog.setTitle(heading);
                    dialog.setMessage("Loading...");
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(false);
                    dialog.show();
                }

                public void onPageFinished(WebView view, String url) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                }
            });

            //Setting a condition to load privacy policy  as we use the same fragment for every nav button in activity_main
            if (Objects.equals(page, "privacy"))
                myWebView.loadUrl("https://itfactori.github.io/myZoo/");
            back = rootview.findViewById(R.id.back1);
            text = rootview.findViewById(R.id.text1);

            //This displays the name of the animal
            text.setText(heading);

            //This textView takes user back to main activity
            back.setOnClickListener(view -> {
                FragmentManager fragmentManager = MainActivity.getInstance().getFragmentManager();
                fragmentManager.popBackStackImmediate();
                if (fragmentManager.getBackStackEntryCount() == 0)
                    MainActivity.getInstance().onBackPressed();
            });

        }

        else{
            // Inflate the layout for this fragment
            rootview = inflater.inflate(R.layout.fragment_nav, container, false);

            back = rootview.findViewById(R.id.back1);
            text = rootview.findViewById(R.id.text1);
            about1 = rootview.findViewById(R.id.website);
            about2 = rootview.findViewById(R.id.website_text);
            text.setText(heading);
                    about1.setOnClickListener(view -> {
                        Uri uri = Uri.parse("https://itfactori.github.io/home/"); // missing 'http://' will cause crash
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    });
            about2.setOnClickListener(view -> {
                Uri uri = Uri.parse("https://itfactori.github.io/home/"); // missing 'http://' will cause crash
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            });



            //This textView takes user back to main activity
            back.setOnClickListener(view -> {
                FragmentManager fragmentManager = MainActivity.getInstance().getFragmentManager();
                fragmentManager.popBackStackImmediate();
                if (fragmentManager.getBackStackEntryCount() == 0)
                    MainActivity.getInstance().onBackPressed();
            });

        }
        return rootview;
    }
}