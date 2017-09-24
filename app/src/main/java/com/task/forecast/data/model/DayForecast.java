package com.task.forecast.data.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "DayForecast")
public class DayForecast extends Model {

    @Column(name = "City")
    public City city;

    @Column(name = "Description")
    public String description;

    @Column(name = "TempHigh")
    public int tempHigh;

    @Column(name = "TempLow")
    public int tempLow;

    @Column(name = "IconId")
    public String iconId;

    @Column(name = "Date")
    public long date;

    public DayForecast() {
        super();
    }

    public DayForecast(City city,
                       long date,
                       String description,
                       int tempLow,
                       int tempHigh,
                       String iconId
    ) {
        this.city = city;
        this.date = date;
        this.description = description;
        this.tempLow = tempLow;
        this.tempHigh = tempHigh;
        this.iconId = iconId;

    }
}
