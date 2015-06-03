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
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.microsoft.windowsazure.mobileservices.MobileServiceJsonTable;
import com.microsoft.windowsazure.mobileservices.MobileServiceQuery;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class MapActivity extends ActionBarActivity {
    private GoogleMap map;
    private LocationManager mLocationManager;
    private Location current;
    private MobileServiceClient client;
    private MobileServiceUser user;
    private NewApp myApp;

    //Used for event form
    private EventAdapter eventAdapter;
    private ArrayList<Event> events = new ArrayList<Event>();
    private MobileServiceTable<Event> eTable;
    private TextView mName;
    private TextView mAddress;
    private TextView mAttributions;

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(47.5, -122.5), new LatLng(47.7, -122.3));

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        myApp = (NewApp)getApplication();
        client = myApp.getClient();
        user = client.getCurrentUser();

        //placeholder button for creating a new event
        Button eventButton = (Button) findViewById(R.id.newEvent);
        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertFormElements();
            }
        });

        //placeholder button for loading list of evente
        Button eventLoadButton = (Button) findViewById(R.id.loadEvents);
        eventLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLoadEvents();
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
        loadEvents(); //Gets events from backend and adds them to events list
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

    //creates alert dialog with list of all current events
    public void alertLoadEvents() {
        LayoutInflater loadinflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View loadElementsView = loadinflater.inflate(R.layout.load_elements,
                null, false);
        final ListView listViewEvent = (ListView) loadElementsView
                .findViewById(R.id.listViewEvent);

        // Load the items from the Mobile Service
        loadEvents();
        //Toast.makeText(MapActivity.this, "loaded-"+events.size(), Toast.LENGTH_LONG).show();

        // Create The Adapter with passing ArrayList as 3rd parameter
        EventAdapter arrayAdapter =
                new EventAdapter(MapActivity.this,0,events);
        listViewEvent.setAdapter(arrayAdapter);


        AlertDialog ok = new AlertDialog.Builder(MapActivity.this).setView(loadElementsView)
                .setTitle("Saved Events")
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Toast.makeText(MapActivity.this, "load", Toast.LENGTH_LONG).show();

                        dialog.cancel();
                    }
                }).show();


    }


    //Refresh the list with the items in the Mobile Service Table
    //gets all the events and loads them to the events arraylist
    public void loadEvents(){
        // Get the Mobile Service Table instance to use
        eTable = client.getTable(Event.class);
        eTable.execute(new TableQueryCallback<Event>() {
            @Override
            public void onCompleted(List<Event> list, int i, Exception e, ServiceFilterResponse serviceFilterResponse) {
                if (e == null) {
                    //success
                    events.clear();
                    for (Event event : list) {
                        events.add(event);
                    }
                    Toast.makeText(MapActivity.this, "loaded - " + events.size() + " events", Toast.LENGTH_LONG).show();
                    if (map != null) {
                        for (Event event : events) {
                            if (event.getLat().length() > 3 && event.getLng().length() > 3) {
                                map.addMarker(new MarkerOptions().position(
                                        new LatLng(Double.parseDouble(event.getLat()), Double.parseDouble(event.getLng()))
                                ).title(event.getName()).snippet("an event in DoSomething"));
                            }
                        }
                    }
                } else {
                    //failure
                    Toast.makeText(MapActivity.this, "fail-" + e.getCause(), Toast.LENGTH_LONG).show();
                }
            }
        });

    };


    //Creates a alert dialog with the form elements for creating a new event
    public void alertFormElements() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View formElementsView = inflater.inflate(R.layout.form_elements,
                null, false);

        // You have to list down your form elements
        final EditText nameEditText = (EditText) formElementsView
                .findViewById(R.id.nameEditText);
        final Spinner categorySpinner = (Spinner) formElementsView
                .findViewById(R.id.category_spinner);
//        final EditText eventTime = (EditText) formElementsView
//               .findViewById(R.id.eventTime);
//        final EditText eventDate = (EditText) formElementsView
//                .findViewById(R.id.eventDate);
        final EditText eventDescription = (EditText) formElementsView
                .findViewById(R.id.eventDescription);
        final EditText eventLat = (EditText) formElementsView
                .findViewById(R.id.eventLat);
        final EditText eventLng = (EditText) formElementsView
                .findViewById(R.id.eventLng);
        final DatePicker eventCalendar = (DatePicker) formElementsView
                .findViewById((R.id.event_calender));
        final TimePicker eventTime = (TimePicker) formElementsView
                .findViewById(R.id.event_time);


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
//                    String[] accountTypes = new String[]{"com.facebook"};
//                    Intent intent = AccountPicker.newChooseAccountIntent(null, null,
//                            accountTypes, false, null, null, null, null);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        AlertDialog ok = new AlertDialog.Builder(MapActivity.this).setView(formElementsView)
                .setTitle("New Event")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Get values from form
                        Event event = new Event();
                        event.setName(nameEditText.getText().toString().trim());
                        event.setAuthor(user.getUserId().trim());
//                        event.date = eventDate.getText().toString().trim();
//                        event.time = eventTime.getText().toString().trim();
                        event.setDate(eventCalendar.getMonth() + "/" + eventCalendar.getDayOfMonth() + "/" + eventCalendar.getYear());
                        event.setTime(eventTime.getCurrentHour() + ":" + eventTime.getCurrentMinute());
                        event.setDescription(eventDescription.getText().toString().trim());
                        event.setLat(eventLat.getText().toString().trim());
                        event.setLng(eventLng.getText().toString().trim());

                        if (!event.getName().isEmpty()) {
                            if (-90 <= Double.parseDouble(event.getLat()) && Double.parseDouble(event.getLat()) <= 90 && -180 <= Double.parseDouble(event.getLng()) && Double.parseDouble(event.getLng()) <= 180) {
                                client.getTable(Event.class).insert(event, new TableOperationCallback<Event>() {
                                    @Override
                                    public void onCompleted(Event event, Exception e1, ServiceFilterResponse serviceFilterResponse) {
                                        if (e1 == null) {
                                            //success
                                            Toast.makeText(MapActivity.this, "success", Toast.LENGTH_LONG).show();

                                        } else {
                                            //failure
                                            Toast.makeText(MapActivity.this, "fail-" + e1.getCause(), Toast.LENGTH_LONG).show();

                                        }
                                    }
                                });

                                Toast.makeText(MapActivity.this, "submitted event " + nameEditText.getText()
                                        .toString().trim(), Toast.LENGTH_LONG).show();

                                dialog.cancel();
                            } else {
                                Toast.makeText(MapActivity.this, "must choose valid lat/lng", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(MapActivity.this, "1 or more empty fields", Toast.LENGTH_LONG).show();
                        }
                    }
                }).show();
        loadEvents();
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