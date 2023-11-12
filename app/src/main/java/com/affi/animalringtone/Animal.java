package com.affi.animalringtone;


import androidx.annotation.Keep;

//Each list displays an animal object
@Keep
public class Animal {
    boolean selected = false;       //this is condition for fav button to be selected or not
    int sound;                         //R.raw.mp3Id
    String purl="";
    String soundUrl="";
    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    int count = 0;                          //this is condition for whether fav button removes or adds, both count and selected go hand in hand
    int image;                          //R.drawable.imageId
    String name;                    //Simply the Animal's name
    String yt = "";
    Animal(int count,String name,int s, int image,boolean selected,String yt){
        this.name = name;
        this.sound = s;
        this. image = image;
        this.count = count;
        this.selected = selected;
        this.yt = yt;
    }
    Animal(){

    }

    public String getPurl() {
        return purl;
    }

    public void setPurl(String purl) {
        this.purl = purl;
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getSound() {
        return sound;
    }

    public void setSound(int sound) {
        this.sound = sound;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYt() {
        return yt;
    }

    public void setYt(String yt) {
        this.yt = yt;
    }

    Animal(String yt, int count, boolean selected){
            this.yt = yt;
            this.count = count;
            this.selected = selected;
    }
}
