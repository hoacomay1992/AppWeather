package com.example.appweather.retrofit;

import com.example.appweather.model7day.Weather7Data;
import com.example.appweather.models.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("weather?&units=metric&appid=211ff006de9aba9ddd122331f87cdf8b")
    Call<WeatherData> getWeatherData(@Query("q") String name);

    @GET("forecast/daily?&units=metric&cnt=7&appid=211ff006de9aba9ddd122331f87cdf8b")
    Call<Weather7Data> get7WeatherData(@Query("q") String name);

    @GET("weather?&units=metric&appid=211ff006de9aba9ddd122331f87cdf8b")
    Call<WeatherData> getWeatherDataLocation(@Query("lat") double lat,
                                             @Query("lon") double lng);

    @GET("forecast/daily?&units=metric&appid=211ff006de9aba9ddd122331f87cdf8b")
    Call<Weather7Data> get7WeatherDataLocation(@Query("lat") double lat,
                                               @Query("lon") double lng);
}
