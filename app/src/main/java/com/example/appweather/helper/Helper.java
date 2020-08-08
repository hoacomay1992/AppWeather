package com.example.appweather.helper;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.example.appweather.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Helper {

    /**
     * The method returns the current time
     *
     * @param unixTimeSramp
     * @return
     */
    public static String unixTimeStampToDatime(double unixTimeSramp) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        date.setTime((long) (unixTimeSramp * 1000));
        return dateFormat.format(date);
    }

    /**
     * method returns date of the week
     */
    public static String getDateOfWeek(double unixTimeSramp) {
        DateFormat dateFormat = new SimpleDateFormat("EEEE");
        Date date = new Date();
        date.setTime((long) (unixTimeSramp * 1000));
        return dateFormat.format(date);
    }

    /**
     * method to get current time
     *
     * @return
     */
    public static String getDateNow() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm");
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * The test for lat lng status does not exist
     *
     * @param lat
     * @param lng
     * @return
     */
    public static boolean checkLatLng(double lat, double lng) {
        if (lat != 0 && lng != 0)
            return true;
        return false;
    }

    public static List<Address> getAddress(Context context, double lat, double lng) {
        try {
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            addresses = gcd.getFromLocation(lat, lng, 1);
            if (addresses.size() > 0) {
                return addresses;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void addMakerLocation(GoogleMap mMap, double lat, double lng, String nameAddress, String spneetAddress) {
        LatLng latLng = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        markerOptions.title(nameAddress).snippet(spneetAddress);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        mMap.addMarker(markerOptions);
    }

    public static int checkDescription(String descrition) {
        if (descrition.equals("clear sky")) {
            return R.string.clear_sky;
        } else if (descrition.equals("few clouds")) {
            return R.string.few_clouds;
        } else if (descrition.equals("scattered clouds")) {
            return R.string.scattered_clouds;
        } else if (descrition.equals("broken clouds")) {
            return R.string.broken_clouds;
        } else if (descrition.equals("shower rain")) {
            return R.string.shower_rain;
        } else if (descrition.equals("rain")) {
            return R.string.rain;
        } else if (descrition.equals("thunderstorm")) {
            return R.string.thunderstorm;
        } else if (descrition.equals("snow")) {
            return R.string.snow;
        } else if (descrition.equals("mist")) {
            return R.string.mist;
        } else if (descrition.equals("light rain")) {
            return R.string.light_rain;
        } else if (descrition.equals("sky is clear")) {
            return R.string.sky_is_clear;
        } else if (descrition.equals("moderate rain")) {
            return R.string.moderate_rain;
        } else if (descrition.equals("heavy intensity rain")) {
            return R.string.heavy_intensity_rain;
        } else if (descrition.equals("overcast clouds")) {
            return R.string.overcast_clouds;
        } else {
            return 0;
        }
    }
}
