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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.google.android.gms.location.DetectedActivity;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;

import fr.quentinklein.slt.LocationTracker;
import fr.quentinklein.slt.TrackerSettings;
import io.nlopez.smartlocation.OnActivityUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

import static com.example.omair.minethetag.LoginActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.omair.minethetag.LoginActivity.latitude;
import static com.example.omair.minethetag.LoginActivity.longitude;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    LocationManager locationManager;
    int pos = 1;
    double radi = 0.00019;
    double posMinaX = latitude;
    double posMinaY = longitude;
    int TotalMines = 5;
    int inicialMines = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
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

        SmartLocation.with(getApplicationContext()).activityRecognition()
                .start(new OnActivityUpdatedListener() {

                    @Override
                    public void onActivityUpdated(DetectedActivity a)
                    {
                       // Toast.makeText(getApplicationContext(), "Latitude : " + latitude, Toast.LENGTH_SHORT).show();
                    }
                });

        final MapView map = (MapView) findViewById(R.id.mapview);
        map.setTileSource(TileSourceFactory.MAPNIK);
        //map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(20);
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);

        ArrayList<OverlayItem> overlayItemArray;
        overlayItemArray = new ArrayList<OverlayItem>();
        OverlayItem linkopingItem = new OverlayItem("Current", "Location", new GeoPoint(latitude, longitude));
        Drawable newMarker = this.getResources().getDrawable(R.drawable.icon);
        linkopingItem.setMarker(newMarker);
        overlayItemArray.add(linkopingItem);
        final ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(this, overlayItemArray, null);

        // Add the overlay to the MapView
        map.getOverlays().add(itemizedIconOverlay);

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
                if (inicialMines <= TotalMines)
                {
                    ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
                    double posX = 0, posY = 0;
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
                    map.invalidate();
                    ++inicialMines;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "You cannot insert more mines!", Toast.LENGTH_SHORT).show();
                }
            }
        });

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

        if (id == R.id.logout)
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
