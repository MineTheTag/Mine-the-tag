package com.example.omair.minethetag;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
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
import java.util.Map;
import fr.quentinklein.slt.TrackerSettings;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import android.os.*;
import static com.example.omair.minethetag.LoginActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.omair.minethetag.LoginActivity.TOKEN;
import static com.example.omair.minethetag.LoginActivity.gpassword;
import static com.example.omair.minethetag.LoginActivity.gusername;
import static com.example.omair.minethetag.LoginActivity.latitude;
import static com.example.omair.minethetag.LoginActivity.longitude;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int pos = 1;
    double radi = 0.00019;
    double posMinaX = latitude;
    double posMinaY = longitude;
    double oldLat, oldLon;
    int TotalMines = 5;
    int inicialMines = 0;
    public static String gtoken;
    //ArrayList<OverlayItem> osm_items;
    //ArrayList<OverlayItem> osm_mines;
    public ItemizedIconOverlay<OverlayItem> osm_items;
    public MyOwnItemizedOverlay osm_mines_propies;
    public MyOwnItemizedOverlay osm_mines_alienes;
    public MyOwnItemizedOverlay osm_mines_explotades;
    public MyOwnItemizedOverlay osm_tags_propis;
    public MyOwnItemizedOverlay osm_tags_aliens;
    public OverlayItem osm_pos;
    Handler ha_ref;
    Runnable runab_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
                        //Toast.makeText(getApplicationContext(), "Latitude = " + latitude, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(getApplicationContext(), "Longitude = " + longitude, Toast.LENGTH_SHORT).show();
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

        getMinesUsuari();
        getAltresMinesUsuari();
        getTags();

        // OSM //
        //osm_items = new ArrayList<OverlayItem>();
        //osm_mines = new ArrayList<OverlayItem>();
        osm_items = new ItemizedIconOverlay<OverlayItem>(this, new ArrayList<OverlayItem>(), null);

        //osm_items.addItem(osm_pos);
        osm_mines_propies = new MyOwnItemizedOverlay(getApplicationContext(), new ArrayList<OverlayItem>());
        osm_mines_alienes = new MyOwnItemizedOverlay(getApplicationContext(), new ArrayList<OverlayItem>());
        osm_mines_explotades = new MyOwnItemizedOverlay(getApplicationContext(), new ArrayList<OverlayItem>());
        osm_tags_propis = new MyOwnItemizedOverlay(getApplicationContext(), new ArrayList<OverlayItem>());
        osm_tags_aliens = new MyOwnItemizedOverlay(getApplicationContext(), new ArrayList<OverlayItem>());
        map.getOverlays().add(osm_items);
        map.getOverlays().add(osm_mines_propies);
        map.getOverlays().add(osm_mines_alienes);
        map.getOverlays().add(osm_mines_explotades);
        map.getOverlays().add(osm_tags_propis);
        map.getOverlays().add(osm_tags_aliens);

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
                //Intent a = new Intent(MainActivity.this, BlockedActivity.class);
                //startActivity(a);
                map.invalidate();
                ++inicialMines;
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setBackgroundColor(Color.parseColor("#FFFFFF"));
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IMapController mapController = map.getController();
                mapController.setZoom(20);
                GeoPoint startPoint = new GeoPoint(latitude, longitude);
                mapController.setCenter(startPoint);
            }
        });

        final Handler ha = new Handler();
        final Runnable runab = new Runnable() {
            @Override
            public void run() {
                CheckExplosio();
                getMinesUsuari();
                getAltresMinesUsuari();
                getTags();
                ActualizarPos();

                SmartLocation.with(getApplicationContext()).location().continuous()
                        .start(new OnLocationUpdatedListener() {
                            @Override
                            public void onLocationUpdated(Location location)
                            {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                oldLat = latitude;
                                oldLon = longitude;
                                //Toast.makeText(getApplicationContext(), "Latitude = " + latitude, Toast.LENGTH_SHORT).show();
                                //Toast.makeText(getApplicationContext(), "Longitude = " + longitude, Toast.LENGTH_SHORT).show();
                                //CheckExplosio();
                            }
                        });

                oldLat = latitude;
                oldLon = longitude;
                ActualizarPos();
                ha.postDelayed(this, 2000);
            }
        };
        ha.postDelayed(runab, 10000);
        ha_ref = ha;
        runab_ref = runab;

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
                this,  mDrawerLayout, mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();
    }

    void ActualizarPos()
    {
        final MapView map = (MapView) findViewById(R.id.mapview);
        if ( osm_pos != null ) {
            //Log.d("OSM DEL", Boolean.toString(osm_items.removeItem(osm_pos)));
            osm_items.removeItem(osm_pos);

        }
        Log.d("Pos", latitude + ":" + longitude);

        osm_pos = new OverlayItem("Current", "Location", new GeoPoint(latitude, longitude));
        Drawable newMarker = this.getResources().getDrawable(R.drawable.icon);
        osm_pos.setMarker(newMarker);
        osm_items.addItem(osm_pos);
        //Log.d("Overlay num", Integer.toString(map.getOverlays().size()));
        map.invalidate();
    }

    void getTags()
    {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url = "https://minethetag.cf/api/tags/get";
        Map<String, Double> params = new HashMap<String, Double>();
        JSONObject jsonObj = new JSONObject(params);

        JsonObjectRequest MyStringRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                JSONArray propis = null;
                JSONArray aliens = null;
                try {
                    propis = response.getJSONArray("tags propis");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    aliens = response.getJSONArray("tags aliens");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayList<OverlayItem> overlayItemArray;
                overlayItemArray = new ArrayList<OverlayItem>();
                for (int i = 0; i < aliens.length(); i++)
                {
                    JSONObject a = null;
                    try {
                        a = aliens.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Double posX = null;
                    Double posY = null;
                    try {
                        posX = (Double) a.get("x_pos");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        posY = (Double) a.get("y_pos");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    OverlayItem mina = new OverlayItem("New", "TAG", new GeoPoint(posX, posY));
                    Drawable newMarker = getResources().getDrawable(R.drawable.tag_extern);
                    mina.setMarker(newMarker);
                    overlayItemArray.add(mina);
                }
                MapView map = (MapView) findViewById(R.id.mapview);
                MyOwnItemizedOverlay overlay = new MyOwnItemizedOverlay(getApplicationContext(), overlayItemArray);
                map.getOverlays().add(overlay);
                map.getOverlays().remove(osm_tags_aliens);
                osm_tags_aliens = overlay;
                overlayItemArray = new ArrayList<OverlayItem>();
                for (int i = 0; i < propis.length(); i++)
                {
                    JSONObject a = null;
                    try {
                        a = propis.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Double posX = null;
                    Double posY = null;
                    try {
                        posX = (Double) a.get("x_pos");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        posY = (Double) a.get("y_pos");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    OverlayItem mina = new OverlayItem("New", "TAG", new GeoPoint(posX, posY));
                    Drawable newMarker = getResources().getDrawable(R.drawable.tag_propi);
                    mina.setMarker(newMarker);
                    overlayItemArray.add(mina);

                }
                MyOwnItemizedOverlay overlay2 = new MyOwnItemizedOverlay(getApplicationContext(), overlayItemArray);
                map.getOverlays().add(overlay2);
                map.getOverlays().remove(osm_tags_propis);
                osm_tags_propis = overlay2;
                map.invalidate();
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.

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

    void CheckExplosio()
    {
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
                    Toast.makeText(getApplicationContext(), "BOOOOOOM ", Toast.LENGTH_SHORT).show();
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    v.vibrate(2000);
                    try {
                        JSONArray a = response.getJSONArray("exploded_mines");
                        Toast.makeText(getApplicationContext(), "EXPLOTADES = " + a, Toast.LENGTH_SHORT).show();
                        // Mines que exploten
                        ArrayList<OverlayItem> overlayItemArray;
                        overlayItemArray = new ArrayList<OverlayItem>();
                        for (int i = 0; i < a.length(); i++)
                        {
                            JSONArray pos = null;
                            try {
                                pos = (JSONArray) a.get(i);
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
                            OverlayItem mina = new OverlayItem("New", "Mina", new GeoPoint(posX, posY));
                            Drawable newMarker = getResources().getDrawable(R.drawable.mine28_exploted);
                            mina.setMarker(newMarker);
                            overlayItemArray.add(mina);



                        }
                        MapView map = (MapView) findViewById(R.id.mapview);
                        MyOwnItemizedOverlay overlay = new MyOwnItemizedOverlay(getApplicationContext(), overlayItemArray);
                        map.getOverlays().add(overlay);
                        map.getOverlays().remove(osm_mines_explotades);
                        osm_mines_explotades = overlay;
                        map.invalidate();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Dormir l'usuari durant 3 minuts (de moment nomes 10 segons per la demo)
                    startActivity(new Intent(MainActivity.this, pop.class));
                }
                else
                {
                    //Toast.makeText(getApplicationContext(), "CALM ", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(this).addToRequestQueue(MyStringRequest);
    }

    void getAltresMinesUsuari()
    {
        String url = "https://minethetag.cf/api/admin/mines/getdiff";

        JsonArrayRequest MyStringRequest = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //Toast.makeText(getApplicationContext(), "Response = " + response, Toast.LENGTH_LONG).show();
                ArrayList<OverlayItem> overlayItemArray;
                overlayItemArray = new ArrayList<OverlayItem>();
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
                    OverlayItem mina = new OverlayItem("New", "Mina", new GeoPoint(posX, posY));
                    Drawable newMarker = getResources().getDrawable(R.drawable.mine28_admin);
                    mina.setMarker(newMarker);
                    overlayItemArray.add(mina);
                }
                MapView map = (MapView) findViewById(R.id.mapview);
                MyOwnItemizedOverlay overlay = new MyOwnItemizedOverlay(getApplicationContext(), overlayItemArray);
                map.getOverlays().add(overlay);
                map.getOverlays().remove(osm_mines_alienes);
                osm_mines_alienes = overlay;
                map.invalidate();
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
        MySingleton.getInstance(this).addToRequestQueue(MyStringRequest);
    }

    void getMinesUsuari()
    {
        String url = "https://minethetag.cf/api/mines/get";
        JsonArrayRequest MyStringRequest = new JsonArrayRequest(Request.Method.POST, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //Toast.makeText(getApplicationContext(), "Response = " + response, Toast.LENGTH_LONG).show();
                ArrayList<OverlayItem> overlayItemArray;
                overlayItemArray = new ArrayList<OverlayItem>();
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
                    OverlayItem mina = new OverlayItem("New", "Mina", new GeoPoint(posX, posY));
                    Drawable newMarker = getResources().getDrawable(R.drawable.mine28);
                    mina.setMarker(newMarker);
                    overlayItemArray.add(mina);
                }
                MapView map = (MapView) findViewById(R.id.mapview);
                MyOwnItemizedOverlay overlay = new MyOwnItemizedOverlay(getApplicationContext(), overlayItemArray);
                map.getOverlays().add(overlay);
                map.getOverlays().remove(osm_mines_propies);
                osm_mines_propies = overlay;
                map.invalidate();
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
                //Log.wtf("ERROR mines get: ", error.getMessage().toString());
                //Toast.makeText(getApplicationContext(), "Server Error get mines", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(this).addToRequestQueue(MyStringRequest);
    }

    void authentification()
    {
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
                Toast.makeText(getApplicationContext(), "Server Error authentification", Toast.LENGTH_SHORT).show();
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
        MySingleton.getInstance(this).addToRequestQueue(MyStringRequest);
    }

    void test()
    {
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
        MySingleton.getInstance(this).addToRequestQueue(MyStringRequest);
    }

    void altaMines(final double posMinaX, final double posMinaY)
    {
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
                try {
                    if (response.getString("result").equals("OK"))
                    {
                        //Toast.makeText(getApplicationContext(), "Mina donada de alta", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "You cannot insert more mines", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
        MySingleton.getInstance(this).addToRequestQueue(MyStringRequest);
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
    protected void onStop() {
        super.onStop();
        ha_ref.removeCallbacks(runab_ref);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.newTag)
        {
            Intent a = new Intent(this, NewTagActivity.class);
            startActivity(a);
        }
        else if (id == R.id.captureTag)
        {
            startActivity(new Intent(this, CaptureTagActivity.class));
        }
        else if (id == R.id.logout)
        {
            final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMax(10);
            progressDialog.setMessage("Logging out...");
            progressDialog.show();
            SharedPreferences settings = getSharedPreferences("mttg_config",0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("TOKEN",null);
            editor.commit();


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
