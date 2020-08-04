package com.example.appweather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.appweather.databinding.ActivityMapsBinding;
import com.example.appweather.helper.Helper;
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
    public static final String EXTRA_DATA_LAT = "Lat";
    public static final String EXTRA_DATA_Lng = "Lng";
    public static final String EXTRA_DATA_ADDRESS = "Address";

    private double lat;
    private double lng;
    private String nameAddress;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapsBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        getDriverLocation();
        Log.d("BBBBBBBB", lat + "");
        Log.d("BBBBBBBB", lng + "");
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
        mapsBinding.btnFindWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.checkLatLng(lat, lng) == true) {
                    final Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putDouble(EXTRA_DATA_LAT, lat);
                    bundle.putDouble(EXTRA_DATA_Lng, lng);
                    bundle.putString(EXTRA_DATA_ADDRESS, nameAddress);
                    intent.putExtras(bundle);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(MapsActivity.this, "Bạn chưa chọn địa điểm", Toast.LENGTH_SHORT).show();
                }

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
            name = place.getName();
            Log.d("BBBBBBBBBBBBBB ", nameAddress);
            Log.d("BBBBBBBBBBBBBB ", lat + "");
            Log.d("BBBBBBBBBBBBBB ", lng + "");
            Helper.addMakerLocation(mMap, lat, lng, name, nameAddress);
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
        //mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        LatLng latLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerOptions.title("My Here").snippet(nameAddress);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        mMap.addMarker(markerOptions);

    }

    public void getDriverLocation() {
        Bundle bundle = getIntent().getExtras();
        lat = bundle.getDouble(MainActivity.KEY_MAPSACTIVITY_LAT);
        lng = bundle.getDouble(MainActivity.KEY_MAPSACTIVITY_LNG);
        nameAddress = bundle.getString(MainActivity.KEY_MAPSACTIVITY_Address);
    }
}
