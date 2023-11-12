package com.affi.animalringtone;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class quizActivity extends AppCompatActivity {
    TextView back;
    private AdView mAdView;
    static boolean active = false;
    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        back = findViewById(R.id.back);
        WebView myWebView = findViewById(R.id.webview_quiz);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        Toast.makeText(this, " Quiz Page", Toast.LENGTH_SHORT).show();

        MobileAds.initialize(MainActivity.getInstance(), initializationStatus -> {});

        mAdView = findViewById(R.id.adView4);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        myWebView.loadUrl("https://www.riddle.com/embed/showcase/210051");
        back.setOnClickListener(view -> onBackPressed());
    }

}