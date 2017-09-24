package com.task.forecast.data.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

@Table(name = "Cities")
public class City extends Model {

    @Column(name = "Name")
    public String name;

    public City() {
        super();
    }

    public City(String name) {
        super();
        this.name = name;
    }

    public List<DayForecast> dayForecastList() {
        return getMany(DayForecast.class, "City");
    }
}
