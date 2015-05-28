package edu.washington.wsmay1.dosomething;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.*;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;

import java.net.MalformedURLException;


public class MapActivity extends ActionBarActivity {
    private GoogleMap map;
    private LocationManager mLocationManager;
    private Location current;
    private MobileServiceClient client;
    private MobileServiceUser user;
    private NewApp myApp;

    //Used for event form
    private static final int PLACE_PICKER_REQUEST = 1;
    private TextView mName;
    private TextView mAddress;
    private TextView mAttributions;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        myApp = (NewApp)getApplication();
        client = myApp.getClient();
        user = client.getCurrentUser();
        Button eventButton = (Button) findViewById(R.id.newEvent);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertFormElements();
            }
        });
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

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        //alertFormElements();
        //long time = 5;
        //float distance = 10;
        //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, mLocationListener);
        //mLocationManager.request
    }

    public void loadMap(GoogleMap map) {
        this.map = map;
        map.setMyLocationEnabled(true);
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 13));
        }
        long time = 5;
        float distance = 10;
        //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, this.mLocationListener);
        //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, mLocationListener);

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            current = location;
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 13));
        }
    };


    public void alertFormElements() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.form_elements,
                null, false);

        // You have to list down your form elements
        final EditText nameEditText = (EditText) formElementsView
                .findViewById(R.id.nameEditText);
        final Spinner categorySpinner = (Spinner) formElementsView
                .findViewById(R.id.category_spinner);
        final EditText eventTime = (EditText) formElementsView
                .findViewById(R.id.eventTime);
        final EditText eventDate = (EditText) formElementsView
                .findViewById(R.id.eventDate);
        final EditText eventDescription = (EditText) formElementsView
                .findViewById(R.id.eventDescription);

        final Button pickerButton = (Button) formElementsView
                .findViewById(R.id.pickerButton);
        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder intentBuilder =
                            new PlacePicker.IntentBuilder();
                    intentBuilder.setLatLngBounds(BOUNDS_MOUNTAIN_VIEW);
                    Intent intent = intentBuilder.build(getApplicationContext());
//                    String[] accountTypes = new String[]{"com.google"};
//                    Intent intent = AccountPicker.newChooseAccountIntent(null, null,
//                            accountTypes, false, null, null, null, null);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        AlertDialog ok = new AlertDialog.Builder(MapActivity.this).setView(formElementsView)
                .setTitle("Form Elements")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Get values from form
                        Event event =  new Event();
                        event.name = nameEditText.getText().toString().trim();
                        event.author = user.getUserId().trim();
                        event.date = eventDate.getText().toString().trim();
                        event.time = eventTime.getText().toString().trim();
                        event.description = eventDescription.getText().toString().trim();

                        client.getTable(Event.class).insert(event, new TableOperationCallback<Event>() {
                            @Override
                            public void onCompleted(Event event, Exception e1, ServiceFilterResponse serviceFilterResponse) {
                                if (e1 == null){
                                    //success
                                    Toast.makeText(MapActivity.this, "success", Toast.LENGTH_LONG).show();

                                } else {
                                    //failure
                                    Toast.makeText(MapActivity.this, "fail-"+e1.getCause(), Toast.LENGTH_LONG).show();

                                }
                            }
                        });

                        Toast.makeText(MapActivity.this, "submitted event "+ nameEditText.getText()
                                .toString().trim(), Toast.LENGTH_LONG).show();

                        dialog.cancel();
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {

            final Place place = PlacePicker.getPlace(data, this);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }

            mName.setText(name);
            mAddress.setText(address);
            mAttributions.setText(Html.fromHtml(attributions));

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}