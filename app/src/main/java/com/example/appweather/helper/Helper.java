package com.example.appweather.helper;

import android.os.Bundle;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
     * @param lat
     * @param lng
     * @return
     */
    public static boolean checkLatLng(double lat, double lng) {
        if (lat != 0 && lng != 0)
            return true;
        return false;
    }
}
