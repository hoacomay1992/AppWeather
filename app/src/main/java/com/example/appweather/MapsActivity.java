package com.example.appweather;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appweather.databinding.ActivityMapsBinding;
import com.example.appweather.helper.ClusteringViewModel;
import com.example.appweather.helper.Helper;
import com.example.appweather.helper.MultiDrawable;
import com.example.appweather.helper.MyItem;
import com.example.appweather.helper.MyItemReader;
import com.example.appweather.helper.Person;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<Person>, ClusterManager.OnClusterInfoWindowClickListener<Person>, ClusterManager.OnClusterItemClickListener<Person>, ClusterManager.OnClusterItemInfoWindowClickListener<Person> {
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
    private ClusterManager<Person> mClusterManagerPerson;
    private Random mRandom = new Random(1984);
    private boolean mIsRestore;

    private LinearLayout btnMapTypeNormal;
    private LinearLayout btnMapTypeSatellite;
    private LinearLayout btnMapTypeHybrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mapsBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps);
        getDriverLocation();
        mIsRestore = savedInstanceState != null;
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
                    Toast.makeText(MapsActivity.this, getString(R.string.toat2), Toast.LENGTH_SHORT).show();
                }

            }
        });
        /**
         * chức năng set map typle
         */
        mapsBinding.btnSetMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapsBinding.btnMapType.getVisibility() == View.VISIBLE) {
                    mapsBinding.btnMapType.setVisibility(View.GONE);
                } else {
                    mapsBinding.btnMapType.setVisibility(View.VISIBLE);
                }
            }
        });
        btnMapTypeNormal = findViewById(R.id.btn_map_type_normal);
        btnMapTypeNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mapsBinding.btnMapType.setVisibility(View.GONE);
            }
        });
        btnMapTypeHybrid = findViewById(R.id.btn_map_type_hybrid);
        btnMapTypeHybrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                mapsBinding.btnMapType.setVisibility(View.GONE);
            }
        });
        btnMapTypeSatellite = findViewById(R.id.btn_map_type_satellite);
        btnMapTypeSatellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                mapsBinding.btnMapType.setVisibility(View.GONE);
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
            Helper.addMakerLocation(getMap(), lat, lng, name, nameAddress);
        } else if (requestCode == AutocompleteActivity.RESULT_ERROR) {
            //khi lỗi khởi tạo status
            Status status = Autocomplete.getStatusFromIntent(data);
            //hiện thị thống báo
            Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap != null) {
            return;
        }
        mMap = googleMap;
        startMaps(mIsRestore);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
    }

    /**
     * phương thức để lấy Dl lat,lang,nameAddress được gửi từ MainActivity
     */
    public void getDriverLocation() {
        Bundle bundle = getIntent().getExtras();
        lat = bundle.getDouble(MainActivity.KEY_MAPSACTIVITY_LAT);
        lng = bundle.getDouble(MainActivity.KEY_MAPSACTIVITY_LNG);
        nameAddress = bundle.getString(MainActivity.KEY_MAPSACTIVITY_Address);
    }

    public GoogleMap getMap() {
        return mMap;
    }

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
    private class PersonRenderer extends DefaultClusterRenderer<Person> {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mImageView;
        private final ImageView mClusterImageView;
        private final int mDimension;

        public PersonRenderer() {
            super(getApplicationContext(), getMap(), mClusterManagerPerson);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = multiProfile.findViewById(R.id.image);

            mImageView = new ImageView(getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(@NonNull Person person, MarkerOptions markerOptions) {
            // Draw a single person - show their profile photo and set the info window to show their name
            markerOptions
                    .icon(getItemIcon(person))
                    .title(person.name);
        }

        @Override
        protected void onClusterItemUpdated(@NonNull Person person, Marker marker) {
            // Same implementation as onBeforeClusterItemRendered() (to update cached markers)
            marker.setIcon(getItemIcon(person));
            marker.setTitle(person.name);
        }

        /**
         * Get a descriptor for a single person (i.e., a marker outside a cluster) from their
         * profile photo to be used for a marker icon
         *
         * @param person person to return an BitmapDescriptor for
         * @return the person's profile photo as a BitmapDescriptor
         */
        private BitmapDescriptor getItemIcon(Person person) {
            mImageView.setImageResource(person.profilePhoto);
            Bitmap icon = mIconGenerator.makeIcon();
            return BitmapDescriptorFactory.fromBitmap(icon);
        }

        @Override
        protected void onBeforeClusterRendered(@NonNull Cluster<Person> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            markerOptions.icon(getClusterIcon(cluster));
        }

        @Override
        protected void onClusterUpdated(@NonNull Cluster<Person> cluster, Marker marker) {
            // Same implementation as onBeforeClusterRendered() (to update cached markers)
            marker.setIcon(getClusterIcon(cluster));
        }

        /**
         * Get a descriptor for multiple people (a cluster) to be used for a marker icon. Note: this
         * method runs on the UI thread. Don't spend too much time in here (like in this example).
         *
         * @param cluster cluster to draw a BitmapDescriptor for
         * @return a BitmapDescriptor representing a cluster
         */
        private BitmapDescriptor getClusterIcon(Cluster<Person> cluster) {
            List<Drawable> profilePhotos = new ArrayList<>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (Person p : cluster.getItems()) {
                // Draw 4 at most.
                if (profilePhotos.size() == 4) break;
                Drawable drawable = getResources().getDrawable(p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            return BitmapDescriptorFactory.fromBitmap(icon);
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<Person> cluster) {
        // Show a toast with some info when the cluster is clicked.
        String firstName = cluster.getItems().iterator().next().name;
        Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();

        // Zoom in the cluster. Need to create LatLngBounds and including all the cluster items
        // inside of bounds, then animate to center of the bounds.

        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<Person> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(Person item) {
        // Does nothing, but you could go into the user's profile page, for example.
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Person item) {
        // Does nothing, but you could go into the user's profile page, for example.
    }

    private void startMaps(boolean isRestore) {
        if (!isRestore) {
            getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 18));
        }

        mClusterManagerPerson = new ClusterManager<>(this, getMap());
        mClusterManagerPerson.setRenderer(new PersonRenderer());
        getMap().setOnCameraIdleListener(mClusterManagerPerson);
        getMap().setOnMarkerClickListener(mClusterManagerPerson);
        getMap().setOnInfoWindowClickListener(mClusterManagerPerson);
        mClusterManagerPerson.setOnClusterClickListener(this);
        mClusterManagerPerson.setOnClusterInfoWindowClickListener(this);
        mClusterManagerPerson.setOnClusterItemClickListener(this);
        mClusterManagerPerson.setOnClusterItemInfoWindowClickListener(this);
        addItems();
        mClusterManagerPerson.cluster();
    }

    private void addItems() {

        mClusterManagerPerson.addItem(new Person(new LatLng(21.040569, 105.774544), "Ngọc Mama", R.drawable.ngoc));

        mClusterManagerPerson.addItem(new Person(new LatLng(lat, lng), "Hậu", R.drawable.hau));

        mClusterManagerPerson.addItem(new Person(new LatLng(21.039433, 105.781857), "Teacher", R.drawable.master));

        mClusterManagerPerson.addItem(new Person(new LatLng(21.042545, 105.778214), "Thánh Cường", R.drawable.cuong));
//
//        // http://www.flickr.com/photos/library_of_congress/2179915182/
//        mClusterManagerPerson.addItem(new Person(position(), "Mechanic", R.drawable.mechanic));
//
//        // http://www.flickr.com/photos/nationalmediamuseum/7893552556/
//        mClusterManagerPerson.addItem(new Person(position(), "Yeats", R.drawable.yeats));
//
//        // http://www.flickr.com/photos/sdasmarchives/5036231225/
//        mClusterManagerPerson.addItem(new Person(position(), "John", R.drawable.john));
//
//        // http://www.flickr.com/photos/anmm_thecommons/7694202096/
//        mClusterManagerPerson.addItem(new Person(position(), "Trevor the Turtle", R.drawable.turtle));
//
//        // http://www.flickr.com/photos/usnationalarchives/4726892651/
//        mClusterManagerPerson.addItem(new Person(position(), "Teach", R.drawable.teacher));
    }

    private LatLng position() {
        return new LatLng(lat, lng);
    }

    private LatLng positionRandom() {
        return new LatLng(random(lat, lng), random(0.148271, -0.3514683));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }
}
