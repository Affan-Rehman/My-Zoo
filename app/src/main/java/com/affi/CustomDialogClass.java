package com.affi;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;

import com.affi.animalringtone.MainActivity;
import com.affi.animalringtone.R;

public class CustomDialogClass extends Dialog {

    public Activity c;
    public Dialog d;
    public Button snakes, lizards,crocodiles;

    public CustomDialogClass(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        snakes = findViewById(R.id.snakes);
        lizards =  findViewById(R.id.lizards);
        crocodiles = findViewById(R.id.croc);

        snakes.setOnClickListener(view -> {
           MainActivity.getInstance().tabCheck("Snakes");
            MainActivity.getInstance().reptiles.setBackground(MainActivity.getInstance().getResources().getDrawable(R.drawable.restore_text));
            MainActivity.getInstance().restore("reptile");
           dismiss();
        });
        crocodiles.setOnClickListener(view -> {
            MainActivity.getInstance().tabCheck("Crocodiles");
            MainActivity.getInstance().reptiles.setBackground(MainActivity.getInstance().getResources().getDrawable(R.drawable.restore_text));
            MainActivity.getInstance().restore("reptile");
            dismiss();
        });
        lizards.setOnClickListener(view -> {
            MainActivity.getInstance().tabCheck("Lizards");
            MainActivity.getInstance().reptiles.setBackground(MainActivity.getInstance().getResources().getDrawable(R.drawable.restore_text));
            MainActivity.getInstance().restore("reptile");
            dismiss();
        });

    }

}
