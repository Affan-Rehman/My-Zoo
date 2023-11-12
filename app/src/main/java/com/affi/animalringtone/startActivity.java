package com.affi.animalringtone;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;


//A very basic activity opening on start with only two layouts(Used as buttons) with functions
public class startActivity extends AppCompatActivity {
    private static startActivity start;
    LinearLayout game,view1,quiz,end;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        game = findViewById(R.id.menuLayout);
        view1 = findViewById(R.id.viewLayout);
        quiz = findViewById(R.id.quizLayout);
        end = findViewById(R.id.exitLayout);
        start = this;
        MobileAds.initialize(this, initializationStatus -> {

        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        quiz.setOnClickListener(view -> {
                    if(checkNet()) {
                        startActivity(new Intent(this, quizActivity.class));
                        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        });
        game.setOnClickListener(view -> {
            startActivity(new Intent(this,controlsActivity.class));
            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
        });

        //closes app
        end.setOnClickListener(view -> {
            Toast.makeText(this, "App Closed!", Toast.LENGTH_SHORT).show();
            finish();
            System.exit(0);
        });

        //Starts MainActivity and our basic functionality of the app
        view1.setOnClickListener(view -> {
            startActivity(new Intent(this,MainActivity.class));
            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
        });

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                Toast.makeText(startActivity.this, "Ad clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                Toast.makeText(startActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {

            }

            @Override
            public void onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        });
    }


    public static startActivity getInstance(){
        return start;
    }
    //Overriding just to display the toast in case the user clicks on back button of android instead of exit
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "App Closed!", Toast.LENGTH_SHORT).show();
    }
    //this function checks for internet connectivity, called when opening fragments with webViews
    public boolean checkNet() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}