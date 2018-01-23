package com.example.rohannevrikar.googlemaptest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {
    private static final String TAG = "message";
    private GoogleMap mMap;
    LocationManager locationManager;
    private Button btnDone;
    private ImageView currentLocation;
    private EditText searchInput;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static Marker currentLocationMarker;
    private static Marker inputLocationMarker;
    private static Marker marker;
    private static Marker destinationMarker;
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private static final float DEFAULT_ZOOM = 15f;
    private static int count = 0;
    private SharedPreferences sharedPreferences;
    private LatLng position;
    private double latitude;
    private double longitude;
    private double sourceLatitude;
    private double sourceLongitude;
    private double end_latitude;
    private double end_longitude;
    private Polyline polyLine = null;
    private String parsedDistance;
    private LocationRequest locationRequest;
    private ArrayList<MarkerLatLng> listMarkers = new ArrayList<>();
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        searchInput = (EditText) findViewById(R.id.searchInput);
        currentLocation = (ImageView) findViewById(R.id.currentLocation);
//        currentLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                getCurrentLocation();
//                count = 1;
//            }
//
//        });
        btnDone = (Button) findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sharedPrefManager(latitude, longitude);
                Intent intent = new Intent(MapsActivity.this, RestaurantsList.class);
                startActivity(intent);

            }
        });
//        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for(Location location : locationResult.getLocations()){
                    if(listMarkers.get(0).getMarker() != null){
                        listMarkers.get(0).getMarker().remove();
                    }
                    MarkerLatLng markerLatLng = new MarkerLatLng();
                    markerLatLng.setMarker(mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
                    markerLatLng.setLatitude(location.getLatitude());
                    markerLatLng.setLongitude(location.getLongitude());
                    markerLatLng.setMarkerValue(0);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(markerLatLng.getLatitude(), markerLatLng.getLongitude()), DEFAULT_ZOOM));
                    //moveCamera(new LatLng(sourceLatitude, sourceLongitude), DEFAULT_ZOOM, 1);
                    listMarkers.add(0, markerLatLng);
                    Toast.makeText(MapsActivity.this, "New marker added ", Toast.LENGTH_SHORT).show();
                }
            }
        };
        getCurrentLocation();
        ;

    }

    private void onMarkerDrag(Marker marker) {

        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Toast.makeText(MapsActivity.this, "Starting drag", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                for (MarkerLatLng m : listMarkers) {
                    if (m.getMarker() == marker) {
                        m.setLatitude(marker.getPosition().latitude);
                        m.setLongitude(marker.getPosition().longitude);


                    }
                }
            }
        });

    }

    private void init() {
//        mGoogleApiClient = new GoogleApiClient
//                .Builder(this)
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .enableAutoManage(this, this)
//                .build();
//        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, LAT_LNG_BOUNDS, null);
//        searchInput.setAdapter(placeAutocompleteAdapter);
        buildGoogleApiClient();
        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if ((actionId == EditorInfo.IME_ACTION_SEARCH)
                        || (actionId == EditorInfo.IME_ACTION_DONE)
                        || (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        || (keyEvent.getAction() == KeyEvent.KEYCODE_ENTER)) {
                    geoInputLocation();

                }
                return false;
            }
        });
    }

    private void geoInputLocation() {

        String inputSearch = searchInput.getText().toString();
        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(inputSearch, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.size() > 0) {
            Address address = list.get(0);
            Log.d(TAG, address.toString());
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            latitude = address.getLatitude();
            longitude = address.getLongitude();
            sharedPrefManager(latitude, longitude);
            Log.d(TAG, "geoInputLocation: Removing marker ");
            Log.d(TAG, "geoInputLocation: Adding marker");
            //moveCamera(latLng, DEFAULT_ZOOM, 2);
            MarkerLatLng markerLatLng = new MarkerLatLng();
            markerLatLng.setMarker(mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));
            markerLatLng.setLatitude(latitude);
            markerLatLng.setLongitude(longitude);
            markerLatLng.setMarkerValue(1);

            markerLatLng.getMarker().setDraggable(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(markerLatLng.getLatitude(), markerLatLng.getLongitude()), DEFAULT_ZOOM));
            listMarkers.add(markerLatLng);
        }
    }

    private void getCurrentLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        final Task location = mFusedLocationProviderClient.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: found location!");
                    Location currentLocation = (Location) task.getResult();
                    Log.d(TAG, "getCurrent: adding marker");


//                    sourceLatitude = currentLocation.getLatitude();
//                    sourceLatitude = currentLocation.getLongitude();

                    MarkerLatLng markerLatLng = new MarkerLatLng();
                    markerLatLng.setMarker(mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))));
                    markerLatLng.setLatitude(currentLocation.getLatitude());
                    markerLatLng.setLongitude(currentLocation.getLongitude());
                    markerLatLng.setMarkerValue(0);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(markerLatLng.getLatitude(), markerLatLng.getLongitude()), DEFAULT_ZOOM));
                    //moveCamera(new LatLng(sourceLatitude, sourceLongitude), DEFAULT_ZOOM, 1);
                    listMarkers.add(markerLatLng);


                } else {
                    Log.d(TAG, "onComplete: current location is null");
                    Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sharedPrefManager(latitude, longitude);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }


    private void moveCamera(LatLng latLng, float zoom, int n) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        if (n == 1) {
            currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            marker = currentLocationMarker;
        } else {
            inputLocationMarker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            marker = inputLocationMarker;
        }


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        init();
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void sharedPrefManager(double latitude, double longitude) {
        sharedPreferences = getSharedPreferences("LocationPreference", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong("latitude", Double.doubleToRawLongBits(latitude));
        editor.putLong("longitude", Double.doubleToRawLongBits(longitude));
        editor.apply();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        findDistanceDirection(marker);
        onMarkerDrag(marker);

        return false;

    }

    private void findDistanceDirection(Marker marker) {
        position = marker.getPosition();
        if (listMarkers.get(0).getMarkerValue() == 0) {
            sourceLatitude = listMarkers.get(0).getLatitude();
            sourceLongitude = listMarkers.get(0).getLongitude();
            end_latitude = position.latitude;
            end_longitude = position.longitude;
            float[] results = new float[10];
            Location.distanceBetween(sourceLatitude, sourceLongitude, end_latitude, end_longitude, results);
            Toast.makeText(MapsActivity.this, "Distance between points : " + results[0], Toast.LENGTH_SHORT).show();

            String url = getRequestUrl(sourceLatitude, sourceLongitude, end_latitude, end_longitude);


            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);


        } else
            Toast.makeText(MapsActivity.this, "Cannot find source", Toast.LENGTH_SHORT).show();
    }

    private String getRequestUrl(double sourceLatitude, double sourceLongitude, double end_latitude, double end_longitude) {
        String str_org = "origin=" + sourceLatitude + "," + sourceLongitude;
        String str_dest = "destination=" + end_latitude + "," + end_longitude;
        Log.d(TAG, "getRequestUrl: " + str_org + " " + str_dest);
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String param = str_org + "&" + str_dest + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        Log.d(TAG, "getRequestUrl: " + url);
        return url;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest, mLocationCallback, null);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            String distance;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(10);
                polyLineOptions.color(Color.BLUE);
                if(polyLine != null){
                    polyLine.remove();
                }


                polyLine = mMap.addPolyline(polyLineOptions);
            }

        }
    }
}