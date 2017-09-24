package com.task.forecast.ui.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.task.forecast.Constants;
import com.task.forecast.R;
import com.task.forecast.application.Forecast;
import com.task.forecast.data.DbHelper;
import com.task.forecast.data.model.City;
import com.task.forecast.data.model.DayForecast;
import com.task.forecast.retrofit.model.Error;
import com.task.forecast.retrofit.model.ForecastResponse;
import com.task.forecast.ui.adapter.DayForecastAdapter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Calendar;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

public class CityForecastActivity extends AppCompatActivity {

    private static final String UNITS = "metric";
    private static final int DAYS_COUNT = 7;

    private City city;
    private DayForecastAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long cityId = getIntent().getLongExtra(Constants.CITY_ID_KEY, -1);
        city = DbHelper.getCity(cityId);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        setTitle(city.name);
        setContentView(R.layout.activity_cityforecast);

        initRecyclerView();

        if (city.dayForecastList().get(0).date < calendar.getTime().getTime()
                && isOnline()) {
            updateData();
        }
    }

    private void updateData() {
        Forecast.getApi()
                .getWeatherForecast(city.name, UNITS, DAYS_COUNT, Constants.OPEN_WEATHER_MAP_API_KEY)
                .enqueue(new Callback<ForecastResponse>() {

                    @Override
                    public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                        if (response.isSuccessful()) {
                            DbHelper.updateCity(response.body(), DAYS_COUNT, city);
                            adapter.refreshData(city.dayForecastList());
                        } else {
                            Converter<ResponseBody, Error> converter =
                                    Forecast.getRetrofit()
                                            .responseBodyConverter(Error.class, new Annotation[0]);

                            Error error;
                            try {
                                error = converter.convert(response.errorBody());
                            } catch (IOException e) {
                                error = new Error();
                                error.setMessage(getString(R.string.toast_error_unknown));
                            }
                            Toast.makeText(CityForecastActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastResponse> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    private void initRecyclerView() {

        RecyclerView rvDayForecast;
        rvDayForecast = (RecyclerView) findViewById(R.id.activity_city_forecast_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(rvDayForecast.getContext(), DividerItemDecoration.VERTICAL);

        rvDayForecast.setLayoutManager(layoutManager);
        rvDayForecast.setHasFixedSize(true);
        rvDayForecast.addItemDecoration(dividerItemDecoration);

        List<DayForecast> dayForecastList = city.dayForecastList();

        adapter = new DayForecastAdapter(this, dayForecastList);
        rvDayForecast.setAdapter(adapter);
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
