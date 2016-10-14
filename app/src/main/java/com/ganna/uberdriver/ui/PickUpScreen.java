package com.ganna.uberdriver.ui;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ganna.uberdriver.Constants;
import com.ganna.uberdriver.R;
import com.ganna.uberdriver.managers.FireManager;
import com.ganna.uberdriver.util.MapUtil;
import com.ganna.uberdriver.util.VolleySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class PickUpScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int progressMax;
    @BindView(R.id.progressBar)ProgressBar progressBar;
    @BindView(R.id.elapsed_time)TextView elapsedTime;
    @BindView(R.id.pickup_address)TextView pickupAddress;
    private boolean isPickInfoFirstTime=true;
    private Double lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);
        ButterKnife.bind(this);
        lat =getIntent().getDoubleExtra(Constants.LAT,0);
        lng =getIntent().getDoubleExtra(Constants.LNG,0);
        sendLocationUpdates();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getNavToRiderTime(lat,lng);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setAllGesturesEnabled(false);
        focusPicUpLocation(new LatLng(lat,lng));
    }

    private void focusPicUpLocation(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
        mMap.addMarker(new MarkerOptions().position(latLng));
    }

    private void getNavToRiderTime(final Double lat, final Double lng) {
        String url = String.format(Constants.BASE_DIRECTION_URL,
                lat + "," + lng,
                getIntent().getStringExtra(Constants.CURRENT_LOC));
        Log.d("url", url);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Time", "onResponse: " + response.toString());
                        try {
                            JSONObject duration = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0)
                                    .getJSONObject("duration");
                            JSONObject distance = response.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0)
                                    .getJSONObject("distance");
                            String timeTxt = duration.getString("text");
                            int timeValue = duration.getInt("value");

                            int currentDistanceInM = distance.getInt("value");
                            Log.d("distance", ""+currentDistanceInM);
                            //Rider and driver are close - 100 M
                            if (currentDistanceInM<100){
                                setRideStatus(Constants.STATUS_PICK_ARRIVED);
                            }
                            if (isPickInfoFirstTime){
                                progressMax =timeValue/60;
                                progressBar.setMax(progressMax);
                                Log.d("Progress", "max"+progressMax);
                            }
                            setToRiderInfo(timeTxt,timeValue,lat,lng);
                            isPickInfoFirstTime=false;
                            Log.d("Time", "onResponse: " + timeTxt + "/val/" + timeValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Time", "onErrorResponse: ", error.getCause());
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjReq);
    }

    private void setRideStatus(String status) {
        FireManager.getRideNode()
                .child(Constants.RIDE_STATUS_KEY).setValue(status).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });
    }


    private void setToRiderInfo(String timeTxt, int timeValue,Double lat,Double lng) {
        elapsedTime.setText(timeTxt);
        int current = Math.abs(progressMax-timeValue/60);
        progressBar.setProgress(current);
        Log.d("Progress", ""+current);
        pickupAddress.setText(MapUtil.getAddress(this,lat,lng));

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            Log.d("Location", "onLocationChanged: "+location.getLatitude());
            HashMap map = new HashMap();
            map.put(Constants.DRIVER_LATLNG_KEY + "/lat", location.getLatitude());
            map.put(Constants.DRIVER_LATLNG_KEY + "/lng", location.getLongitude());
            FireManager.getRideNode()
                    .updateChildren(map);
            getNavToRiderTime(location.getLatitude(),location.getLongitude());

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void sendLocationUpdates() {
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria crita = new Criteria();
        crita.setAccuracy(Criteria.ACCURACY_FINE);
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(crita, false), 1000,
                0, mLocationListener);
    }

    public static boolean isActive = false;

    @Override
    public void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        isActive = false;
    }
}

