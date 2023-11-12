package com.affi.animalringtone;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Objects;


//Displayed when user clicks on info button to show information of the animal in wikipedia
public class webViewFragment extends Fragment {
    Animal animal;
    TextView back,text;
    private AdView mAdView;

    public webViewFragment() {

    }

    public webViewFragment(Animal animal) {
        this.animal = animal;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_web_view, container, false);
        WebView myWebView = rootview.findViewById(R.id.webview);
        mAdView =rootview.findViewById(R.id.adView5);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        String wikiTitle = animal.name.replace(' ', '_');
        if(Objects.equals(animal.name, "African gecko"))
            wikiTitle = "African_fat-tailed_gecko";
        if(Objects.equals(animal.name, "African Elephant"))
            wikiTitle = "African_Forest_Elephant";


        //just like navFragment, this progress dialog is displayed as long as it takes for the web page to load in webView
        myWebView.setWebViewClient(new WebViewClient() {
        });

        //Loads animal's information on wikipedia
        if(animal.name=="Kinkalow")
            myWebView.loadUrl("https://cats.fandom.com/wiki/Kinkalow");
        else
            myWebView.loadUrl("http://en.wikipedia.org/wiki/" + wikiTitle);
        Toast.makeText(MainActivity.getInstance(), "Opening WikiPedia Page", Toast.LENGTH_SHORT).show();
        back = rootview.findViewById(R.id.back);
        text = rootview.findViewById(R.id.text);

        MobileAds.initialize(MainActivity.getInstance(), initializationStatus -> {});


        //Setting the heading of the layout with the animal's name
        text.setText(animal.name);

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