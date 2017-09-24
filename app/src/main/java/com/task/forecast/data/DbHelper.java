package com.task.forecast.data;


import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.task.forecast.data.model.City;
import com.task.forecast.data.model.DayForecast;
import com.task.forecast.retrofit.model.ForecastResponse;
import com.task.forecast.retrofit.model.WeatherListing;

import java.util.Calendar;
import java.util.List;

public final class DbHelper {

    private DbHelper() {
        throw new AssertionError();
    }

    public static boolean isCityExist(String cityName) {

        List<City> cityList = new Select()
                .from(City.class)
                .execute();
        String name = cityName.trim().toLowerCase();

        for (City city : cityList) {
            if (city.name.trim().toLowerCase().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static long saveResponse(ForecastResponse forecastResponse, int daysCount) {

        City city = new City(forecastResponse.getCity().getName());
        city.save();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        WeatherListing listing;
        DayForecast forecast;

        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < daysCount; i++) {
                listing = forecastResponse.getList().get(i);

                forecast = new DayForecast(
                        city,
                        calendar.getTime().getTime(),
                        listing.getWeather().get(0).getDescription(),
                        Math.round(listing.getMain().getTempMin()),
                        Math.round(listing.getMain().getTempMax()),
                        listing.getWeather().get(0).getIcon()
                );
                forecast.save();
                calendar.add(Calendar.DATE, 1);
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

        return city.getId();
    }

    public static City getCity(long cityId) {
        return new Select()
                .from(City.class)
                .where("id = ?", cityId)
                .executeSingle();
    }

    public static List<City> getCities() {
        return new Select()
                .from(City.class)
                .execute();
    }

    public static void updateCity(ForecastResponse forecastResponse, int daysCount, City city) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        WeatherListing listing;
        DayForecast forecast;

        ActiveAndroid.beginTransaction();
        try {

            List<DayForecast> dayForecastList = city.dayForecastList();

            for (DayForecast dayForecast : dayForecastList) {
                dayForecast.delete();
            }

            for (int i = 0; i < daysCount; i++) {
                listing = forecastResponse.getList().get(i);

                forecast = new DayForecast(
                        city,
                        calendar.getTime().getTime(),
                        listing.getWeather().get(0).getDescription(),
                        Math.round(listing.getMain().getTempMin()),
                        Math.round(listing.getMain().getTempMax()),
                        listing.getWeather().get(0).getIcon()
                );
                forecast.save();
                calendar.add(Calendar.DATE, 1);
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}