package edu.washington.wsmay1.dosomething;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.*;
import android.widget.*;
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.util.regex.Pattern;



public class MapActivity extends ActionBarActivity {
    private GoogleMap map;
    private LocationManager mLocationManager;
    private Location current;
    private MobileServiceClient client;
    private MobileServiceUser user;
    public static NewApp myApp;

    //Used for event form
    private EventAdapter eventAdapter;
    public static ArrayList<Event> events = new ArrayList<Event>();
    private ArrayList<Event> myEvents = new ArrayList<Event>();
    private ArrayList<Event> upcomingEvents = new ArrayList<Event>();
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

        loadEvents();
        LatLng test = getLocationFromAddress("1800 NE 47th Street Seattle, WA 98105");
        Toast.makeText(this, test.toString(), Toast.LENGTH_LONG).show();
        
    }

    public void loadMap(GoogleMap map) {
        this.map = map;
        map.setMyLocationEnabled(true);
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 13));
        }
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker marker) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.info_window, null);

                // Getting reference to the TextView to set title
                TextView title = (TextView) v.findViewById(R.id.title);
                title.setText(marker.getTitle());
                String[] items = marker.getSnippet().split(Pattern.quote("|"));
                TextView desc = (TextView) v.findViewById(R.id.description);
                desc.setText(items[0]);
                TextView date = (TextView) v.findViewById(R.id.date);
                date.setText(items[2]);
                TextView author = (TextView) v.findViewById(R.id.author);
                author.setText(items[1]);

                // Returning the view containing InfoWindow contents
                return v;

            }

        });
        loadEvents(); //Gets events from backend and adds them to events list
        long time = 5;
        float distance = 10;
        //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, this.mLocationListener);
        //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, distance, mLocationListener);

    };

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            current = location;
            LatLng coordinates = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 11));
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
        //loadEvents();

        Log.w("=====================", "my events ->" + myEvents.size());
        Log.w("=====================", "All events ->" + events.size());

        // Create The Adapter with passing ArrayList as 3rd parameter
        EventAdapter arrayAdapter =
                new EventAdapter(MapActivity.this,0,upcomingEvents);
        listViewEvent.setAdapter(arrayAdapter);

        AlertDialog ok = new AlertDialog.Builder(MapActivity.this).setView(loadElementsView)
                .setTitle("Upcoming Events")
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }).show();


        // create a new ListView, set the adapter and item click listener
        listViewEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
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
                    myEvents.clear();
                    upcomingEvents.clear();
                    for (Event event : list) {
                        events.add(event);
                    }
                    if (map != null) {
                        for (Event event : events) {
                            if (event.getAuthor().equals(user.getUserId())) {
                                myEvents.add(event);
                            }
                            try {
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                Date date = formatter.parse(event.getDate());
                                Date thisDate = Calendar.getInstance().getTime();
                                if (!date.before(thisDate)) {
                                    if (event.getLat().length() > 3 && event.getLng().length() > 3) {
                                        upcomingEvents.add(event);
                                        addMarker(event);
                                    }
                                }
                            } catch (Exception ex) {
                                Log.w("submit================", ex.toString());
                                Log.w("submit================", event.getDate().toString());
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

    public void addMarker(Event event) {
        final String category = event.getCategory().toLowerCase().trim();
        Log.w("category======", category);
        if (category.equals("athletics")) {
            map.addMarker(new MarkerOptions().position(
                    new LatLng(Double.parseDouble(event.getLat()), Double.parseDouble(event.getLng())))
                        .title(event.getName()).snippet(event.getDescription() + "|" + "Hosted By: " + event.getHost() + "|" + "Date: " + event.getDate())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.sport))
            );
        } else if (category.equals("academics")) {
            map.addMarker(new MarkerOptions().position(
                            new LatLng(Double.parseDouble(event.getLat()), Double.parseDouble(event.getLng())))
                            .title(event.getName()).snippet(event.getDescription() + "|" + "Hosted By: " + event.getHost() + "|" + "Date: " + event.getDate())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.book))
            );
        } else if (category.equals("social")) {
            map.addMarker(new MarkerOptions().position(
                            new LatLng(Double.parseDouble(event.getLat()), Double.parseDouble(event.getLng())))
                            .title(event.getName()).snippet(event.getDescription() + "|" + "Hosted By: " + event.getHost() + "|" + "Date: " + event.getDate())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.network))
            );
        } else if (category.equals("night-life")) {
            map.addMarker(new MarkerOptions().position(
                            new LatLng(Double.parseDouble(event.getLat()), Double.parseDouble(event.getLng())))
                            .title(event.getName()).snippet(event.getDescription() + "|" + "Hosted By: " + event.getHost() + "|" + "Date: " + event.getDate())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.moon))
            );
        } else if (category.equals("gaming")) {
            map.addMarker(new MarkerOptions().position(
                            new LatLng(Double.parseDouble(event.getLat()), Double.parseDouble(event.getLng())))
                            .title(event.getName()).snippet(event.getDescription() + "|" + "Hosted By: " + event.getHost() + "|" + "Date: " + event.getDate())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.game))
            );
        } else if (category.equals("entertainment")) {
            map.addMarker(new MarkerOptions().position(
                            new LatLng(Double.parseDouble(event.getLat()), Double.parseDouble(event.getLng())))
                            .title(event.getName()).snippet(event.getDescription() + "|" + "Hosted By: " + event.getHost() + "|" + "Date: " + event.getDate())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.tele))
            );
        } else if (category.equals("activism")) {
            map.addMarker(new MarkerOptions().position(
                            new LatLng(Double.parseDouble(event.getLat()), Double.parseDouble(event.getLng())))
                            .title(event.getName()).snippet(event.getDescription() + "|" + "Hosted By: " + event.getHost() + "|" + "Date: " + event.getDate())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mega))
            );
        } else if (category.equals("party")) {
            map.addMarker(new MarkerOptions().position(
                            new LatLng(Double.parseDouble(event.getLat()), Double.parseDouble(event.getLng())))
                            .title(event.getName()).snippet(event.getDescription() + "|" + "Hosted By: " + event.getHost() + "|" + "Date: " + event.getDate())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.balloon))
            );
        } else if (category.equals("other")) {
            map.addMarker(new MarkerOptions().position(
                            new LatLng(Double.parseDouble(event.getLat()), Double.parseDouble(event.getLng())))
                            .title(event.getName()).snippet(event.getDescription() + "|" + "Hosted By: " + event.getHost() + "|" + "Date: " + event.getDate())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.rsz_ban))
            );
        }
    }

    public LatLng getLocationFromAddress(String strAddress) {
        List<Address> addresses;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocationName(strAddress, 1);
            return new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        } catch (Exception e) {
            Toast.makeText(this, "Please enter a valid address", Toast.LENGTH_LONG).show();
            return null;
        }
    }

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
        final EditText eventDescription = (EditText) formElementsView
                .findViewById(R.id.eventDescription);
        final DatePicker eventCalendar = (DatePicker) formElementsView
                .findViewById((R.id.event_calender));
        final TimePicker eventTime = (TimePicker) formElementsView
                .findViewById(R.id.event_time);
        final EditText hostName = (EditText) formElementsView.findViewById(R.id.hostName);
        final EditText address = (EditText) formElementsView.findViewById(R.id.address);

        AlertDialog ok = new AlertDialog.Builder(MapActivity.this).setView(formElementsView)
                .setTitle("New Event")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Get values from form
                        Event event = new Event();
                        event.setName(nameEditText.getText().toString().trim());
                        event.setAuthor(user.getUserId().trim());
                        event.setCategory(categorySpinner.getSelectedItem().toString());
                        event.setDate(eventCalendar.getMonth() + "/" + eventCalendar.getDayOfMonth() + "/" + eventCalendar.getYear());
                        event.setTime(eventTime.getCurrentHour() + ":" + eventTime.getCurrentMinute());
                        event.setDescription(eventDescription.getText().toString().trim());
                        event.setHost(hostName.getText().toString().trim());
                        LatLng location = getLocationFromAddress(address.getText().toString().trim());
                        event.setLat("" + location.latitude);
                        event.setLng("" + location.longitude);

                        if (!event.getName().isEmpty()) {
                            if (-90 <= Double.parseDouble(event.getLat()) && Double.parseDouble(event.getLat()) <= 90 && -180 <= Double.parseDouble(event.getLng()) && Double.parseDouble(event.getLng()) <= 180) {
                                client.getTable(Event.class).insert(event, new TableOperationCallback<Event>() {
                                    @Override
                                    public void onCompleted(Event event, Exception e1, ServiceFilterResponse serviceFilterResponse) {
                                        if (e1 == null) {
                                            //success
                                            loadEvents();
                                        } else {
                                            //failure
                                            Toast.makeText(MapActivity.this, "failed to save event -" + e1.getCause(), Toast.LENGTH_LONG).show();
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


}