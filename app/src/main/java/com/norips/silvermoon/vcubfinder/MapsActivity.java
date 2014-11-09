package com.norips.silvermoon.vcubfinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.norips.silvermoon.vcubfinder.KMLStation.ExtendedData;
import com.norips.silvermoon.vcubfinder.KMLStation.Folder;
import com.norips.silvermoon.vcubfinder.KMLStation.Placemark;
import com.norips.silvermoon.vcubfinder.KMLStation.SchemaData;
import com.norips.silvermoon.vcubfinder.WFS.RealTimePlace;
import com.norips.silvermoon.vcubfinder.WFS.featureMember;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



public class MapsActivity extends FragmentActivity{
    CameraPosition cameraPosition;
    private LocationManager locationManager;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng BORDEAUX = new LatLng(44.8376455, -0.5790386999999555);
    private MyLocationListener mylistener;
    private ArrayList<HmMarker> aNbPlaceList = new ArrayList<HmMarker>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        mylistener = new MyLocationListener();
        if (location != null) {
            mylistener.onLocationChanged(location);
        } else {
            // leads to the settings because there is no last known location
            location = new Location(locationManager.NETWORK_PROVIDER);
            location.setLatitude(BORDEAUX.latitude);
            location.setLongitude(BORDEAUX.longitude);
            mylistener.onLocationChanged(location);
        }
        // location updates: at least 1 meter and 200millsecs change
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Log.e("SIZE",String.valueOf(aNbPlaceList.size()));
                for(int i=0;i<aNbPlaceList.size();i++){
                    aNbPlaceList.get(i).getMarker().remove();
                }
                setUpMap();
                return true;
            case R.id.action_search:
                Log.e("SIZE2",String.valueOf(aNbPlaceList.size()));
                final AutoCompleteTextView search = (AutoCompleteTextView) findViewById(R.id.editbox_find);
                String [] Station= new String[aNbPlaceList.size()] ;
                // Get the string array
                for(int i=0;i<aNbPlaceList.size();i++){
                    Station[i] = aNbPlaceList.get(i).getNom();
                }
// Create the adapter and set it to the AutoCompleteTextView
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Station);
                search.setAdapter(adapter);
                search.setVisibility(View.VISIBLE);
                search.bringToFront();
                search.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        for(int i=0;i<aNbPlaceList.size();i++){
                            if(search.getText().toString().equals(aNbPlaceList.get(i).getNom().toString())){
                                cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(aNbPlaceList.get(i).getLat(),aNbPlaceList.get(i).getLng()))      // Sets the center of the map to Mountain View
                                        .zoom(15)                   // Sets the zoom
                                        .build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                aNbPlaceList.get(i).getMarker().showInfoWindow();
                                hideKeyboard(MapsActivity.this);
                                search.setText("");
                                search.setVisibility(View.INVISIBLE);
                            }

                        }

                    }
                });
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
    private class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
        cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(),location.getLongitude()))      // Sets the center of the map to Mountain View
                .zoom(15)                   // Sets the zoom
                .build();                   // Creates a CameraPosition from the builder
        Toast.makeText(MapsActivity.this, "Location changed!",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
       //
    }

    @Override
    public void onProviderEnabled(String provider) {
       //

    }

    @Override
    public void onProviderDisabled(String provider) {
    //
    }
}


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();

            mMap.setMyLocationEnabled(true);
            CameraPosition cameraPositionInit = new CameraPosition.Builder()
                    .target(BORDEAUX)      // Sets the center of the map to Mountain View
                    .zoom(11)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap()  {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
       // new setMarker().execute("http://data.lacub.fr/files.php?gid=43&format=6");
        new setPlace().execute("http://data.lacub.fr/wfs?key=IL6PD1P6U0&SERVICE=WFS&VERSION=1.0.0&REQUEST=GetFeature&TYPENAME=CI_VCUB_P&SRSNAME=EPSG:3945");
    }

    private class setPlace extends AsyncTask<String, Void, ArrayList<HmMarker>   > {
        protected ArrayList<HmMarker> doInBackground(String... URL) {
            //android.os.Debug.waitForDebugger();
            try {
                ///////////////////////////////////////////////////////
                ////////Nombre de place
                ///////////////////////////////////////////////////////
                if (!isConnected(getApplicationContext())) {
                    return null;
                }

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(URL[0]);
                HttpParams httpParameters = httpGet.getParams();
                // Set the timeout in milliseconds until a connection is established.
                int timeoutConnection = 7500;
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                // Set the default socket timeout (SO_TIMEOUT)
                // in milliseconds which is the timeout for waiting for data.
                int timeoutSocket = 7500;
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                httpGet.setHeader("User-Agent",
                        "curl/7.35.0");
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                InputStream is = httpEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                Serializer serializer = new Persister();
                RealTimePlace wfs = serializer.read(RealTimePlace.class, reader);
                //////////////////////////////////////////////////
                ///////Nom et coordonnée
                /////////////////////////////////////////////////
                int pos=0;
                List coord = new ArrayList();
                List namepoint = new ArrayList();
                List gid = new ArrayList();
                BufferedReader readerLOC = null;
                try {
                    readerLOC = new BufferedReader(
                            new InputStreamReader(getAssets().open("doc.kml"), "UTF-8"));
                } catch (IOException e) {
                    Log.e("Buff", e.toString());
                }
                try {
                    if (reader == null){
                        System.out.println("NULL");
                    }
                    Serializer serializerLOC = new Persister();
                    Folder test = serializerLOC.read(Folder.class, readerLOC);
                    Iterator iter = test.getPlacemark().iterator();
                    pos = 0;
                    while(iter.hasNext()) {
                        Placemark station = (Placemark) iter.next();
                        gid.add(pos, (String) station.getExtendedData().getSchemaDataList().getSimpledata().get("GID"));
                        coord.add(pos, (String) station.getPoint().getCoordinates());
                        namepoint.add(pos, (String) ((SchemaData) ((ExtendedData) station.getExtendedData()).getSchemaDataList()).getSimpledata().get("NOM"));
                        pos++;
                    }
                    }catch(IOException e){
                        e.printStackTrace();
                    }



                Iterator iter = wfs.getFeaturemember().iterator();
                pos=0;
                aNbPlaceList.clear();
                while(iter.hasNext()){
                    featureMember nbPlace =(featureMember) iter.next();
                    HmMarker HM = new HmMarker();

                    if(!nbPlace.getCi_vcup_p().getETAT().equals("DECONNECTEE")) {

                        HM.setGID(nbPlace.getCi_vcup_p().getGID());
                        HM.setNom(nbPlace.getCi_vcup_p().getNOM());
                        HM.setNbPlaces(nbPlace.getCi_vcup_p().getNBPLACES());

                        HM.setNbVelo(nbPlace.getCi_vcup_p().getNBVELOS());
                        String[] latlng = coord.get(namepoint.indexOf(HM.getNom())).toString().split(",");
                        HM.setLng(Double.parseDouble(latlng[0]));
                        HM.setLat(Double.parseDouble(latlng[1]));


                        aNbPlaceList.add(pos, HM);
                    } else {
                        pos--;
                    }

                    pos++;
                }



                return aNbPlaceList;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(ArrayList<HmMarker> arr) {
            if(arr!=null) {
                int size = arr.size();
                for (int i = 0; i < size; i++) {
                    String NBPLACE = "Nombre de place : " + arr.get(i).getNbPlaces();
                    String NBVELO = "Nombre de vélo : " + arr.get(i).getNbVelo();
                    arr.get(i).setMarker(mMap.addMarker((new MarkerOptions().position(new LatLng(arr.get(i).getLat(), arr.get(i).getLng())).title(arr.get(i).getNom()).snippet(NBPLACE + System.getProperty("line.separator") + NBVELO))));
                }
            }else{
                //TODO fonction quand il y a pas internet
                Toast.makeText(MapsActivity.this,"Pas de connexion",Toast.LENGTH_SHORT).show();
            }
        }


        }
    public boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


}
