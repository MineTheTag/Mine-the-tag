package com.example.omair.minethetag;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.DetectedActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;
import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import android.os.*;

import static android.R.attr.password;
import static com.example.omair.minethetag.LoginActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.omair.minethetag.LoginActivity.TOKEN;
import static com.example.omair.minethetag.LoginActivity.gpassword;
import static com.example.omair.minethetag.LoginActivity.gusername;
import static com.example.omair.minethetag.LoginActivity.latitude;
import static com.example.omair.minethetag.LoginActivity.longitude;
import static com.example.omair.minethetag.LoginActivity.gresponse;
import static com.example.omair.minethetag.R.id.username;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    LocationManager locationManager;
    int pos = 1;
    double radi = 0.00019;
    double posMinaX = latitude;
    double posMinaY = longitude;
    double oldLat, oldLon;
    int TotalMines = 5;
    int inicialMines = 0;
    public static String gtoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        TrackerSettings settings =
                new TrackerSettings()
                        .setUseGPS(true)
                        .setUseNetwork(true)
                        .setUsePassive(true)
                        .setTimeBetweenUpdates(30 * 1000)
                        .setMetersBetweenUpdates(5);

        LocationTracker tracker = new LocationTracker(getApplicationContext(), settings) {

            @Override
            public void onLocationFound(Location location) {
                Toast.makeText(getApplicationContext(), "LocationTracker = " + location.getLatitude(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTimeout() {

            }
        };
        //tracker.startListening();

        if (SmartLocation.with(getApplicationContext()).location().state().locationServicesEnabled())
        {

        }
        else if (SmartLocation.with(getApplicationContext()).location().state().isAnyProviderAvailable())
        {

        }

        SmartLocation.with(getApplicationContext()).location().continuous()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location)
                    {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        oldLat = latitude;
                        oldLon = longitude;
                        Toast.makeText(getApplicationContext(), "Latitude = " + latitude, Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "Longitude = " + longitude, Toast.LENGTH_SHORT).show();
                        //CheckExplosio();
                    }
                });

        final MapView map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(20);
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);

        ////////////////////////////////////
        getMinesUsuari();
        getAltresMinesUsuari();
        ////////////////////////////////////


        ActualizarPos();

        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(getApplicationContext(), map);
        mRotationGestureOverlay.setEnabled(true);
        map.setMultiTouchControls(true);
        map.getOverlays().add(mRotationGestureOverlay);
        map.setBuiltInZoomControls(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.mina);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double posX = 0, posY = 0;
                if (inicialMines <= TotalMines)
                {
                    ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
                    if (pos == 1)
                    {
                        posX = posMinaX + radi;
                        posY = posMinaY;
                        pos = 2;
                    }
                    else if (pos == 2)
                    {
                        posX = posMinaX;
                        posY = posMinaY - radi;
                        pos = 3;
                    }
                    else if (pos == 3)
                    {
                        posX = posMinaX - radi;
                        posY = posMinaY;
                        pos = 4;
                    }
                    else if (pos == 4)
                    {
                        posX = posMinaX;
                        posY = posMinaY + radi;
                        pos = 5;
                    }
                    else if (pos == 5)
                    {
                        posX = posMinaX + radi;
                        posY = posMinaY + radi;
                        pos = 1;
                    }

                    OverlayItem mina = new OverlayItem("New", "Mina", new GeoPoint(posX, posY));
                    Drawable newMarker = getResources().getDrawable(R.drawable.mine28);
                    mina.setMarker(newMarker);
                    overlayItemArray.add(mina);

                    MyOwnItemizedOverlay overlay = new MyOwnItemizedOverlay(getApplicationContext(), overlayItemArray);
                    map.getOverlays().add(overlay);
                    altaMines(posX, posY);
                    map.invalidate();
                    ++inicialMines;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "You cannot insert more mines!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActualizarPos();
                CheckExplosio();
                getMinesUsuari();

                SmartLocation.with(getApplicationContext()).location().continuous()
                        .start(new OnLocationUpdatedListener() {
                            @Override
                            public void onLocationUpdated(Location location)
                            {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                oldLat = latitude;
                                oldLon = longitude;
                                Toast.makeText(getApplicationContext(), "Latitude = " + latitude, Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Longitude = " + longitude, Toast.LENGTH_SHORT).show();
                                //CheckExplosio();
                            }
                        });

                oldLat = latitude;
                oldLon = longitude;
                ha.postDelayed(this, 10000);
            }
        }, 10000);

    }

    void ActualizarPos()
    {
        final MapView map = (MapView) findViewById(R.id.mapview);
        ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
        ArrayList<OverlayItem> overlayItemArray2 = new ArrayList<OverlayItem>();

        OverlayItem linkopingItem = new OverlayItem("Current", "Location", new GeoPoint(latitude, longitude));
        OverlayItem linkopingItemOld = new OverlayItem("Current", "Location", new GeoPoint(oldLat, oldLon));

        Drawable newMarker = this.getResources().getDrawable(R.drawable.icon);
        linkopingItem.setMarker(newMarker);
        overlayItemArray.add(linkopingItem);
        overlayItemArray2.add(linkopingItemOld);

        final ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(this, overlayItemArray, null);
        final ItemizedIconOverlay<OverlayItem> itemizedIconOverlayOld = new ItemizedIconOverlay<OverlayItem>(this, overlayItemArray2, null);
        // Add the overlay to the MapView
        map.getOverlays().remove(itemizedIconOverlayOld);
        map.getOverlays().add(itemizedIconOverlay);
        map.invalidate();
    }

    void CheckExplosio()
    {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "https://minethetag.cf/api/mines/check/explosion";
        Map<String, Double> params = new HashMap<String, Double>();
        params.put("x_pos", latitude);
        params.put("y_pos", longitude);
        JSONObject jsonObj = new JSONObject(params);

        JsonObjectRequest MyStringRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                if (response.toString().contains("Booom"))
                {
                    Toast.makeText(getApplicationContext(), "BOOOOOOM " + response, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "CALM ", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Log.d("CHECK: ", "MISSATGE = " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = gusername + ":" + gpassword;
                String tok = TOKEN + ":NONE";
                String auth = "Basic "
                        + Base64.encodeToString(tok.getBytes(), Base64.NO_WRAP);
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        MyRequestQueue.add(MyStringRequest);
    }

    void getAltresMinesUsuari()
    {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "https://minethetag.cf/api/admin/mines/getdiff";
        Map<String, String> params = new HashMap<String, String>();
        JSONArray jsonObj = null;
        try {
            jsonObj = new JSONArray(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest MyStringRequest = new JsonArrayRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //Toast.makeText(getApplicationContext(), "Response = " + response, Toast.LENGTH_LONG).show();
                for (int i = 0; i < response.length(); i++)
                {
                    JSONArray pos = null;
                    try {
                        pos = (JSONArray) response.get(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Double posX = null;
                    Double posY = null;
                    try {
                        posX = (Double) pos.get(0);
                        posY = (Double) pos.get(1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayList<OverlayItem> overlayItemArray;
                    overlayItemArray = new ArrayList<OverlayItem>();
                    OverlayItem mina = new OverlayItem("New", "Mina", new GeoPoint(posX, posY));
                    Drawable newMarker = getResources().getDrawable(R.drawable.mine28_admin);
                    mina.setMarker(newMarker);
                    overlayItemArray.add(mina);

                    MapView map = (MapView) findViewById(R.id.mapview);
                    MyOwnItemizedOverlay overlay = new MyOwnItemizedOverlay(getApplicationContext(), overlayItemArray);
                    map.getOverlays().add(overlay);
                    map.invalidate();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                //Log.wtf("ERROR mines get: ", error.getMessage().toString());
                Log.d("ALTRES", "MISSATGE = " + error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = gusername + ":" + gpassword;
                String tok = TOKEN + ":NONE";
                String auth = "Basic "
                        + Base64.encodeToString(tok.getBytes(), Base64.NO_WRAP);
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        MyRequestQueue.add(MyStringRequest);
    }

    void getMinesUsuari()
    {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "https://minethetag.cf/api/mines/get";
        Map<String, String> params = new HashMap<String, String>();
        JSONArray jsonObj = null;
        try {
            jsonObj = new JSONArray(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest MyStringRequest = new JsonArrayRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //Toast.makeText(getApplicationContext(), "Response = " + response, Toast.LENGTH_LONG).show();
                for (int i = 0; i < response.length(); i++)
                {
                    JSONArray pos = null;
                    try {
                        pos = (JSONArray) response.get(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Double posX = null;
                    Double posY = null;
                    try {
                        posX = (Double) pos.get(0);
                        posY = (Double) pos.get(1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ArrayList<OverlayItem> overlayItemArray;
                    overlayItemArray = new ArrayList<OverlayItem>();
                    OverlayItem mina = new OverlayItem("New", "Mina", new GeoPoint(posX, posY));
                    Drawable newMarker = getResources().getDrawable(R.drawable.mine28);
                    mina.setMarker(newMarker);
                    overlayItemArray.add(mina);

                    MapView map = (MapView) findViewById(R.id.mapview);
                    MyOwnItemizedOverlay overlay = new MyOwnItemizedOverlay(getApplicationContext(), overlayItemArray);
                    map.getOverlays().add(overlay);
                    map.invalidate();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                //Log.wtf("ERROR mines get: ", error.getMessage().toString());
                //Toast.makeText(getApplicationContext(), "L'usuari no te mines encara", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = gusername + ":" + gpassword;
                String tok = TOKEN + ":NONE";
                String auth = "Basic "
                        + Base64.encodeToString(tok.getBytes(), Base64.NO_WRAP);
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        MyRequestQueue.add(MyStringRequest);
    }

    void authentification()
    {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "https://minethetag.cf/api/token";

        Map<String, String> params = new HashMap<String, String>();
        //params.put("username", username);
        //params.put("password", password);
        JSONObject jsonObj = new JSONObject(params);

        JsonObjectRequest MyStringRequest = new JsonObjectRequest(Request.Method.GET, url, jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Toast.makeText(getApplicationContext(), "Response = " + response, Toast.LENGTH_LONG).show();

                try {
                    gtoken = response.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(
                        "Authorization",
                        String.format("Basic %s", Base64.encodeToString(
                                String.format("%s:%s", gusername, gpassword).getBytes(), Base64.DEFAULT)));
                return params;
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText(getApplicationContext(), "BAD", Toast.LENGTH_SHORT).show();
            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = gusername + ":" + gpassword;
                String tok = TOKEN + ":NONE";
                String auth = "Basic "
                        + Base64.encodeToString(tok.getBytes(), Base64.NO_WRAP);
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        MyRequestQueue.add(MyStringRequest);
    }
    /*
    http://stackoverflow.com/questions/30859044/post-json-object-data-to-get-json-array-response-using-volley-in-android

    Aqui estem treballant amb JSONObjects, pero l'error que dona es que es retorna un JSON array. Per
    tant peta.
    O fem servir la solucio indicada a stack overflow o canviem backend

     */


    void test()
    {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "https://minethetag.cf/test";
        Map<String, String> params = new HashMap<String, String>();
        JSONObject jsonObj = new JSONObject(params);

        StringRequest MyStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                Log.d("Auth test result: ", response);
                //Toast.makeText(getApplicationContext(), "Response = " + response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText(getApplicationContext(), "Token == " + TOKEN, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "Username = " + gusername + " " + "Password = " + gpassword, Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "NO USER", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = gusername + ":" + gpassword;
                String tok = TOKEN + ":NONE";
                String auth = "Basic "
                        + Base64.encodeToString(tok.getBytes(), Base64.NO_WRAP);
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        MyRequestQueue.add(MyStringRequest);
    }

    void altaMines(final double posMinaX, final double posMinaY)
    {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "https://minethetag.cf/api/mines/new";
        Map<String, Double> params = new HashMap<String, Double>();
        params.put("x_pos", posMinaX);
        params.put("y_pos", posMinaY);
        JSONObject jsonObj = new JSONObject(params);

        JsonObjectRequest MyStringRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //Toast.makeText(getApplicationContext(), "Mina donada d'alta ", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                Toast.makeText(getApplicationContext(), "NO MINE", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                String credentials = gusername + ":" + gpassword;
                String tok = TOKEN + ":NONE";
                String auth = "Basic "
                        + Base64.encodeToString(tok.getBytes(), Base64.NO_WRAP);
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", auth);
                return headers;
            }
        };
        MyRequestQueue.add(MyStringRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.insertTag)
        {
            Intent a = new Intent(this, InsertTagActivity.class);
            startActivity(a);
        }
        else if (id == R.id.logout)
        {
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMax(10);
            progressDialog.setMessage("Logging out...");
            progressDialog.show();

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {

                            Intent i = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(i);

                            progressDialog.dismiss();
                        }
                    }, 3000);
        }
        else if (id == R.id.help)
        {
            Intent a = new Intent(this, HelpActivity.class);
            startActivity(a);
        }
        else if (id == R.id.about)
        {
            Intent a = new Intent(this, AboutActivity.class);
            startActivity(a);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
