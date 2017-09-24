package com.task.forecast.retrofit.api;

import com.task.forecast.retrofit.model.ForecastResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface WeatherForecastApi {

    @GET("data/2.5/forecast")
    Call<ForecastResponse> getWeatherForecast(@Query("q") String city,
                                              @Query("units") String units,
                                              @Query("cnt") int daysCount,
                                              @Query("appid") String apiKey);
}