package com.affi.animalringtone;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class ytFragment extends Fragment {
        Animal a;
    TextView back;
    public ytFragment() {
        // Required empty public constructor
    }
    public ytFragment(Animal a){
        this.a = a;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_yt, container, false);
        WebView myWebView = rootview.findViewById(R.id.webview);

        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setPluginState(WebSettings.PluginState.ON);

        myWebView.setWebChromeClient(new WebChromeClient());
        //just like navFragment, this progress dialog is displayed as long as it takes for the web page to load in webView
        myWebView.setWebViewClient(new WebViewClient() {


        });

            myWebView.getSettings().setPluginState(WebSettings.PluginState.ON);

        //Loads animal's information on wikipedia
        myWebView.loadUrl("https://www.youtube.com/watch?v=" + a.yt);
        Toast.makeText(MainActivity.getInstance(), "Opening Youtube Page", Toast.LENGTH_SHORT).show();
        back = rootview.findViewById(R.id.back);

        //this textView takes user back to soundFragment screen
        back.setOnClickListener(view -> {
            FragmentManager fragmentManager = MainActivity.getInstance().getFragmentManager();
            fragmentManager.popBackStack();
            if (fragmentManager.getBackStackEntryCount() == 0)
                MainActivity.getInstance().onBackPressed();

        });

        return  rootview;
    }
    }
