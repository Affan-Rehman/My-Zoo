package com.affi.animalringtone;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.affi.CustomDialog;
import com.affi.CustomDialog2;
import com.bullhead.equalizer.EqualizerFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import io.gresse.hugo.vumeterlibrary.VuMeterView;


//Displayed when user clicks phone button
public class soundFragment extends Fragment {
TextView back,name;
ImageButton speech;

    public ImageButton getRing() {
        return ring;
    }

    ImageButton ring;

    public ImageButton getNotification() {
        return notification;
    }

    ImageButton notification;
    ImageButton eqButton;
    ImageButton fav;
    ImageButton yt;
    ImageButton wiki;
    ImageButton reset;
ImageView check;
CircleImageView imageView;
    Animal object;
Context c;
VuMeterView eq;
ConstraintLayout layout;
StorageReference sr;
ProgressDialog dialog;
cardViewAdapter v;
    boolean f = false;
    private AdView mAdView;
    public soundFragment() {
        // Required empty public constructor
    }

    public void setChoice(boolean v){
        f = v;
    }

    public soundFragment(Animal a,Context c,cardViewAdapter v) {
     this.c = c;
      this.object =  a;
      this.v = v;
        if(Objects.equals(object.soundUrl, ""))
        v.mediaPlayer = MediaPlayer.create(c, object.getSound());
      else {
            Uri myUri = Uri.parse(object.soundUrl);

            v.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                v.mediaPlayer.setDataSource(c, myUri);
                v. mediaPlayer.prepare();
            }
            catch (Exception r){
                r.printStackTrace();
            }
      }

