package com.norips.silvermoon.vcubfinder;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by silvermoon on 09/11/14.
 */
public class HmMarker {
    protected String GID;
    protected String Nom ;
    protected String NbPlaces;
    protected String NbVelo;

    protected double Lat;
    protected double Lng;

    protected Marker marker;


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
