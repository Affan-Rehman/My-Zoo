package com.affi.animalringtone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.affi.CustomDialogClass;
import com.bullhead.equalizer.EqualizerFragment;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.gresse.hugo.vumeterlibrary.VuMeterView;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, NavigationView.OnNavigationItemSelectedListener {
    @SuppressLint("StaticFieldLeak")
    //Defining all variables
    public static MainActivity instanceMainActivity;
    static int firstVisibleInListview;
    public DrawerLayout drawerLayout;
    private InterstitialAd mInterstitialAd;
    //cardView recycler for cardViewAdapter
    RecyclerView cardView;
    //making lists that will pass as argument to cardViewAdapter
    List<Animal> cats;
    List<Animal> horses;
    List<Animal> favs;
    List<Animal> birds;
    List<Animal> sea;
    List<Animal> all;
    List<Animal> lizards;
    List<Animal> snakes;
    List<Animal> crocodiles;
    List<Animal> endangered;
    List<Animal> extras;
    TextView allT, birdT, favT,horseT, catT, seaT, arrow,endangeredT,extraT;
    int count = 0;
    int counter =0;
    public TextView reptiles;
    cardViewAdapter ad;
    SharedPreferences share;
    String file = "";
    VuMeterView eq;
    ImageView nav;
    LinearLayoutManager layout;
    DatabaseReference database;
    private TextToSpeech repeatTTS;

    public static MainActivity getInstance() {
        return instanceMainActivity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ad.mediaPlayer!=null) {
            ImageView myImageView = findViewById(R.id.check);

            if(myImageView!=null) {
                SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                boolean m = prefs.getBoolean("f", false);
                if (ad.frag && m) {
                    myImageView.setBackground(getDrawable(R.drawable.ic_play));
                    eq = findViewById(R.id.vumeter);
                    eq.stop(true);

                }
            }
        }
    }



    @Override
    public void onBackPressed() {
        // Check if the current fragment is MyFragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (currentFragment instanceof EqualizerFragment) {
            ImageView myImageView = findViewById(R.id.check);
            myImageView.setBackground(getDrawable(R.drawable.ic_play));
            eq = findViewById(R.id.vumeter);
            eq.stop(true);
        }
        super.onBackPressed();
        if(currentFragment instanceof EqualizerFragment){
            ad.mediaPlayer.pause();
            return;
        }
        if(ad.mediaPlayer.isPlaying())
            ad.mediaPlayer.stop();
        if(counter%2==0)
        showInterstitial();
        counter++;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(ad.edit!=null)
        {
            //Used to store the value for each favourite to shared preferences
            for (int i = 0; i < favs.size(); i++) {
                ad.edit.putString("name" + i, favs.get(i).getName());
                ad.edit.putInt("sound" + i, favs.get(i).getSound());
                ad.edit.putInt("image" + i, favs.get(i).getImage());
                ad.edit.putString("yt" + i, favs.get(i).getYt());
                ad.edit.putString("type" + i, favs.get(i).getType());
            }
            ad.edit.apply();
        }
        if(ad.mediaPlayer!=null){
            if(ad.mediaPlayer.isPlaying())
                ad.mediaPlayer.pause();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialising every member
        instanceMainActivity = this;
        cats = new ArrayList<>();
        horses = new ArrayList<>();
        birds = new ArrayList<>();
        sea = new ArrayList<>();
        all = new ArrayList<>();
        snakes = new ArrayList<>();
        crocodiles = new ArrayList<>();
        lizards = new ArrayList<>();
        extras = new ArrayList<>();
        endangered = new ArrayList<>();
        repeatTTS = new TextToSpeech(this, this);
        cardView = findViewById(R.id.cardView);
        share = getSharedPreferences(file, 0);
        favs = new ArrayList<>();
        allT = findViewById(R.id.all);
        birdT = findViewById(R.id.bird);
        favT = findViewById(R.id.fav);
        horseT = findViewById(R.id.horses);
        catT = findViewById(R.id.cat);
        seaT = findViewById(R.id.sea);
        endangeredT = findViewById(R.id.endangered);
        arrow = findViewById(R.id.arrow);
        reptiles = findViewById(R.id.reptiles);
        extraT = findViewById(R.id.server);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        NavigationView mNavigationView = findViewById(R.id.account_navigation_view);
        layout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        nav = findViewById(R.id.drawer);
        database = FirebaseDatabase.getInstance().getReference("animals");
        cardView.setHasFixedSize(true);
        //arrow is the small icon displayed at bottom of screen when user scrolls
        arrow.setVisibility(View.GONE);

        mNavigationView.setNavigationItemSelectedListener(this);

        //Adding favs from shared preferences
        if (share.contains("name0"))
            favs.add(new Animal(1, share.getString("name0", ""), share.getInt("sound0", R.raw.persian), share.getInt("image0", R.raw.persian), true,share.getString("yt0", "")));

        if (share.contains("name1"))
            favs.add(new Animal(1, share.getString("name1", ""), share.getInt("sound1", R.raw.persian), share.getInt("image1", R.raw.persian), true,share.getString("yt1", "")));

        if (share.contains("name2"))
            favs.add(new Animal(1, share.getString("name2", ""), share.getInt("sound2", R.raw.persian), share.getInt("image2", R.raw.persian), true,share.getString("yt2", "")));

        if (share.contains("name3"))
            favs.add(new Animal(1, share.getString("name3", ""), share.getInt("sound3", R.raw.persian), share.getInt("image3", R.raw.persian), true,share.getString("yt3", "")));

        if (share.contains("name4"))
            favs.add(new Animal(1, share.getString("name4", ""), share.getInt("sound4", R.raw.persian), share.getInt("image4", R.raw.persian), true,share.getString("yt4", "")));
        if (share.contains("name5"))
            favs.add(new Animal(1, share.getString("name5", ""), share.getInt("sound5", R.raw.persian), share.getInt("image5", R.raw.persian), true,share.getString("yt5", "")));

        if (share.contains("name6"))
            favs.add(new Animal(1, share.getString("name6", ""), share.getInt("sound6", R.raw.persian), share.getInt("image6", R.raw.persian), true,share.getString("yt6", "")));

        if (share.contains("name7"))
            favs.add(new Animal(1, share.getString("name7", ""), share.getInt("sound7", R.raw.persian), share.getInt("image7", R.raw.persian), true,share.getString("yt7", "")));

        if (share.contains("name8"))
            favs.add(new Animal(1, share.getString("name8", ""), share.getInt("sound8", R.raw.persian), share.getInt("image8", R.raw.persian), true,share.getString("yt8", "")));

        if (share.contains("name9"))
            favs.add(new Animal(1, share.getString("name9", ""), share.getInt("sound9", R.raw.persian), share.getInt("image9", R.raw.persian), true,share.getString("yt9", "")));


        for(int i = 0; i<favs.size();i++){
            favs.get(i).setType(share.getString("type" + i, "cat"));
        }
        //Making cardView cats
        Animal c1 = new Animal(0, "Persian Cat", R.raw.persian, R.drawable.persian, false,"QqZ7YkTdGIY");
        Animal c2 = new Animal(0, "Siamese Cat", R.raw.siamese, R.drawable.siamesecat, false,"2LyVbxmMxqE");
        Animal c3 = new Animal(0, "Himalayan cat", R.raw.himalayan, R.drawable.himalayan, false,"64X3CFr-uW4");
        Animal c4 = new Animal(0, "Feral Cat", R.raw.stray, R.drawable.stray, false,"Mdz_L96Nu5E");
        Animal c5 = new Animal(0, "Munchkin Cat", R.raw.munchkin, R.drawable.munchkin, false,"GCgDOXgxbHQ");
        Animal c6 = new Animal(0, "Balinese Cat", R.raw.balinese, R.drawable.balinese, false,"D4OHPptHATM");
        Animal c7 = new Animal(0, "Donskoy Cat", R.raw.donskoy, R.drawable.donskoy, false,"gQxl9meqbxY");
        Animal c8 = new Animal(0, "Kinkalow", R.raw.kinkalow, R.drawable.kinkalow, false,"UXO30osFOk4");
        Animal c9 = new Animal(0, "Maine Coon", R.raw.maine, R.drawable.maine, false,"g_LNu6Aaxvk");
        Animal c0 = new Animal(0, "Ragdoll", R.raw.ragdoll, R.drawable.ragdoll, false,"dVg4zgkUriI");


        //Making cardView horses
        Animal h0 = new Animal(0, "Friesian horse", R.raw.fersianhorse, R.drawable.fersianhorse, false,"G7cQP24b1sQ");
        Animal h1 = new Animal(0, "Arabian Horse", R.raw.arabian, R.drawable.arabian, false,"vLzSgLbRsq0");
        Animal h2 = new Animal(0, "Mustang", R.raw.mustang, R.drawable.mustang, false,"2m8nZhkvPLk");
        Animal h3 = new Animal(0, "Shire Horse", R.raw.shire, R.drawable.shire, false,"8PtAMnGgQ4o");
        Animal h4 = new Animal(0, "Lusitano", R.raw.last, R.drawable.lusitano, false,"XriQFPuNLSc");
        Animal h5 = new Animal(0, "Ardennais", R.raw.ardennais, R.drawable.ardennais, false,"G7cQP24b1sQ");
        Animal h6 = new Animal(0, "Falabella", R.raw.falabella, R.drawable.fabella, false,"vLzSgLbRsq0");
        Animal h7 = new Animal(0, "Haflinger", R.raw.haflinger, R.drawable.haflinger, false,"2m8nZhkvPLk");
        Animal h8 = new Animal(0, "Paso Fino", R.raw.paso, R.drawable.paso, false,"8PtAMnGgQ4o");
        Animal h9 = new Animal(0, "Thoroughbred", R.raw.thoroughbred, R.drawable.thoroughbred, false,"XriQFPuNLSc");


        //Making bird cardView
        Animal b0 = new Animal(0, "Hawk", R.raw.hawk, R.drawable.hawk, false,"G-wHuC_7xlg");
        Animal b1 = new Animal(0, "Eagle", R.raw.eagle, R.drawable.eagle, false,"Yaq-Qt1TXaw");
        Animal b2 = new Animal(0, "Parrot", R.raw.parrot, R.drawable.parrot, false,"o0syttKqVhA");
        Animal b3 = new Animal(0, "Owl", R.raw.owl, R.drawable.owl, false,"_qffjityHI4");
        Animal b4 = new Animal(0, "Hummingbird", R.raw.humming, R.drawable.humming, false,"dVykWl3wDjI");
        Animal b5 = new Animal(0, "Chicken", R.raw.chicken, R.drawable.chicken, false,"wYKJkHcaMzE");
        Animal b6 = new Animal(0, "Chukar Partridge", R.raw.chukar, R.drawable.chukar, false,"CozlxNnVw_g");
        Animal b7 = new Animal(0, "Cormorant", R.raw.cormorant, R.drawable.cormorants, false,"7TyVsdj-6vA");
        Animal b8 = new Animal(0, "Hornbill", R.raw.hornbill, R.drawable.hornbill, false,"elDqlbBqur8");
        Animal b9 = new Animal(0, "Kingfisher", R.raw.kingfisher, R.drawable.kingfisher, false,"vZCV8Q47TIM");
        Animal b10 = new Animal(0, "Quail", R.raw.quail, R.drawable.quail, false,"Sh4KKV6wg3I");
        Animal b11 = new Animal(0, "Stork", R.raw.stork, R.drawable.stork, false,"lOyPeUJbUXU");
        Animal b12 = new Animal(0, "Toucan", R.raw.toucan, R.drawable.toucan, false,"5ExbebXMw1M");
        Animal b13 = new Animal(0, "Woodpecker", R.raw.woodpecker, R.drawable.woodpecker, false,"RH6FpnY3Sts");
        Animal b14 = new Animal(0, "Cuckoo", R.raw.cuckoo, R.drawable.cuckoo, false,"2C4ocPrWbNw");
        Animal b15 = new Animal(0, "Crow", R.raw.crow, R.drawable.crow, false,"Y1lzgtjXau4");
        Animal b16 = new Animal(0, "Peregrine Falcon", R.raw.p_falcon, R.drawable.p_falcon, false,"-fSdtDbbuYo");


        //Making seaAnimals cardView
        Animal s0 = new Animal(0, "Orca Whale", R.raw.orca, R.drawable.orca, false,"WYjSaU7ueqo");
        Animal s1 = new Animal(0, "Whale", R.raw.whale, R.drawable.whale, false,"Ozi7lcyatt0");
        Animal s2 = new Animal(0, "Crocodile", R.raw.crocodile, R.drawable.crocodile, false,"6w60-QCTKik");
        Animal s3 = new Animal(0, "Sea Otter", R.raw.otter, R.drawable.otter, false,"sMYziAe2LKM");
        Animal s4 = new Animal(0, "Dolphin", R.raw.dolphin, R.drawable.dolphin, false,"oL7ltmSmaTw");
        Animal s5 = new Animal(0, "Catfish", R.raw.catfish, R.drawable.catfish, false,"eaCQ4ZAsAgc");
        Animal s6 = new Animal(0, "Crab", R.raw.crab, R.drawable.crab, false,"6oaEF7Kq_64");
        Animal s7 = new Animal(0, "Lobster", R.raw.lobster, R.drawable.lobster, false,"QjBB46McM4o");
        Animal s8 = new Animal(0, "Loach", R.raw.loach, R.drawable.loach, false,"HC_vceuQg1c");
        Animal s9 = new Animal(0, "Sea Lion", R.raw.s_lion, R.drawable.s_lion, false,"hGIZ6yBRV9A");


        //Making Lizards cardView
        Animal l0 = new Animal(0, "African gecko", R.raw.african_gecko, R.drawable.african_gecko, false,"vlqeUM4xoJU");
        Animal l1 = new Animal(0, "Bearded Dragon", R.raw.beard_drag, R.drawable.beard_drag, false,"5LaficwbFO4");
        Animal l2 = new Animal(0, "Crested Gecko", R.raw.crested, R.drawable.crested, false,"ToTqJ3oqQgU");
        Animal l3 = new Animal(0, "Green Iguana", R.raw.green_iguana, R.drawable.green_iguana, false,"4_nNrjmtFKY");
        Animal l4 = new Animal(0, "Panther Chameleon", R.raw.panther_chameleon, R.drawable.panther_chameleon, false,"BFerXeDB53o");


        //Making Snakes CardView
        Animal sn0 = new Animal(0, "Ball python", R.raw.b_python, R.drawable.b_python, false,"nbgr5HfSjlY");
        Animal sn1 = new Animal(0, "Boa Constrictor", R.raw.b_constrictor, R.drawable.b_constrictor, false,"vuzyV8iM_ag");
        Animal sn2 = new Animal(0, "King Cobra", R.raw.cobra, R.drawable.cobra, false,"Thw3_6dFg-A");
        Animal sn3 = new Animal(0, "Rattlesnake", R.raw.rattle, R.drawable.rattle, false,"JE_zSLxD04w");
        Animal sn4 = new Animal(0, "Viperidae", R.raw.viper, R.drawable.viper, false,"yxQdU7iphRg");


        //Making Endangered CardView
        Animal e0 = new Animal(0, "African Elephant", R.raw.afe, R.drawable.afe, false,"3B-PRQmMXkU");
        Animal e1 = new Animal(0, "African Wild Dog", R.raw.awd, R.drawable.awd, false,"CSrqCUdRKjg");
        Animal e2 = new Animal(0, "Black footed ferret", R.raw.bff, R.drawable.bff, false,"7IOXtYmtRG8-A");
        Animal e3 = new Animal(0, "Black Rhino", R.raw.br, R.drawable.br, false,"7Q6dDf_Q6jU");
        Animal e4 = new Animal(0, "Blue Whale", R.raw.bw, R.drawable.bw, false,"bjjkbnZwjA8");
        Animal e5 = new Animal(0, "Bonobo", R.raw.bonobo, R.drawable.bonobo, false,"sN-Hj73ES2U");
        Animal e7 =new Animal(0, "Chinese Alligator", R.raw.ca, R.drawable.ca, false,"AbafKuxcHpQ");
        Animal e8 = new Animal(0, "Galapagos penguin", R.raw.gp, R.drawable.gp, false,"NcXt5SzDJAg");
        Animal e9 = new Animal(0, "Giant Panda", R.raw.panda, R.drawable.panda, false,"dqT-UlYlg1s");
        Animal e10 = new Animal(0, "Hawksbill sea turtle", R.raw.ht, R.drawable.ht, false,"RL_Uh2X_DVs");
        Animal e11 = new Animal(0, "Irrawaddy Dolphin", R.raw.id, R.drawable.id, false,"-9wq12kn3pA");
        Animal e12 = new Animal(0, "Javan Rhinoceros", R.raw.jr, R.drawable.jr, false,"3tTf2q6MYjM");
        Animal e13 = new Animal(0, "Kakapo", R.raw.kakapo, R.drawable.kakapo, false,"hV6-oNRSqCk");
        Animal e14 = new Animal(0, "Leatherback Turtles", R.raw.lt, R.drawable.lt, false,"kxUnmH6CORc");
        Animal e15 = new Animal(0, "Monarch Butterfly", R.raw.mb, R.drawable.mb, false,"RPQimPt2HYc");
        Animal e16 = new Animal(0, "Okapi", R.raw.okapi, R.drawable.okapi, false,"aJv6AUPxS84");
        Animal e17 = new Animal(0, "Pangolin", R.raw.pangolin, R.drawable.pangolin, false,"DqC3ieJJlFM");
        Animal e18 = new Animal(0, "Red Panda", R.raw.redpanda, R.drawable.redpanda, false,"XdM6c4juY1g");
        Animal e19 = new Animal(0, "Saola", R.raw.sa, R.drawable.sa, false,"KnLntvQbf_Y");
        Animal e20 = new Animal(0, "Snow Leopard", R.raw.sl, R.drawable.sl, false,"JTlveCrymV8");
        Animal e21 = new Animal(0, "Sumatran Orangutan", R.raw.so, R.drawable.so, false,"0fts6x_EE_E");
        Animal e22 = new Animal(0, "Tapanuli Orangutan", R.raw.to, R.drawable.to, false,"bWd1A9AWtWo");
        Animal e23 = new Animal(0, "Vaquita", R.raw.vaquita, R.drawable.vaquita, false,"h6km0IKCqiI");
        Animal e24 = new Animal(0, "Whooping crane", R.raw.wc, R.drawable.wc, false,"8unlrpeF_lM");


        //Making Crocodiles CardView
        Animal cr0 = new Animal(0, "American Alligator", R.raw.a_alligator, R.drawable.a_alligator, false,"qQTYf169dO4");
        Animal cr1 = new Animal(0, "False Gharial", R.raw.f_gharial, R.drawable.f_gharial, false,"PaPJZz99FeI");
        Animal cr2 = new Animal(0, "Gharial", R.raw.gharial, R.drawable.gharial, false,"jlXjfgCZEok");
        Animal cr3 = new Animal(0, "Nile Crocodile", R.raw.n_crocodile, R.drawable.n_crocodile, false,"eIAG852keFA");
        Animal cr4 = new Animal(0, "Spectacled Caiman", R.raw.caiman, R.drawable.caiman, false,"7rqnFwbS7NA");


        //making cardView all animals
        all.add(c1);
        all.add(c2);
        all.add(c3);
        all.add(c4);
        all.add(c5);
        all.add(c0);
        all.add(c6);
        all.add(c7);
        all.add(c8);
        all.add(c9);

        all.add(cr1);
        all.add(cr2);
        all.add(cr3);
        all.add(cr4);
        all.add(cr0);

        all.add(h0);
        all.add(h1);
        all.add(h2);
        all.add(h3);
        all.add(h4);
        all.add(h5);
        all.add(h6);
        all.add(h7);
        all.add(h8);
        all.add(h9);

        all.add(b0);
        all.add(b1);
        all.add(b2);
        all.add(b3);
        all.add(b4);
        all.add(b5);
        all.add(b6);
        all.add(b7);
        all.add(b8);
        all.add(b9);
        all.add(b10);
        all.add(b11);
        all.add(b12);
        all.add(b13);
        all.add(b14);
        all.add(b15);
        all.add(b16);

        all.add(s0);
        all.add(s1);
        all.add(s2);
        all.add(s3);
        all.add(s4);
        all.add(s5);
        all.add(s6);
        all.add(s7);
        all.add(s8);
        all.add(s9);

        all.add(l0);
        all.add(l1);
        all.add(l2);
        all.add(l3);
        all.add(l4);

        all.add(sn0);
        all.add(sn1);
        all.add(sn2);
        all.add(sn3);
        all.add(sn4);

        all.add(e0);
        all.add(e1);
        all.add(e2);
        all.add(e3);
        all.add(e4);
        all.add(e5);
        all.add(e7);
        all.add(e8);
        all.add(e9);
        all.add(e10);
        all.add(e11);
        all.add(e12);
        all.add(e13);
        all.add(e14);
        all.add(e15);
        all.add(e16);
        all.add(e17);
        all.add(e18);
        all.add(e19);
        all.add(e20);
        all.add(e21);
        all.add(e22);
        all.add(e23);
        all.add(e24);
        setData();

        //storing the fav button state for all animals if they were favs before as well
        for (Animal a : all) {
            if (share.contains("name0") & Objects.equals(share.getString("name0", ""), a.name)) {
                a.selected = true;
                a.count = 1;
            }
            if (share.contains("name1") && Objects.equals(share.getString("name1", ""), a.name)) {
                a.selected = true;
                a.count = 1;
            }
            if (share.contains("name2") && Objects.equals(share.getString("name2", ""), a.name)) {
                a.selected = true;
                a.count = 1;
            }
            if (share.contains("name3") && Objects.equals(share.getString("name3", ""), a.name)) {
                a.selected = true;
                a.count = 1;
            }
            if (share.contains("name4") && Objects.equals(share.getString("name4", ""), a.name)) {
                a.selected = true;
                a.count = 1;
            }
            if (share.contains("name5") & Objects.equals(share.getString("name5", ""), a.name)) {
                a.selected = true;
                a.count = 1;
            }
            if (share.contains("name6") && Objects.equals(share.getString("name6", ""), a.name)) {
                a.selected = true;
                a.count = 1;
            }
            if (share.contains("name7") && Objects.equals(share.getString("name7", ""), a.name)) {
                a.selected = true;
                a.count = 1;
            }
            if (share.contains("name8") && Objects.equals(share.getString("name8", ""), a.name)) {
                a.selected = true;
                a.count = 1;
            }
            if (share.contains("name9") && Objects.equals(share.getString("name9", ""), a.name)) {
                a.selected = true;
                a.count = 1;
            }
        }

        //simply adding animals to their respective lists

        //endangered
        endangered.add(e0);
        endangered.add(e1);
        endangered.add(e2);
        endangered.add(e3);
        endangered.add(e4);
        endangered.add(e5);
        endangered.add(e7);
        endangered.add(e8);
        endangered.add(e9);
        endangered.add(e10);
        endangered.add(e11);
        endangered.add(e12);
        endangered.add(e13);
        endangered.add(e14);
        endangered.add(e15);
        endangered.add(e16);
        endangered.add(e17);
        endangered.add(e18);
        endangered.add(e19);
        endangered.add(e20);
        endangered.add(e21);
        endangered.add(e22);
        endangered.add(e23);
        endangered.add(e24);

        //sea animals
        sea.add(s0);
        sea.add(s1);
        sea.add(s2);
        sea.add(s3);
        sea.add(s4);
        sea.add(s5);
        sea.add(s6);
        sea.add(s7);
        sea.add(s8);
        sea.add(s9);

        //birds
        birds.add(b0);
        birds.add(b1);
        birds.add(b2);
        birds.add(b3);
        birds.add(b4);
        birds.add(b5);
        birds.add(b6);
        birds.add(b7);
        birds.add(b8);
        birds.add(b9);
        birds.add(b10);
        birds.add(b11);
        birds.add(b12);
        birds.add(b13);
        birds.add(b14);
        birds.add(b15);
        birds.add(b16);

        //horses
        horses.add(h0);
        horses.add(h1);
        horses.add(h2);
        horses.add(h3);
        horses.add(h4);
        horses.add(h5);
        horses.add(h6);
        horses.add(h7);
        horses.add(h8);
        horses.add(h9);

        //cats
        cats.add(c1);
        cats.add(c2);
        cats.add(c3);
        cats.add(c4);
        cats.add(c5);
        cats.add(c6);
        cats.add(c7);
        cats.add(c8);
        cats.add(c9);
        cats.add(c0);

        //lizards
        lizards.add(l0);
        lizards.add(l1);
        lizards.add(l2);
        lizards.add(l3);
        lizards.add(l4);

        //Snakes
        snakes.add(sn0);
        snakes.add(sn1);
        snakes.add(sn2);
        snakes.add(sn3);
        snakes.add(sn4);

        //Crocodiles
        crocodiles.add(cr0);
        crocodiles.add(cr1);
        crocodiles.add(cr2);
        crocodiles.add(cr3);
        crocodiles.add(cr4);

        for(Animal a : crocodiles){
            a.type = "crocodile";
        }
        for(Animal a : endangered){
            a.type = "endangered";
        }
        for(Animal a : snakes){
            a.type = "snake";
        }
        for(Animal a : lizards){
            a.type = "lizard";
        }
        for(Animal a : sea){
            a.type = "sea";
        }
        for(Animal a : birds){
            a.type = "bird";
        }
        for(Animal a : cats){
            a.type = "cat";
        }
        for(Animal a : horses){
            a.type = "horse";
        }

        //setting onScrollListener so that our recycler view has the little arrow icon displayed on bottom when we scroll the list
        cardView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            final int currentFirstVisible = layout.findFirstVisibleItemPosition();

            @Override
            public void onScrolled(@NonNull RecyclerView cardView, int dx, int dy) {
                super.onScrolled(cardView, dx, dy);
                arrow.setBackground(getResources().getDrawable(R.drawable.ic_arrow));
                firstVisibleInListview = currentFirstVisible;
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState) {
                    arrow.setVisibility(View.VISIBLE);
                }
                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    arrow.setVisibility(View.GONE);
                }
            }
        });

        loadAd();
        //Setting default condition
        tabCheck("All Animals");
        allT.setBackground(getResources().getDrawable(R.drawable.restore_text));
        restore("all");

        //setting onClickListener for each textView that is used to display each list
        allT.setOnClickListener(view -> {
            tabCheck("All Animals");
            allT.setBackground(getResources().getDrawable(R.drawable.restore_text));
            restore("all");

        });
        birdT.setOnClickListener(view -> {
            tabCheck("Birds");
            birdT.setBackground(getResources().getDrawable(R.drawable.restore_text));
            restore("bird");

        });
        horseT.setOnClickListener(view -> {
            tabCheck("Horses");
            horseT.setBackground(getResources().getDrawable(R.drawable.restore_text));
            restore("horse");
        });
        seaT.setOnClickListener(view -> {
            tabCheck("Sea Animals");
            seaT.setBackground(getResources().getDrawable(R.drawable.restore_text));
            restore("sea");
        });
        catT.setOnClickListener(view -> {
            tabCheck("Cats");
            catT.setBackground(getResources().getDrawable(R.drawable.restore_text));
            restore("cat");
        });
        endangeredT.setOnClickListener(view -> {
            tabCheck("Endangered");
            endangeredT.setBackground(getResources().getDrawable(R.drawable.restore_text));
            restore("endangered");
        });
        extraT.setOnClickListener(view -> {
            if(checkNet()) {
                    tabCheck("Extra");
                    extraT.setBackground(getResources().getDrawable(R.drawable.restore_text));
                    restore("extra");
            }
            else
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
        });
        favT.setOnClickListener(view -> {
            tabCheck("Favourites");
            favT.setBackground(getResources().getDrawable(R.drawable.restore_text));
            restore("fav");
            Toast.makeText(this, "Favourites selected", Toast.LENGTH_SHORT).show();
        });
        reptiles.setOnClickListener(view -> {
            CustomDialogClass cdd=new CustomDialogClass(this);
            cdd.show();
            Toast.makeText(this, "Select Reptile!", Toast.LENGTH_SHORT).show();
        });

        loadAd();


        //navigation textView opens navigation drawer
        nav.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));
        SharedPreferences.Editor edit = share.edit();
        edit.clear();
        edit.apply();


    }

    //used to keep clicked textView color different from the rest of the textViews
    @SuppressLint("UseCompatLoadingForDrawables")
    public void restore(String tab) {
        if (!Objects.equals(tab, "all"))
            allT.setBackground(getResources().getDrawable(R.drawable.d_rbtn_back_primary_semi));
        if (!Objects.equals(tab, "horse"))
            horseT.setBackground(getResources().getDrawable(R.drawable.d_rbtn_back_primary_semi));
        if (!Objects.equals(tab, "cat"))
            catT.setBackground(getResources().getDrawable(R.drawable.d_rbtn_back_primary_semi));
        if (!Objects.equals(tab, "sea"))
            seaT.setBackground(getResources().getDrawable(R.drawable.d_rbtn_back_primary_semi));
        if (!Objects.equals(tab, "fav"))
            favT.setBackground(getResources().getDrawable(R.drawable.d_rbtn_back_primary_semi));
        if (!Objects.equals(tab, "bird"))
            birdT.setBackground(getResources().getDrawable(R.drawable.d_rbtn_back_primary_semi));
        if (!Objects.equals(tab, "reptile"))
            reptiles.setBackground(getResources().getDrawable(R.drawable.d_rbtn_back_primary_semi));
        if (!Objects.equals(tab, "endangered"))
            endangeredT.setBackground(getResources().getDrawable(R.drawable.d_rbtn_back_primary_semi));
        if (!Objects.equals(tab, "extra"))
            extraT.setBackground(getResources().getDrawable(R.drawable.d_rbtn_back_primary_semi));
    }


    //called when clicking respective textView, this displays the recycler view of that list
    public void tabCheck(String tab) {

        switch (tab) {
            case "Horses":
                if(count%3==0) {
                    showInterstitial();
                    count++;
                }
                if(MainActivity.getInstance().adStatus())
                    MainActivity.getInstance().loadAd();

                cardView.setLayoutManager(layout);
                ad = new cardViewAdapter(this, horses);
                cardView.setAdapter(ad);
                break;
            case "Extra":
                cardView.setLayoutManager(layout);
                ad = new cardViewAdapter(this, extras);
                cardView.setAdapter(ad);
                break;

            case "Cats":
                if(count%3==0) {
                    showInterstitial();
                    count++;
                }
                if(MainActivity.getInstance().adStatus())
                    MainActivity.getInstance().loadAd();
                cardView.setLayoutManager(layout);
                ad = new cardViewAdapter(this, cats);
                cardView.setAdapter(ad);
                break;
            case "Birds":
                if(count%3==1) {
                    showInterstitial();
                    count++;
                }
                if(MainActivity.getInstance().adStatus())
                    MainActivity.getInstance().loadAd();
                cardView.setLayoutManager(layout);
                ad = new cardViewAdapter(this, birds);
                cardView.setAdapter(ad);
                break;
            case "Sea Animals":
                if(count%3==1) {
                    showInterstitial();
                    count++;
                }
                if(MainActivity.getInstance().adStatus())
                    MainActivity.getInstance().loadAd();
                cardView.setLayoutManager(layout);
                ad = new cardViewAdapter(this, sea);
                cardView.setAdapter(ad);
                break;
            case "All Animals":
                cardView.setLayoutManager(layout);
                ad = new cardViewAdapter(this, all);
                cardView.setAdapter(ad);
                break;
            case "Favourites":
                cardView.setLayoutManager(layout);
                ad = new cardViewAdapter(this, favs);
                cardView.setAdapter(ad);
                break;
            case "Lizards":
                if(count%3==1) {
                    showInterstitial();
                    count++;
                }
                if(MainActivity.getInstance().adStatus())
                    MainActivity.getInstance().loadAd();
                cardView.setLayoutManager(layout);
                ad = new cardViewAdapter(this, lizards);
                cardView.setAdapter(ad);
                break;
            case "Snakes":
                if(count%3==1) {
                    showInterstitial();
                    count++;
                }
                if(MainActivity.getInstance().adStatus())
                    MainActivity.getInstance().loadAd();
                cardView.setLayoutManager(layout);
                ad = new cardViewAdapter(this, snakes);
                cardView.setAdapter(ad);
                break;
            case "Crocodiles":
                if(count%3==0) {
                    showInterstitial();
                    count++;
                }
                if(MainActivity.getInstance().adStatus())
                    MainActivity.getInstance().loadAd();
                cardView.setLayoutManager(layout);
                ad = new cardViewAdapter(this, crocodiles);
                cardView.setAdapter(ad);
                break;
            case "Endangered":
                if(count%3==2) {
                    showInterstitial();
                    count++;
                }
                if(MainActivity.getInstance().adStatus())
                    MainActivity.getInstance().loadAd();
                cardView.setLayoutManager(layout);
                ad = new cardViewAdapter(this, endangered);
                cardView.setAdapter(ad);
                break;
        }
    }


    //simply pronounces the animal's name
    public void textToSpeech(String name) {
        repeatTTS.speak(name, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int i) {
        //necessary Override
    }


    //this function checks for internet connectivity, called when opening fragments with webViews
    public boolean checkNet() {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
        //necessary Override
    }



    //setting onClickLIsteners for every nav drawer item
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.remove) {
            //clears all favs and resets all animals to initial state
            Toast.makeText(this, "Remove selected", Toast.LENGTH_SHORT).show();
            favs.clear();
            for (Animal a : all) {
                a.selected = false;
                a.count = 0;
            }
            //displaying default list
            tabCheck("All Animals");
            allT.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.restore_text, getTheme()));
            restore("all");
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }



        if (id == R.id.privacy) {
            //displays navFragment with webView
            if(checkNet()) {
                navFragment fragment = new navFragment("Privacy Policy", "privacy");
                FragmentTransaction transaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.setReorderingAllowed(true);
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.exit_to_left, R.anim.exit_to_right);
                transaction.replace(R.id.activity_main, fragment).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            else
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.home) {
            //displays startActivity
            finish();
        }

        if (id == R.id.contact) {
            if(checkNet()) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","it-factori@outlook.com", null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Contact");
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            else
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.about) {
            //displays navFragment with webView
            if(checkNet()) {
                navFragment fragment = new navFragment("About Us", "about");
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.addToBackStack(null);
                transaction.setReorderingAllowed(true);
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.exit_to_left, R.anim.exit_to_right);
                transaction.replace(R.id.activity_main, fragment).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            else
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.visit) {
            //Opens Google Maps location to NUST
            if(checkNet()) {
                Uri address = Uri.parse("geo:o,0?q=" + "NUST");
                Intent intent = new Intent(Intent.ACTION_VIEW, address);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            else
                Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(id == R.id.share){
            //allows user to share app
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Animal Ringtone");
            String shareMessage= "\nCheck this out!\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" +  "com.affi.animalringtone&pli=1"+"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    public boolean  adStatus(){
        return mInterstitialAd == null;
    }
    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        MainActivity.this.mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        MainActivity.this.mInterstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        MainActivity.this.mInterstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }
    public void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null)
            mInterstitialAd.show(this);

    }
    private  void setData(){
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    extras.clear();
                    for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                        Animal a = dataSnapshot.getValue(Animal.class);
                        extras.add(a);
                    }
                    ad.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }
}

