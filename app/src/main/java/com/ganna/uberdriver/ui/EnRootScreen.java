package com.ganna.uberdriver.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.ganna.uberdriver.Constants;
import com.ganna.uberdriver.R;
import com.ganna.uberdriver.managers.FireManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EnRootScreen extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    @BindView(R.id.rider_tv)TextView riderTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_en_root);
        ButterKnife.bind(this);
        riderTv.setText("WAITING FOR "+getIntent().getStringExtra(Constants.RIDE_RIDER_NAME)+"!");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Double lat = getIntent().getDoubleExtra(Constants.LAT, 0);
        Double lng = getIntent().getDoubleExtra(Constants.LNG, 0);
        Log.d("EnRoot", lat+" / "+lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 18));
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @OnClick(R.id.start_btn)void startTrip(){
        FireManager.getRideNode()
                .child(Constants.RIDE_STATUS_KEY).setValue(Constants.STATUS_PICK_STARTED).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });

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
