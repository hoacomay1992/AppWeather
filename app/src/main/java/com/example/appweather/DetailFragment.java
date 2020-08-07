package com.example.appweather;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

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

    public Handler mHandler;
    public View ftView;
    public boolean isLoading = false;
    public int currentId = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.detail_fragment, container, false);
        ftView = inflater.inflate(R.layout.footer_view, null);
        mHandler = new Handler();
        weatherList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(getActivity(), weatherList);
        Bundle bundle = getArguments();
        if (bundle != null) {
            lat = bundle.getDouble(MainActivity.KEY_FRAGMENT_LAT);
            lng = bundle.getDouble(MainActivity.KEY_FRAGMENT_LNG);
            Log.d("Địa điểm fragment", lat + "" + lng);
            binding.lvWeather.setAdapter(weatherAdapter);
            getList7DayData(lat, lng);
        }
        binding.lvWeather.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getLastVisiblePosition() == totalItemCount - 1 && binding.lvWeather.getCount() >= 4 && isLoading == false) {
                    isLoading = true;
                    Thread thread = new ThreadGetMoreData();
                    thread.start();
                }
            }
        });
        return binding.getRoot();
    }

    private void getList7DayData(double lat, double lng) {
        String latLocation = String.valueOf(lat);
        String lngLocation = String.valueOf(lng);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Weather7Data> callback = apiInterface.get7WeatherDataLocation(latLocation, lngLocation);
        callback.enqueue(new Callback<Weather7Data>() {
            @Override
            public void onResponse(Call<Weather7Data> call, Response<Weather7Data> response) {
                java.util.List<List> listData = response.body().getList();
                for (int i = 0; i < listData.size(); i++) {
                    String time = Helper.unixTimeStampToDatime(listData.get(i).getDt());
                    String date = Helper.getDateOfWeek(listData.get(i).getDt());
                    String dateTime = date;

                    String maxTemp = String.valueOf(listData.get(i).getTemp().getMax());
                    String minTemp = String.valueOf(listData.get(i).getTemp().getMin());

                    String description = listData.get(i).getWeather().get(0).getDescription();
                    String icon = listData.get(i).getWeather().get(0).getIcon();
                    weatherList.add(new Weather(dateTime, description, icon, maxTemp, minTemp));
                }
                weatherAdapter.notifyDataSetChanged();
                binding.progressFragment.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Weather7Data> call, Throwable t) {

            }
        });
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    //thêm loading view trong khi search processing
                    binding.lvWeather.addFooterView(ftView);
                    break;
                case 1:
                    //cập nhật data adapter trong UI
                    weatherAdapter.addListItemToAdapter((java.util.List<Weather>) msg.obj);
                    //remove load view sau khi đã cập nhật listView
                    binding.lvWeather.removeFooterView(ftView);
                    isLoading = false;
                    break;
                default:
                    break;

            }

        }
    }

    private ArrayList<Weather> getMoreData() {
        ArrayList<Weather> list = new ArrayList<>();
        return list;
    }

    public class ThreadGetMoreData extends Thread {
        @Override
        public void run() {
            //thêm footer view sau khi get data
            mHandler.sendEmptyMessage(0);
            //search more data
            ArrayList<Weather> arrayList = getMoreData();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //gửi kết quả cho Handle
            Message msg = mHandler.obtainMessage(1, arrayList);
            mHandler.sendMessage(msg);

        }
    }
}
