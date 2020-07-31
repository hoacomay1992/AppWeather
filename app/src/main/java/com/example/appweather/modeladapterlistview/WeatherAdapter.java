package com.example.appweather.modeladapterlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appweather.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WeatherAdapter extends BaseAdapter {
    private Context context;
    private List<Weather> weatherList;

    public WeatherAdapter(Context context, List<Weather> weatherList) {
        this.context = context;
        this.weatherList = weatherList;
    }

    @Override
    public int getCount() {
        return weatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return weatherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_data_weather, null);

        Weather weather = weatherList.get(position);
        TextView tvDay = convertView.findViewById(R.id.tv_date);
        TextView tvStatus = convertView.findViewById(R.id.tv_status);
        ImageView imgStatus = convertView.findViewById(R.id.img_status);
        TextView tvMaxTemp = convertView.findViewById(R.id.tv_max_temp);
        TextView tvMinTemp = convertView.findViewById(R.id.tv_min_temp);

        tvDay.setText(weather.getDay());
        tvStatus.setText(weather.getStatus());
        tvMaxTemp.setText(weather.getMaxTemp() + "°C");
        tvMinTemp.setText(weather.getMinTemp() + "°C");
        Picasso.get().load("http://openweathermap.org/img/wn/" + weather.getImage() + ".png").into(imgStatus);
        return convertView;
    }
}
