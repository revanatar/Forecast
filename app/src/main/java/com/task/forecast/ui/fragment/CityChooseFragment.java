package com.task.forecast.ui.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.task.forecast.Constants;
import com.task.forecast.R;
import com.task.forecast.application.Forecast;
import com.task.forecast.data.DbHelper;
import com.task.forecast.retrofit.model.Error;
import com.task.forecast.retrofit.model.ForecastResponse;
import com.task.forecast.ui.activity.CityForecastActivity;
import com.task.forecast.ui.activity.MainActivity;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;


public class CityChooseFragment extends Fragment implements View.OnClickListener {

    private static final String UNITS = "metric";
    private static final int DAYS_COUNT = 7;

    private EditText etCity;
    private TextInputLayout etCityLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_city_choose, container, false);

        etCityLayout = rootView.findViewById(R.id.fragment_city_choose_input_layout);
        etCity = rootView.findViewById(R.id.fragment_city_choose_edit_text);
        rootView.findViewById(R.id.fragment_city_choose_btn_submit).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.fragment_city_choose_btn_submit:
                String city = etCity.getText().toString().trim();
                etCity.setText(city, TextView.BufferType.EDITABLE);
                etCity.setSelection(city.length());

                if (city.isEmpty()) {
                    etCityLayout.setError(getString(R.string.input_error_empty_city));
                    break;
                } else {
                    if (DbHelper.isCityExist(city)) {
                        showCityExistErrorDialog();
                        break;
                    }
                }
                getRemoteData(city);
                break;
        }
    }

    private void getRemoteData(final String city) {

        ((MainActivity) getActivity()).setProgressBarVisible(true);
        Forecast.getApi()
                .getWeatherForecast(city, UNITS, DAYS_COUNT, Constants.OPEN_WEATHER_MAP_API_KEY)
                .enqueue(new Callback<ForecastResponse>() {

                    @Override
                    public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                        ((MainActivity) getActivity()).setProgressBarVisible(false);
                        if (response.isSuccessful()) {
                            handleSuccessfulResponse(response.body());
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
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastResponse> call, Throwable t) {
                        ((MainActivity) getActivity()).setProgressBarVisible(false);
                        t.printStackTrace();
                        showConnectionErrorDialog();
                    }
                });
    }


    private void handleSuccessfulResponse(ForecastResponse forecastResponse) {

        String responseCity = forecastResponse.getCity().getName();
        boolean sameCity = etCity.getText().toString().toLowerCase()
                .equals(responseCity.trim().toLowerCase());

        if (!sameCity) {
            showCityConfirmDialog(responseCity, forecastResponse);
        } else {
            handleData(forecastResponse);
        }
    }


    private void handleData(ForecastResponse forecastResponse) {

        long cityId = DbHelper.saveResponse(forecastResponse, DAYS_COUNT);
        proceedToCityWeather(cityId);
    }

    private void showConnectionErrorDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setCancelable(false);
        alert.setTitle(R.string.alert_title_connection_error);
        alert.setMessage(R.string.alert_msg_connection_error);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void showCityConfirmDialog(String city, final ForecastResponse forecastResponse) {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setCancelable(false);
        alert.setTitle(R.string.dialog_title_city_confirm);
        alert.setMessage(
                getString(R.string.dialog_msg_city_confirm)
                        + "\n\n"
                        + city
        );

        alert.setPositiveButton(R.string.btn_title_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleData(forecastResponse);
            }
        });
        alert.setNegativeButton(R.string.btn_title_no, null);
        alert.show();
    }

    private void showCityExistErrorDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setCancelable(false);
        alert.setTitle(R.string.alert_title_city_exists);
        alert.setMessage(R.string.alert_msg_city_exists);

        alert.setPositiveButton(R.string.btn_title_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }


    private void proceedToCityWeather(long cityId) {

        Intent intent = new Intent(getActivity(), CityForecastActivity.class);
        intent.putExtra(Constants.CITY_ID_KEY, cityId);
        startActivity(intent);
    }
}
