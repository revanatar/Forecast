package com.task.forecast.application;

import com.task.forecast.retrofit.api.WeatherForecastApi;

import java.util.Calendar;
import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Forecast extends com.activeandroid.app.Application {

    private static WeatherForecastApi weatherForecastApi;
    private static Retrofit retrofit;

    public static WeatherForecastApi getApi() {
        return weatherForecastApi;
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherForecastApi = retrofit.create(WeatherForecastApi.class);

//        Configuration dbConfiguration = new Configuration.Builder(this)
//                .setDatabaseName("forecast.db")
//                .create();
//        ActiveAndroid.initialize(dbConfiguration);
    }
}