        v.mediaPlayer.setLooping(true);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("f", false);
        editor.apply();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.fragment_sound2, container, false);

        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("f", true);
        editor.apply();
        back = rootview.findViewById(R.id.back);
        wiki = rootview.findViewById(R.id.wiki);
        speech = rootview.findViewById(R.id.speech);
        ring = rootview.findViewById(R.id.ring);
        notification = rootview.findViewById(R.id.notification);
        imageView = rootview.findViewById(R.id.image);
        eq = rootview.findViewById(R.id.vumeter);
        eqButton = rootview.findViewById(R.id.eqButton);
        check = rootview.findViewById(R.id.check);
        name = rootview.findViewById(R.id.name);
        fav = rootview.findViewById(R.id.fav_sound);
        yt = rootview.findViewById(R.id.yt);
        layout = rootview.findViewById(R.id.background_sound);
        reset = rootview.findViewById(R.id.reset);
        MobileAds.initialize(MainActivity.getInstance(), initializationStatus -> {});

        mAdView = rootview.findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        if(Objects.equals(object.type, "cat"))
            layout.setBackground(MainActivity.getInstance().getDrawable(R.drawable.phone_bg));
       else  if(Objects.equals(object.type, "bird"))
            layout.setBackground(c.getDrawable(R.drawable.bird_bg));
        else  if(Objects.equals(object.type, "lizard")) {
            reset.setBackground(ResourcesCompat.getDrawable(c.getResources(),R.drawable.ic_reset_black,c.getTheme()));
            layout.setBackground(c.getDrawable(R.drawable.lizard_bg));
        }
        else  if(Objects.equals(object.type, "snake")){
            layout.setBackground(c.getDrawable(R.drawable.snake_bg));
            speech.setBackground(ResourcesCompat.getDrawable(c.getResources(),R.drawable.ic_mic2,c.getTheme()));
            notification.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.svg_notification_press, c.getTheme()));
            ring.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.ic_ring_press, c.getTheme()));
            eqButton.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.ic_eq2, c.getTheme()));
        }
        else  if(Objects.equals(object.type, "horse")){
            speech.setBackground(ResourcesCompat.getDrawable(c.getResources(),R.drawable.ic_mic2,c.getTheme()));
            notification.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.svg_notification_press, c.getTheme()));
            ring.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.ic_ring_press, c.getTheme()));
            eqButton.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.ic_eq2, c.getTheme()));
            layout.setBackground(c.getDrawable(R.drawable.horse_bg));}
        else  if(Objects.equals(object.type, "crocodile")){
            speech.setBackground(ResourcesCompat.getDrawable(c.getResources(),R.drawable.ic_mic2,c.getTheme()));
            notification.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.svg_notification_press, c.getTheme()));
            ring.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.ic_ring_press, c.getTheme()));
            eqButton.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.ic_eq2, c.getTheme()));
            layout.setBackground(c.getDrawable(R.drawable.crocodile_bg));}
        else  if(Objects.equals(object.type, "sea")) {
            speech.setBackground(ResourcesCompat.getDrawable(c.getResources(),R.drawable.ic_mic2,c.getTheme()));
            notification.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.svg_notification_press, c.getTheme()));
            ring.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.ic_ring_press, c.getTheme()));
            eqButton.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.ic_eq2, c.getTheme()));
            layout.setBackground(c.getDrawable(R.drawable.sea_bg));
        }
        else  if(Objects.equals(object.type, "endangered")) {
            layout.setBackground(c.getDrawable(R.drawable.endangered_bg));
            reset.setBackground(ResourcesCompat.getDrawable(c.getResources(),R.drawable.ic_reset_black,c.getTheme()));
        }
        else
        layout.setBackground(c.getDrawable(R.drawable.phone_bg));

        name.setText(object.name);

        if(!Objects.equals(object.purl, "")){
            dialog = new ProgressDialog(c);
            dialog.setMessage("Fetching Data from Server");
            dialog.setTitle("Please wait...");
            dialog.setCancelable(false);
            dialog.show();
            sr= FirebaseStorage.getInstance().getReference(object.purl + ".jpg");
            try {
                File localFile = File.createTempFile("tempfile",".jpg");
                sr.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    imageView.setImageBitmap(bitmap);
                    if(dialog.isShowing())
                        dialog.dismiss();
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            //setting imageView with animal's image
            imageView.setImageDrawable(ResourcesCompat.getDrawable(c.getResources(), object.image, c.getTheme()));
        }
        //This block of code tells whether the fav button is selected for each animal or not based on Boolean selected attribute
        if(object.selected)
            fav.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.ic_fav, c.getTheme()));
        else
            fav.setBackground(ResourcesCompat.getDrawable(c.getResources(),R.drawable.ic_remove,c.getTheme()));
        ///////////////////


        reset.setOnClickListener(view -> {
            Uri path;
            RingtoneManager manager = new RingtoneManager(c);
            manager.setType(RingtoneManager.TYPE_RINGTONE);
            Cursor cursor = manager.getCursor();
            while (cursor!=null && cursor.moveToNext()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(c)) {
                    Toast.makeText(c,  " default set", Toast.LENGTH_SHORT).show();
                    path = manager.getRingtoneUri(cursor.getPosition());
                    // Do something with the title and the URI of ringtone
                    ring.setBackground(c.getDrawable(R.drawable.ic_ring));
                    notification.setBackground(c.getDrawable(R.drawable.svg_notification));
                    RingtoneManager.setActualDefaultRingtoneUri(c, RingtoneManager.TYPE_RINGTONE,path);
                    RingtoneManager.setActualDefaultRingtoneUri(c.getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION, path);
                    cursor.close();
                    break;
                }
                else {
                    Toast.makeText(c, "Grant permissions first!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + c.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(intent);
                }
            }
            }


        });
        fav.setOnClickListener(view -> {
            if(Objects.equals(object.purl, "")) {
                //Very imp
                //Sets animal in favs list

                //This if-else checks whether to remove or add animal based on int count attribute
                //We also keep a check of only 5 animals in favs

                //this adds
                if (object.count % 2 == 0) {
                    if (MainActivity.getInstance().favs.size() < 10) {
                        MainActivity.getInstance().favs.add(object);
                        view.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.ic_fav, c.getTheme()));
                        object.count++;
                        object.selected = true;
                        MainActivity.getInstance().ad.notifyDataSetChanged();
                    } else
                        Toast.makeText(c, "Fav length reached", Toast.LENGTH_SHORT).show();
                }
                //this removes
                else {
                    for (int i = 0; i < MainActivity.getInstance().favs.size(); i++) {
                        if (MainActivity.getInstance().favs.get(i).name.equals(object.name)) {
                            MainActivity.getInstance().favs.remove(i);
                            view.setBackground(ResourcesCompat.getDrawable(c.getResources(), R.drawable.ic_remove, c.getTheme()));
                            object.count++;
                            object.selected = false;
                            for (Animal a : MainActivity.getInstance().all) {
                                if (Objects.equals(a.name, object.name)) {
                                    a.selected = false;
                                    a.count = 0;
                                }
                            }
                        }
                    }
                }
            }
            else
                Toast.makeText(c, "Server Data can not be made fav!", Toast.LENGTH_SHORT).show();
        });
        //this button displays equaliser fragment
        eqButton.setOnClickListener(view -> {
            if(v.mediaPlayer.isPlaying()){
            int sessionId = v.mediaPlayer.getAudioSessionId();
            EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
                    .setAccentColor(Color.parseColor("#4caf50"))
                    .setAudioSessionId(sessionId)
                    .build();
            MainActivity.getInstance().getSupportFragmentManager().beginTransaction()
                    . setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.exit_to_left, R.anim.exit_to_right)
                    .replace(R.id.fragment, equalizerFragment)
                    .addToBackStack(null)
                    .setReorderingAllowed(true)
                    .commit();}
            else
                Toast.makeText(c, "No media playing!", Toast.LENGTH_SHORT).show();
        });

        //This textView takes user back to MainActivity with all the recyclerViews
        back.setOnClickListener(view -> {
            FragmentManager fragmentManager = MainActivity.getInstance().getFragmentManager();
            fragmentManager.popBackStack();
            if (fragmentManager.getBackStackEntryCount() == 0) {
                MainActivity.getInstance().onBackPressed();
                if(v.mediaPlayer.isPlaying()) {
                    v.mediaPlayer.stop();
                    eq.stop(true);
                }
            }
        });

        //this button lets user view animal's information on wikipedia
        wiki.setOnClickListener(view -> {
            if(MainActivity.getInstance().checkNet()) {
                webViewFragment fragment = new webViewFragment(object);
                if (v.mediaPlayer.isPlaying()) {
                    eq.stop(true);
                    check.setBackground(c.getDrawable(R.drawable.ic_play));
                    v.mediaPlayer.pause();
                }
                if(MainActivity.getInstance().adStatus())
                    MainActivity.getInstance().loadAd();
                FragmentTransaction transaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.setReorderingAllowed(true);
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.exit_to_left, R.anim.exit_to_right);
                transaction.replace(R.id.fragment, fragment).commit();
            }
            else{
                Toast.makeText(c, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });

        //this button lets user hear the pronunciation of the name
        speech.setOnClickListener(view -> {
            Toast.makeText(MainActivity.getInstance(), object.name, Toast.LENGTH_SHORT).show();
            MainActivity.getInstance().textToSpeech(object.name);
        });

        //This button lets user set animal's sound as default ringtone and asks for permissions if not granted
        ring.setOnClickListener(view -> {

            Uri soundUri;
            if(Objects.equals(object.soundUrl, "")) {
                int soundId = object.getSound(); // Replace "sound_file_name" with the name of your sound file
                 soundUri = Uri.parse("android.resource://" + c.getPackageName() + "/" + soundId);
            }
            else{
                soundUri = Uri.parse(object.soundUrl);
            }
            // Check if the app has permission to write to system settings
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(getContext())) {
                // If not, request permission from user
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" +c.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            }
            CustomDialog cdd=new CustomDialog(MainActivity.getInstance(),this,soundUri,object);
            cdd.show();
        });

        //This button lets user set animal's sound as default notification's sound and asks for permissions if not granted
        notification.setOnClickListener(view -> {
            Uri soundUri;
            if(Objects.equals(object.soundUrl, "")) {
                int soundId = object.getSound(); // Replace "sound_file_name" with the name of your sound file
                soundUri = Uri.parse("android.resource://" + c.getPackageName() + "/" + soundId);
            }
            else{
                soundUri = Uri.parse(object.soundUrl);
            }
            // Check if the app has permission to write to system settings
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(c)) {
                    CustomDialog2 cdd=new CustomDialog2(MainActivity.getInstance(),this,soundUri,object);
                    cdd.show();

                } else {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + c.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(intent);
                }
            }

        });

        //Clicking on image will let user play and pause the animal's sound as required
        imageView.setOnClickListener(view -> {

            if(v.mediaPlayer.isPlaying()) {
                v.mediaPlayer.pause();
                check.setBackground(c.getDrawable(R.drawable.ic_play));
                eq.stop(true);
            }
            else {
                v.mediaPlayer.start();
                f = true;
                check.setBackground(c.getDrawable(R.drawable.ic_pause));
                eq.resume(true);
            }
        });
        yt.setOnClickListener(view -> {
            if(!Objects.equals(object.yt, "")) {
                if (MainActivity.getInstance().checkNet()) {
                    ytFragment fragment = new ytFragment(object);
                    if(MainActivity.getInstance().adStatus())
                        MainActivity.getInstance().loadAd();
                    if (v.mediaPlayer.isPlaying()) {
                        eq.stop(true);
                        check.setBackground(c.getDrawable(R.drawable.ic_play));
                        v.mediaPlayer.pause();
                    }

                    FragmentTransaction transaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                    transaction.addToBackStack(null);
                    transaction.setReorderingAllowed(true);
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.exit_to_left, R.anim.exit_to_right);
                    transaction.replace(R.id.fragment_2, fragment).commit();
                } else {
                    Toast.makeText(c, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
            else
                Snackbar.make(view,"No youtube video found on this animal",Snackbar.LENGTH_SHORT).show();
        });

        return rootview;
    }
}
