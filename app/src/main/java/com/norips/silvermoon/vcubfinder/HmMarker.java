package com.norips.silvermoon.vcubfinder;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by silvermoon on 09/11/14.
 */
public class HmMarker {
    protected String GID;
    protected String Nom ;
    protected String NbPlaces;
    protected String NbVelo;
    protected String Etat;

    public HmMarker() {
        Nom = "";
        this.GID = "";
        NbPlaces = "";
        NbVelo = "";
        Etat = "";
    }

    public String getEtat() {
        return Etat;
    }

    public void setEtat(String etat) {
        Etat = etat;
    }

    protected double Lat;
    protected double Lng;

    protected Marker marker;


    protected BitmapDescriptor icon;

    public void setGID(String gid){
        GID=gid;
    }
    public void setNom(String nom){
        Nom=nom;
    }
    public void setNbPlaces(String place){
        NbPlaces=place;
    }
    public void setNbVelo(String velo){
        NbVelo=velo;
    }
    public void setLat(double la){
        Lat=la;
    }
    public void setLng(double ln){
        Lng = ln;
    }
    public BitmapDescriptor getIcon() {
        return icon;
    }
    public void setIcon(String Number,Context myContext) throws IOException {
        //TODO bug dezoom les marker change de place
        AssetManager mngr = myContext.getAssets();
        InputStream bitmap = mngr.open("icon.png");
        Bitmap orig = BitmapFactory.decodeStream(bitmap);
        Bitmap bit = orig.copy(Bitmap.Config.ARGB_8888, true);
        bitmap.close();
        Bitmap bitfinal = Bitmap.createBitmap(63,130, Bitmap.Config.ARGB_8888);
        Canvas canvas1 = new Canvas(bitfinal);

// paint defines the text color,
// stroke width, size
        Paint color = new Paint();
        color.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        color.setTextSize(50);
        if(!Number.equals("") && !Number.equals("HS")) {
            if (Integer.decode(Number) <= 5) {
                color.setColor(Color.RED);
            } else if (Integer.decode(Number) > 5 && Integer.decode(Number) <= 10) {
                color.setColor(Color.rgb(255,153,0));
            } else {
                color.setColor(Color.rgb(51,153,0));
            }
        }

//modify canvas
        canvas1.drawBitmap(bit,0,50,color);
        if(Etat.equals("DECONNECTEE")) {
            color.setColor(Color.BLACK);
            canvas1.drawText("HS", 0, 50, color);
        } else {
            canvas1.drawText(Number, 0, 50, color);
        }
        final float scale = myContext.getResources().getDisplayMetrics().density;
        int width = (int) (63 * (scale/2.5) );
        int height = (int) (130 * (scale/2.5) );
        bitfinal = Bitmap.createScaledBitmap(bitfinal, width, height, true);
        this.icon = BitmapDescriptorFactory.fromBitmap(bitfinal);
    }
    public void setMarker(Marker mark){
        marker = mark;
    }



    public String getGID(){
        return GID;
    }
    public String getNom(){
        return Nom;
    }
    public String getNbPlaces(){
        return NbPlaces;
    }
    public String getNbVelo(){
        return NbVelo;
    }
    public double getLat(){
        return Lat;
    }
    public double getLng(){
        return Lng;
    }
    public Marker getMarker(){
        return marker;
    }
}
