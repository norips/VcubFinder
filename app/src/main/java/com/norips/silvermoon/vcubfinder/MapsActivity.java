package com.norips.silvermoon.vcubfinder;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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


public class MapsActivity extends FragmentActivity {
    //API KEY pour la cub
    String APICUB = "APIKEY";
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    CameraPosition cameraPosition;
    private LocationManager locationManager;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LatLng BORDEAUX = new LatLng(44.8376455, -0.5790386999999555);
    private MyLocationListener mylistener;
    private ArrayList<HmMarker> aNbPlaceList = new ArrayList<HmMarker>();
    private boolean PermissionOK = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
        } else {
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            try {
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
            } catch (SecurityException e) {
                Log.e("Security", e.getMessage());
            }
            PermissionOK = true;
        }
        // location updates: at least 1 meter and 200millsecs change
        super.onCreate(savedInstanceState);
        if(PermissionOK) {
            setContentView(R.layout.activity_maps);
            setUpMapIfNeeded();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    try {
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
                    } catch (SecurityException e) {
                        Log.e("Security", e.getMessage());
                    }
                    setContentView(R.layout.activity_maps);
                    setUpMapIfNeeded();


                } else {
                    PermissionOK = false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
                final AutoCompleteTextView search2 = (AutoCompleteTextView) findViewById(R.id.editbox_find);
                search2.setVisibility(View.INVISIBLE);
                Log.i("Vcub", "Refresh");
                for (int i = 0; i < aNbPlaceList.size(); i++) {
                    aNbPlaceList.get(i).getMarker().remove();
                }
                Button btVelo = (Button) findViewById(R.id.btVelo);
                Button btPlace = (Button) findViewById(R.id.btPlace);
                btPlace.setBackgroundResource(android.R.drawable.btn_default);
                btVelo.setBackgroundResource(android.R.drawable.btn_default);

                setUpMap();
                return true;
            case R.id.action_search:
                Log.i("Vcub", "Search");
                final AutoCompleteTextView search = (AutoCompleteTextView) findViewById(R.id.editbox_find);
                search.setVisibility(View.INVISIBLE);
                String[] Station = new String[aNbPlaceList.size()];
                // Get the string array
                for (int i = 0; i < aNbPlaceList.size(); i++) {
                    Station[i] = aNbPlaceList.get(i).getNom();
                }
// Create the adapter and set it to the AutoCompleteTextView
                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Station);
                search.setAdapter(adapter);
                search.setVisibility(View.VISIBLE);
                search.bringToFront();
                search.requestFocus();
                search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            search.setVisibility(View.INVISIBLE);
                            search.clearFocus();
                            Log.i("Vcub", "FOCUS");
                        }
                    }
                });
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT);
                search.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        for (int i = 0; i < aNbPlaceList.size(); i++) {
                            if (search.getText().toString().equals(aNbPlaceList.get(i).getNom().toString())) {
                                cameraPosition = new CameraPosition.Builder()
                                        .target(new LatLng(aNbPlaceList.get(i).getLat(), aNbPlaceList.get(i).getLng()))      // Sets the center of the map to Mountain View
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
            case R.id.action_settings:
                Intent intent = new Intent(this, aPropos.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to Mountain View
                    .zoom(15)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
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
        if(PermissionOK) {
            setUpMapIfNeeded();
        }
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

            //Set click listener on info windows
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker mark) {
                    LatLng location = mark.getPosition();
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr=" + String.valueOf(location.latitude) + "," + String.valueOf(location.longitude)));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            });
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {

                    LinearLayout info = new LinearLayout(getApplicationContext());
                    info.setOrientation(LinearLayout.VERTICAL);

                    TextView title = new TextView(getApplicationContext());
                    title.setTextColor(Color.BLACK);
                    title.setGravity(Gravity.CENTER);
                    title.setTypeface(null, Typeface.BOLD);
                    title.setText(marker.getTitle());

                    TextView snippet = new TextView(getApplicationContext());
                    snippet.setTextColor(Color.GRAY);
                    snippet.setText(marker.getSnippet());

                    info.addView(title);
                    info.addView(snippet);

                    return info;
                }
            });
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
    private void setUpMap() {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        // new setMarker().execute("http://data.lacub.fr/files.php?gid=43&format=6");
        new setPlace().execute("http://data.lacub.fr/wfs?key=" + APICUB + "&SERVICE=WFS&VERSION=1.1.0&REQUEST=GetFeature&TYPENAME=CI_VCUB_P&SRSNAME=EPSG:4326", "");
    }

    private void setUpMap(String Option) {
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        // new setMarker().execute("http://data.lacub.fr/files.php?gid=43&format=6");
        new setPlace().execute("http://data.lacub.fr/wfs?key=" + APICUB + "&SERVICE=WFS&VERSION=1.1.0&REQUEST=GetFeature&TYPENAME=CI_VCUB_P&SRSNAME=EPSG:4326", Option);
    }

    private class setPlace extends AsyncTask<String, Void, ArrayList<HmMarker>> {


        private final ProgressDialog dialog = new ProgressDialog(MapsActivity.this);

        protected void onPreExecute() {
            this.dialog.setMessage("Mise à jour des stations...");
            this.dialog.show();
        }

        protected ArrayList<HmMarker> doInBackground(String... URL) {
            return retrieveMarker(URL[0], URL[1]);
        }

        protected void onPostExecute(ArrayList<HmMarker> arr) {
            if (arr != null) {
                int size = arr.size();
                for (int i = 0; i < size; i++) {
                    String NBPLACE = "Nombre de place : " + arr.get(i).getNbPlaces();
                    String NBVELO = "Nombre de vélo : " + arr.get(i).getNbVelo();
                    arr.get(i).setMarker(mMap.addMarker((new MarkerOptions()
                            .position(new LatLng(arr.get(i).getLat(), arr.get(i).getLng()))
                            .title(arr.get(i).getNom()).snippet(NBPLACE + System.getProperty("line.separator") + NBVELO))
                            .icon(arr.get(i).getIcon())));
                }
            } else {
                //TODO fonction quand il y a pas internet
                Toast.makeText(MapsActivity.this, "Pas de connexion", Toast.LENGTH_SHORT).show();
                Toast.makeText(MapsActivity.this, "Impossibilité de récuperer les stations Vcub", Toast.LENGTH_LONG).show();
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
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

    public ArrayList<HmMarker> retrieveMarker(String URL, String Option) {
        //android.os.Debug.waitForDebugger();
        try {
            ///////////////////////////////////////////////////////
            ////////Nombre de place
            ///////////////////////////////////////////////////////
            if (!isConnected(getApplicationContext())) {
                return null;
            }

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(URL);
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
            //Get the xml in my class Realtime
            RealTimePlace wfs = serializer.read(RealTimePlace.class, reader);

            Iterator iter = wfs.getFeaturemember().iterator();
            int pos = 0;
            aNbPlaceList.clear();
            while (iter.hasNext()) {
                featureMember Station = (featureMember) iter.next();
                HmMarker HM = new HmMarker();

                //if(!Station.getCi_vcup_p().getETAT().equals("DECONNECTEE")) {

                HM.setGID(Station.getCi_vcup_p().getGID());
                HM.setNom(Station.getCi_vcup_p().getNOM());
                HM.setNbPlaces(Station.getCi_vcup_p().getNBPLACES());
                HM.setNbVelo(Station.getCi_vcup_p().getNBVELOS());
                Log.d("WFS", HM.getNom());
                String[] latlng = Station.getCi_vcup_p().getGeometry().getPoint().getPos().split("\\s+");
                Log.d("WFS", latlng[0]);
                Log.d("WFS", latlng[1]);

                HM.setLat(Double.parseDouble(latlng[0]));
                HM.setLng(Double.parseDouble(latlng[1]));
                if (Station.getCi_vcup_p().getETAT().equals("DECONNECTEE") || (Station.getCi_vcup_p().getNBPLACES().equals("0") && Station.getCi_vcup_p().getNBVELOS().equals("0"))) {
                    HM.setEtat("DECONNECTEE");
                    HM.setIcon("HS", getApplicationContext());

                } else if (Option.equals("Velo")) {
                    HM.setIcon(HM.getNbVelo().toString(), getApplicationContext());

                } else if (Option.equals("Place")) {
                    HM.setIcon(HM.getNbPlaces().toString(), getApplicationContext());

                } else {
                    HM.setIcon("", getApplicationContext());

                }
                aNbPlaceList.add(pos, HM);
                   /* } else {
                        pos--;
                    }*/

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


    public void ButtonVelo(View v) throws IOException {
        Button btVelo = (Button) findViewById(R.id.btVelo);
        Button btPlace = (Button) findViewById(R.id.btPlace);
        btPlace.setBackgroundResource(android.R.drawable.btn_default);
        btVelo.setBackgroundResource(R.drawable.buttontheme_btn_default_normal_holo_light);

        for (int i = 0; i < aNbPlaceList.size(); i++) {
            aNbPlaceList.get(i).getMarker().remove();
            String NBPLACE = "Nombre de place : " + aNbPlaceList.get(i).getNbPlaces();
            String NBVELO = "Nombre de vélo : " + aNbPlaceList.get(i).getNbVelo();
            aNbPlaceList.get(i).setIcon(aNbPlaceList.get(i).getNbVelo().toString(), getApplicationContext());
            aNbPlaceList.get(i).setMarker(mMap.addMarker((new MarkerOptions()
                    .position(new LatLng(aNbPlaceList.get(i).getLat(), aNbPlaceList.get(i).getLng()))
                    .title(aNbPlaceList.get(i).getNom()).snippet(NBPLACE + System.getProperty("line.separator") + NBVELO))
                    .icon(aNbPlaceList.get(i).getIcon())));
        }

        final AutoCompleteTextView search = (AutoCompleteTextView) findViewById(R.id.editbox_find);
        search.setVisibility(View.INVISIBLE);
        Log.i("Vcub", "Velo");
    }

    public void ButtonPlace(View v) throws IOException {
        Button btVelo = (Button) findViewById(R.id.btVelo);
        Button btPlace = (Button) findViewById(R.id.btPlace);
        btPlace.setBackgroundResource(R.drawable.buttontheme_btn_default_normal_holo_light);
        btVelo.setBackgroundResource(android.R.drawable.btn_default);

        for (int i = 0; i < aNbPlaceList.size(); i++) {
            aNbPlaceList.get(i).getMarker().remove();
            String NBPLACE = "Nombre de place : " + aNbPlaceList.get(i).getNbPlaces();
            String NBVELO = "Nombre de vélo : " + aNbPlaceList.get(i).getNbVelo();
            aNbPlaceList.get(i).setIcon(aNbPlaceList.get(i).getNbPlaces().toString(), getApplicationContext());
            aNbPlaceList.get(i).setMarker(mMap.addMarker((new MarkerOptions()
                    .position(new LatLng(aNbPlaceList.get(i).getLat(), aNbPlaceList.get(i).getLng()))
                    .title(aNbPlaceList.get(i).getNom()).snippet(NBPLACE + System.getProperty("line.separator") + NBVELO))
                    .icon(aNbPlaceList.get(i).getIcon())));
        }

        final AutoCompleteTextView search = (AutoCompleteTextView) findViewById(R.id.editbox_find);
        search.setVisibility(View.INVISIBLE);
        Log.i("Vcub", "Place");
    }



}
