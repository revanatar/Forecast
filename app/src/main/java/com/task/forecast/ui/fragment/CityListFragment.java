package com.task.forecast.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.task.forecast.Constants;
import com.task.forecast.R;
import com.task.forecast.data.DbHelper;
import com.task.forecast.data.model.City;
import com.task.forecast.ui.activity.CityForecastActivity;
import com.task.forecast.ui.adapter.CityAdapter;

import java.util.List;

public class CityListFragment extends Fragment implements CityAdapter.OnItemClickListener {

    private List<City> cities;
    private RecyclerView rvCity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cities = DbHelper.getCities();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_city_list, container, false);
        initRecyclerView(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        cities = DbHelper.getCities();
        ((CityAdapter) rvCity.getAdapter()).refreshData(cities);
    }

    @Override
    public void onCityItemClick(City item) {
        proceedToCityWeather(item.getId());
    }

    private void initRecyclerView(View rootView) {

        rvCity = rootView.findViewById(R.id.fragment_city_list_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(rvCity.getContext(), DividerItemDecoration.VERTICAL);

        rvCity.setLayoutManager(layoutManager);
        rvCity.setHasFixedSize(true);
        rvCity.addItemDecoration(dividerItemDecoration);

        CityAdapter adapter = new CityAdapter(getActivity(), cities, this);
        rvCity.setAdapter(adapter);
    }

    private void proceedToCityWeather(long cityId) {
        Intent intent = new Intent(getActivity(), CityForecastActivity.class);
        intent.putExtra(Constants.CITY_ID_KEY, cityId);
        startActivity(intent);
    }
}
