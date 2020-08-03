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

    public void addListItemToAdapter(List<Weather> list) {
        // thêm danh sách hiện tại vào list của data
        weatherList.addAll(list);
        //cập nhật lên UI
        this.notifyDataSetChanged();


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
        ViewHolder viewHolder = null;
        Weather weather = weatherList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_data_weather, null);
            viewHolder = new ViewHolder();
            viewHolder.tvDay = convertView.findViewById(R.id.tv_date);
            viewHolder.tvStatus = convertView.findViewById(R.id.tv_status);
            viewHolder.imgStatus = convertView.findViewById(R.id.img_status);
            viewHolder.tvMaxTemp = convertView.findViewById(R.id.tv_max_temp);
            viewHolder.tvMinTemp = convertView.findViewById(R.id.tv_min_temp);
            convertView.setTag(viewHolder);//chuyển vaofbooj nhớ đệm
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tvDay.setText(weather.getDay());
        viewHolder.tvStatus.setText(weather.getStatus());
        viewHolder.tvMaxTemp.setText(weather.getMaxTemp() + "°C");
        viewHolder.tvMinTemp.setText(weather.getMinTemp() + "°C");
        Picasso.get().load("http://openweathermap.org/img/wn/" + weather.getImage() + ".png").into(viewHolder.imgStatus);
        return convertView;
    }

    private class ViewHolder {
        TextView tvDay;
        TextView tvStatus;
        ImageView imgStatus;
        TextView tvMaxTemp;
        TextView tvMinTemp;

    }
}
