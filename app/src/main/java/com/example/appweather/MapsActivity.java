package com.example.appweather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.appweather.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    ActivityMapsBinding mapsBinding;
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    public static final int REQUESST_CODE_AUTOCOMPLEX = 100;
    private double lat;
    private double lng;
    private String nameAddress;
    private String nameLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapsBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        getDriverLocation();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        //set edittext non focusable
        mapsBinding.edtLocation.setFocusable(false);
        mapsBinding.edtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //initialize place field list
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS,
                        Place.Field.LAT_LNG, Place.Field.NAME);
                //create  intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY,
                        fieldList).build(MapsActivity.this);
                startActivityForResult(intent, REQUESST_CODE_AUTOCOMPLEX);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESST_CODE_AUTOCOMPLEX && resultCode == RESULT_OK) {
            //khi đúng thì khởi tạo Place
            Place place = Autocomplete.getPlaceFromIntent(data);
            //set address cho editext
            mapsBinding.edtLocation.setText(place.getAddress());

            lat = place.getLatLng().latitude;
            lng = place.getLatLng().longitude;
            nameAddress = place.getAddress();
            nameLocation = place.getName();
            mapsBinding.latlng.setText(lat + "," + lng);
            mapsBinding.name.setText(nameAddress);
            Log.d("Location Name: ", place.getName());
            Log.d("Lat: ", place.getLatLng().latitude + "");
            Log.d("Lng: ", place.getLatLng().longitude + "");
            LatLng latLng = new LatLng(lat, lng);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerOptions.title(nameLocation).snippet(nameAddress);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            mMap.addMarker(markerOptions);
        } else if (requestCode == AutocompleteActivity.RESULT_ERROR) {
            //khi lỗi khởi tạo status
            Status status = Autocomplete.getStatusFromIntent(data);
            //hiện thị thống báo
            Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        LatLng latLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerOptions.title("nameLocation").snippet("nameAddress");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        mMap.addMarker(markerOptions);

    }

    public void getDriverLocation() {
        Bundle bundle = getIntent().getExtras();
        lat = bundle.getDouble(MainActivity.KEY_MAPSACTIVITY_LAT);
        lng = bundle.getDouble(MainActivity.KEY_MAPSACTIVITY_LNG);
    }
}
