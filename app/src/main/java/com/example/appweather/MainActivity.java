package com.example.appweather;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.appweather.databinding.ActivityMainBinding;
import com.example.appweather.gpsmanagement.GPSLocation;
import com.example.appweather.helper.Helper;
import com.example.appweather.models.WeatherData;
import com.example.appweather.retrofit.ApiClient;
import com.example.appweather.retrofit.ApiInterface;
import com.squareup.picasso.Picasso;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public static final String KEY_FRAGMENT_LAT = "Lat";
    public static final String KEY_FRAGMENT_LNG = "Lng";
    public static final String KEY_MAPSACTIVITY_LAT = "Lat_Maps";
    public static final String KEY_MAPSACTIVITY_LNG = "Lng_Maps";
    public static final String KEY_MAPSACTIVITY_Address = "Address_Maps";
    public static final int REQUEST_CODE_MAPS = 101;
    private boolean FLAG_WEATHER_MAPS;

    private double lat;
    private double lng;
    private String currentAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (!checkRequiredPermissions()) checkRequiredPermissions();
        final GPSLocation gpsLocation = new GPSLocation(this);
        if (gpsLocation.canGetLocation) {
            gpsLocation.getLocation();
            setLat(gpsLocation.getLatitude());
            setLng(gpsLocation.getLongitude());
            List<Address> addresses = Helper.getAddress(this, getLat(), getLng());
            if (addresses.size() > 0) {
                setCurrentAddress(addresses.get(0).getAddressLine(0));
            } else {
                Toast.makeText(this, getString(R.string.Toat1), Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("BBBBBBB ", "Lỗi");
            System.out.println("Unable");
        }
        Log.d("Điểm ban đầu", getLat() + "" + getLng());
        Log.d("Flag", FLAG_WEATHER_MAPS + "");
        if (isFLAG_WEATHER_MAPS() == false) {
            getWeatherDataLocation(getLat(), getLng());
            setFLAG_WEATHER_MAPS(true);
            Log.d("Flag1", FLAG_WEATHER_MAPS + "");
        }

        binding.btnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.checkLatLng(getLat(), getLng()) == true) {
                    DetailFragment detailFragment = new DetailFragment();
                    Bundle bundle = new Bundle();
                    bundle.putDouble(KEY_FRAGMENT_LAT, getLat());
                    bundle.putDouble(KEY_FRAGMENT_LNG, getLng());
                    detailFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.main, detailFragment, DetailFragment.class.getName())
                            .commit();
                    getSupportFragmentManager().beginTransaction().show(detailFragment)
                            .addToBackStack(null).commit();

                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.Toat1), Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.btnMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Helper.checkLatLng(getLat(), getLng()) == true) {
                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble(KEY_MAPSACTIVITY_LAT, getLat());
                    bundle.putDouble(KEY_MAPSACTIVITY_LNG, getLng());
                    bundle.putString(KEY_MAPSACTIVITY_Address, currentAddress);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, REQUEST_CODE_MAPS);
                }

            }
        });
    }

    /**
     * xử lý dl được MapsActivity trả về
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_MAPS) {
            if (resultCode == RESULT_OK) {
                Bundle bundle = data.getExtras();
                setLat(bundle.getDouble(MapsActivity.EXTRA_DATA_LAT));
                setLng(bundle.getDouble(MapsActivity.EXTRA_DATA_Lng));
                setCurrentAddress(bundle.getString(MapsActivity.EXTRA_DATA_ADDRESS));
                Log.d("vị trí lấy từ maps", getLat() + " " + getLng());
                if (!isFLAG_WEATHER_MAPS()) {
                    getWeatherDataLocation(lat, lng);
                    binding.tvAddress.setText(getCurrentAddress());
                    Log.d("Flag2", FLAG_WEATHER_MAPS + "");
                }


            }

        }
    }

    public void getWeatherDataLocation(double lat, double lng) {
        String latLocation = String.valueOf(lat);
        String lngLocation = String.valueOf(lng);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<WeatherData> callback = apiInterface.getWeatherDataLocation(latLocation, lngLocation);
        callback.enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.code() == 404) {
                    Toast.makeText(MainActivity.this, getString(R.string.Toat1), Toast.LENGTH_SHORT).show();
                } else if (!(response.isSuccessful())) {
                    Toast.makeText(MainActivity.this, response.code(), Toast.LENGTH_LONG).show();
                }
                String descrition = response.body().getWeather().get(0).getDescription();
                if (descrition.equals("clear sky") || descrition.equals("few clouds")) {
                    binding.bgMain.setBackgroundResource(R.drawable.backggroung_hind);
                } else if (descrition.equals("shower rain") || descrition.equals("rain")
                        || descrition.equals("thunderstorm")) {
                    binding.bgMain.setBackgroundResource(R.drawable.rain_backgroung);
                } else if (descrition.equals("scattered clouds") || descrition.equals("broken clouds")) {
                    binding.bgMain.setBackgroundResource(R.drawable.may);
                } else {
                    binding.bgMain.setBackgroundResource(R.drawable.may);
                }
                binding.tvAddress.setText(currentAddress);
                Picasso.get().load("http://openweathermap.org/img/wn/" + response.body().getWeather().get(0).getIcon() + ".png").into(binding.imgIcon, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        binding.progressMain.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                int status = Helper.checkDescription(descrition);
                if (status != 0) {
                    binding.tvStatus.setText(getString(status));
                } else {
                    binding.tvStatus.setText(descrition);
                }

                binding.tvHumidity.setText(response.body().getMain().getHumidity() + "%");
                binding.tvTemp.setText(response.body().getMain().getTemp() + "°C");
                binding.tvWind.setText(response.body().getWind().getSpeed() + " m/s");
                binding.tvCloud.setText(response.body().getClouds().getAll() + "%");
                String time = Helper.unixTimeStampToDatime(response.body().getDt());
                String date = Helper.getDateOfWeek(response.body().getDt());
                binding.tvTime.setText(date + " " + time);

            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                //Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                //Log.d("TAG", t.getMessage());
            }
        });
    }

    public boolean isFLAG_WEATHER_MAPS() {
        return false;
    }

    public void setFLAG_WEATHER_MAPS(boolean FLAG_WEATHER_MAPS) {
        this.FLAG_WEATHER_MAPS = FLAG_WEATHER_MAPS;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
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
