package com.example.appweather.helper;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

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
}
