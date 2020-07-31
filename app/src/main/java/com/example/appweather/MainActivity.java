package com.example.appweather;

import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.appweather.databinding.ActivityMainBinding;
import com.example.appweather.gpsmanagement.GPSLocation;
import com.example.appweather.helper.Helper;
import com.example.appweather.models.WeatherData;
import com.example.appweather.retrofit.ApiClient;
import com.example.appweather.retrofit.ApiInterface;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public static final String KEY_FRAGMENT_LAT_LNG = "Lat_Lng";
    private double lat;
    private double lng;
    private String currentCountryName = "";
    private String currentAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (!checkRequiredPermissions()) checkRequiredPermissions();
        final GPSLocation gpsLocation = new GPSLocation(this);
        if (gpsLocation.canGetLocation) {
            gpsLocation.getLocation();
            lat = gpsLocation.getLatitude();
            lng = gpsLocation.getLongitude();
            try {
                Geocoder gcd = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = null;
                addresses = gcd.getFromLocation(lat, lng, 1);
                if (addresses.size() > 0) {
                    currentCountryName = addresses.get(0).getCountryName();
                    currentAddress = addresses.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.d("BBBBBBB ", "Lỗi");
            System.out.println("Unable");
        }

        getWeatherDataLocation(lat, lng);
        binding.btnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.checkLatLng(lat, lng) == true) {
                    DetailFragment detailFragment = new DetailFragment();
                    Bundle bundle = new Bundle();
                    double[] listlatLng = {lat, lng};
                    bundle.putDoubleArray(KEY_FRAGMENT_LAT_LNG, listlatLng);
                    detailFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.main, detailFragment, DetailFragment.class.getName())
                            .commit();
                    getSupportFragmentManager().beginTransaction().show(detailFragment)
                            .addToBackStack(null).commit();

                } else {
                    Toast.makeText(MainActivity.this, "không tồn tại vị trí", Toast.LENGTH_SHORT).show();
                }

            }
        });

//        binding.btnChangeActivity.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                curentCity = binding.edtSearch.getText().toString().trim();
////                if (curentCity.equals("")) {
////                    curentCity = "hanoi";
////                }
////                DetailFragment detailFragment = new DetailFragment();
////                Bundle bundle = new Bundle();
////                bundle.putString(KEY_BUNDLE_FRAGMENT, curentCity);
////                detailFragment.setArguments(bundle);
////                getSupportFragmentManager().beginTransaction().add(R.id.main, detailFragment, DetailFragment.class.getName())
////                        .commit();
////                getSupportFragmentManager().beginTransaction().show(detailFragment)
////                        .addToBackStack(null).commit();
////            }
////        });

        binding.btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapsFragment mapsFragment = new MapsFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.main, mapsFragment, MapsFragment.class.getName())
                        .commit();
                getSupportFragmentManager().beginTransaction().show(mapsFragment).addToBackStack(null).commit();
            }
        });
    }

    public void getWeatherDataLocation(double lat, double lng) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<WeatherData> callback = apiInterface.getWeatherDataLocation(lat, lng);
        callback.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                binding.tvCountry.setText(currentCountryName);
                binding.tvAddress.setText(currentAddress);
                Picasso.get().load("http://openweathermap.org/img/wn/" + response.body().getWeather().get(0).getIcon() + ".png").into(binding.imgIcon);
                binding.tvHumidity.setText(response.body().getMain().getHumidity() + "%");
                binding.tvTemp.setText(response.body().getMain().getTemp() + "°C");
                binding.tvStatus.setText(response.body().getWeather().get(0).getDescription());
                binding.tvWind.setText(response.body().getWind().getSpeed() + " m/s");
                binding.tvCloud.setText(response.body().getClouds().getAll() + "%");
                String time = Helper.unixTimeStampToDatime(response.body().getDt());
                String date = Helper.getDateOfWeek(response.body().getDt());
                binding.tvTime.setText(date + " " + time);

            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {

            }
        });
    }

    public void getWeatherData(String name) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<WeatherData> callback = apiInterface.getWeatherData(name);
        callback.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                binding.tvAddress.setText(response.body().getName());
                binding.tvCountry.setText(response.body().getSys().getCountry());
                binding.tvHumidity.setText(response.body().getMain().getHumidity().toString() + "%");
                Picasso.get().load("http://openweathermap.org/img/wn/" + response.body().getWeather().get(0).getIcon() + ".png").into(binding.imgIcon);
                binding.tvStatus.setText(response.body().getWeather().get(0).getMain());
                binding.tvTemp.setText(response.body().getMain().getTemp().toString() + "°C");
                binding.tvWind.setText(response.body().getWind().getSpeed().toString() + "m/s");
                binding.tvCloud.setText(response.body().getClouds().getAll().toString() + "%");

            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {

            }
        });
    }

    private boolean checkRequiredPermissions() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.WAKE_LOCK};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.message_request_permission_read_phone_state),
                    20000, perms);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
