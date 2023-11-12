package com.affi;


import android.app.Activity;
import android.app.Dialog;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.affi.animalringtone.Animal;
import com.affi.animalringtone.R;
import com.affi.animalringtone.soundFragment;

public class CustomDialog extends Dialog {

    public soundFragment check;
    public Dialog d;
    public Button yes, no;
    Activity a;
    Animal animal;
    Uri u;

    public CustomDialog(Activity a, soundFragment check, Uri u,Animal animal) {
        super(a);
        this.a = a;
        this.check = check;
        this.u = u;
        this.animal = animal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog2);
        yes = findViewById(R.id.yes);
        no =  findViewById(R.id.no);

        yes.setOnClickListener(view -> {
            Toast.makeText(a, "Ringtone  sound set!", Toast.LENGTH_SHORT).show();
            if(animal.getType() == "sea" || animal.getType() == "snake" || animal.getType() == "crocodile" || animal.getType() == "horse")
                check.getRing().setBackground(a.getDrawable(R.drawable.ic_ring));
            else
                check.getRing().setBackground(a.getDrawable(R.drawable.ic_ring_press));
            RingtoneManager.setActualDefaultRingtoneUri(a.getApplicationContext(), RingtoneManager.TYPE_RINGTONE, u);
            dismiss();
        });
        no.setOnClickListener(view -> {
            dismiss();
        });
    }

}