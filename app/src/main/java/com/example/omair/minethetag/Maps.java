package com.example.omair.minethetag;

import android.app.Activity;
import android.os.Bundle;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

public class Maps extends MainActivity {

    MapView mapView;
    MapController mapController;
    ArrayList<OverlayItem> overlayItemArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mapView = (MapView) this.findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        mapController = (MapController) mapView.getController();
        mapController.setZoom(14);

        GeoPoint startPoint = new GeoPoint(58.4109, 15.6216);
        mapController.setCenter(startPoint);

        // Create an ArrayList with overlays to display objects on map
        overlayItemArray = new ArrayList<OverlayItem>();

        // Create som init objects
        OverlayItem linkopingItem = new OverlayItem("Linkoping", "Sweden",
                new GeoPoint(58.4109, 15.6216));
        OverlayItem stockholmItem = new OverlayItem("Stockholm", "Sweden",
                new GeoPoint(59.3073348, 18.0747967));

        // Add the init objects to the ArrayList overlayItemArray
        overlayItemArray.add(linkopingItem);
        overlayItemArray.add(stockholmItem);

        // Add the Array to the IconOverlay
        ItemizedIconOverlay<OverlayItem> itemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(this, overlayItemArray, null);

        // Add the overlay to the MapView
        mapView.getOverlays().add(itemizedIconOverlay);

    }


}