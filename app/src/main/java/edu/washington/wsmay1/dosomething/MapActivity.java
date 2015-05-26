package edu.washington.wsmay1.dosomething;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.location.*;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;



public class MapActivity extends ActionBarActivity {
    private GoogleMap map;
    private LocationManager mLocationManager;
    private Location current;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //ActionBar actionBar = getActionBar();
        //actionBar.show();

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                loadMap(map);
            }
        });

        //mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //long time = 5;
        //float distance = 10;
        //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, mLocationListener);
        //mLocationManager.request
    }

    public void loadMap(GoogleMap map) {
        LatLng sydney = new LatLng(-33.867, 151.206);
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 13));

        map.addMarker(new MarkerOptions()
                .title("Sydney")
                .snippet("The most populous city in Australia.")
                .position(sydney));
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            current = location;
        }
    };

}