package com.example.appweather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.appweather.databinding.DetailFragmentBinding;
import com.example.appweather.helper.Helper;
import com.example.appweather.model7day.List;
import com.example.appweather.model7day.Weather7Data;
import com.example.appweather.modeladapterlistview.Weather;
import com.example.appweather.modeladapterlistview.WeatherAdapter;
import com.example.appweather.models.WeatherData;
import com.example.appweather.retrofit.ApiClient;
import com.example.appweather.retrofit.ApiInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailFragment extends Fragment {
    private DetailFragmentBinding binding;
    WeatherAdapter weatherAdapter;
    java.util.List<Weather> weatherList;
    private double lat;
    private double lng;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.detail_fragment, container, false);
        weatherList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(getActivity(), weatherList);
        Bundle bundle = getArguments();

        if (bundle != null) {
            double[] listLatLng = bundle.getDoubleArray(MainActivity.KEY_FRAGMENT_LAT_LNG);
            for (int i = 0; i < listLatLng.length; i++) {
                lat = listLatLng[0];
                lng = listLatLng[1];
            }
            binding.lvWeather.setAdapter(weatherAdapter);
            getList7DayData(lat, lng);
        }
        return binding.getRoot();
    }

    private void getList7DayData(double lat, double lng) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Weather7Data> callback = apiInterface.get7WeatherDataLocation(lat, lng);
        callback.enqueue(new Callback<Weather7Data>() {
            @Override
            public void onResponse(Call<Weather7Data> call, Response<Weather7Data> response) {
                java.util.List<List> listData = response.body().getList();
                for (int i = 0; i < listData.size(); i++) {
                    String time = Helper.unixTimeStampToDatime(listData.get(i).getDt());
                    String date = Helper.getDateOfWeek(listData.get(i).getDt());
                    String dateTime = date + " " + time;

                    String maxTemp = String.valueOf(listData.get(i).getTemp().getMax());
                    String minTemp = String.valueOf(listData.get(i).getTemp().getMin());

                    String description = listData.get(i).getWeather().get(0).getDescription();
                    String icon = listData.get(i).getWeather().get(0).getIcon();
                    weatherList.add(new Weather(dateTime, description, icon, maxTemp, minTemp));
                }
                weatherAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Weather7Data> call, Throwable t) {

            }
        });
    }

    private void getListDayData(String name) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Weather7Data> callback = apiInterface.get7WeatherData(name);
        callback.enqueue(new Callback<Weather7Data>() {
            @Override
            public void onResponse(Call<Weather7Data> call, Response<Weather7Data> response) {
                java.util.List<List> listData = response.body().getList();
                for (int i = 0; i < listData.size(); i++) {
//                    int dt = response.body().getList().get(i).getDt();
//                    String time = Helper.getTime(dt);

                    String maxTemp = String.valueOf(listData.get(i).getTemp().getMax());
                    String minTemp = String.valueOf(listData.get(i).getTemp().getMin());

                    String description = listData.get(i).getWeather().get(0).getDescription();
                    String icon = listData.get(i).getWeather().get(0).getIcon();
                    //weatherList.add(new Weather(time, description, icon, maxTemp, minTemp));
                }
                weatherAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Weather7Data> call, Throwable t) {

            }
        });
    }
}
