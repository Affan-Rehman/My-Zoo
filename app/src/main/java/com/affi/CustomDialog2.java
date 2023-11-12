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

public class CustomDialog2 extends Dialog {

    public soundFragment check;
    public Dialog d;
    public Button yes, no;
    Activity a;
    Animal animal;
    Uri u;

    public CustomDialog2(Activity a, soundFragment check, Uri u,Animal animal) {
        super(a);
        this.a = a;
        this.animal = animal;
        this.check = check;
        this.u = u;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog2);
        yes = findViewById(R.id.yes);
        no =  findViewById(R.id.no);

        yes.setOnClickListener(view -> {
            check.setChoice(true);
                Toast.makeText(a, "Notification  sound set!", Toast.LENGTH_SHORT).show();
                if(animal.getType() == "sea" || animal.getType() == "snake" || animal.getType() == "crocodile" || animal.getType() == "horse")
                check.getNotification().setBackground(a.getDrawable(R.drawable.svg_notification));
                else
                    check.getNotification().setBackground(a.getDrawable(R.drawable.svg_notification_press));
                RingtoneManager.setActualDefaultRingtoneUri(a.getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION, u);

            dismiss();
        });
        no.setOnClickListener(view -> {
            dismiss();
        });
    }

}