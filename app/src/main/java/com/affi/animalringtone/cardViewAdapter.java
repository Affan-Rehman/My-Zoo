package com.affi.animalringtone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


//This class is our main class that displays the animals in a list
public class cardViewAdapter extends RecyclerView.Adapter<cardViewAdapter.ViewHolder> {
    private static List<Animal> list;
    private final LayoutInflater inflater;
    public MediaPlayer mediaPlayer;
    boolean frag;
    StorageReference sr;
    Context context;
    SharedPreferences.Editor edit;

    cardViewAdapter(Context context, List<Animal> list) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        cardViewAdapter.list = list;
        mediaPlayer = new MediaPlayer();

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.card_view, viewGroup, false);
        edit = MainActivity.getInstance().share.edit();
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        if(MainActivity.getInstance().adStatus())
            MainActivity.getInstance().loadAd();
        Animal object = list.get(position);
        assert object != null;
        if(!Objects.equals(object.purl, "")){
            final ProgressDialog pd = ProgressDialog.show(context, "Please wait...", "Fetching Data from Server", false, true);
            sr= FirebaseStorage.getInstance().getReference(object.purl + ".jpg");
            try {
                File localFile = File.createTempFile("tempfile",".jpg");
                sr.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                   viewHolder.imageView.setImageBitmap(bitmap);
                   pd.dismiss();
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            //setting imageView with animal's image
            viewHolder.imageView.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), object.image, context.getTheme()));
        }

        //Sets the text in animal's card using name of animal
        viewHolder.text.setText(object.name);

        //This block of code tells whether the fav button is selected for each animal or not based on Boolean selected attribute
        if(object.selected)
            viewHolder.fav.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_fav, context.getTheme()));
        else
            viewHolder.fav.setBackground(ResourcesCompat.getDrawable(context.getResources(),R.drawable.ic_remove,context.getTheme()));
                                                                                                                                                                                ///////////////////


        //setting onClick listeners for all imageButtons


        viewHolder.imageView.setOnClickListener(view -> {
            //Clicking on image will open soundFragment
            if(!Objects.equals(object.purl, ""))
                Toast.makeText(context, "Please wait", Toast.LENGTH_SHORT).show();
            soundFragment fragment = new soundFragment(object,context,this);
            FragmentTransaction transaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            frag= true;
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.exit_to_left,R.anim.exit_to_right);
            transaction.replace(R.id.activity_main, fragment).commit();
        });

        viewHolder.text.setOnClickListener(view -> {
            //Clicking on text will open soundFragment
            if(!Objects.equals(object.purl, ""))
                Toast.makeText(context, "Please wait", Toast.LENGTH_SHORT).show();
            soundFragment fragment = new soundFragment(object,context,this);
            FragmentTransaction transaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            frag= true;
            transaction.setReorderingAllowed(true);
            transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.exit_to_left,R.anim.exit_to_right);
            transaction.replace(R.id.activity_main, fragment).commit();
        });


        viewHolder.dialog.setOnClickListener(view -> {
            //Clicking on phone button will also open soundFragment
            if(!Objects.equals(object.purl, ""))
                Toast.makeText(context, "Please wait", Toast.LENGTH_SHORT).show();
            soundFragment fragment = new soundFragment(object,context,this);
            FragmentTransaction transaction = MainActivity.getInstance().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.setReorderingAllowed(true);
            frag= true;
            transaction.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left,R.anim.exit_to_left,R.anim.exit_to_right);
            transaction.replace(R.id.activity_main, fragment).commit();
        });


        viewHolder.fav.setOnClickListener(view -> {
            if(Objects.equals(object.purl, "")) {
                //Very imp
                //Sets animal in favs list

                //This if-else checks whether to remove or add animal based on int count attribute
                //We also keep a check of only 5 animals in favs

                //this adds
                if (object.count % 2 == 0) {
                    if (MainActivity.getInstance().favs.size() < 10) {
                        MainActivity.getInstance().favs.add(object);
                        Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show();
                        view.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_fav, context.getTheme()));
                        object.count++;
                        object.selected = true;
                    } else
                        Toast.makeText(context, "Fav length reached", Toast.LENGTH_SHORT).show();
                }
                //this removes
                else {
                    for (int i = 0; i < MainActivity.getInstance().favs.size(); i++) {
                        if (MainActivity.getInstance().favs.get(i).name.equals(object.name)) {
                            MainActivity.getInstance().favs.remove(i);
                            Toast.makeText(context, "favourite removed", Toast.LENGTH_SHORT).show();
                            view.setBackground(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_remove, context.getTheme()));
                            object.count++;
                            object.setSelected(false);
                            for(Animal a : MainActivity.getInstance().all){
                                if(Objects.equals(a.name, object.name)) {
                                      a.selected = false;
                                      a.count = 0;
                                }
                            }
                        }
                    }
                }
//                notifyDataSetChanged();
            }
            else
                Toast.makeText(context, "Server Data can not be made fav!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageButton dialog,fav;
        public CircleImageView imageView;
        TextView text;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            imageView = view.findViewById(R.id.imageView);
            text = view.findViewById(R.id.text);
            dialog = view.findViewById(R.id.dialog);
            fav = view.findViewById(R.id.fav);

        }
    }
}
