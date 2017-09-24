package com.task.forecast.ui.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.task.forecast.Constants;
import com.task.forecast.DownloadImageTask;
import com.task.forecast.R;
import com.task.forecast.data.model.DayForecast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DayForecastAdapter extends RecyclerView.Adapter<DayForecastAdapter.ItemHolder> {

    private List<DayForecast> dayForecastList;
    private Context context;

    public DayForecastAdapter(@NonNull Context context, @NonNull List<DayForecast> cities) {
        this.context = context;
        this.dayForecastList = cities;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_forecast, parent, false);

        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        DayForecast item = dayForecastList.get(position);

        String iconDir = context.getFilesDir().getAbsolutePath();
        String iconName = item.iconId + ".png";

        File icon = new File(iconDir + "/" + iconName);
        if (!icon.exists()) {
            if (isOnline()) {
                new DownloadImageTask(holder.ivIcon, context, iconName)
                        .execute(Constants.WEATHER_ICONS_URL + iconName);
            }
        } else {
            Picasso.with(context).load(icon).into(holder.ivIcon);
        }

        holder.tvDate.setText(getFormattedDate(new java.sql.Date(item.date)));
        holder.tvDesc.setText(item.description);
        holder.tvTempLow.setText(String.valueOf(item.tempLow));
        holder.tvTempHigh.setText(String.valueOf(item.tempHigh));
    }

    @Override
    public int getItemCount() {
        return dayForecastList.size();
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void refreshData(List<DayForecast> dayForecastList) {
        this.dayForecastList = dayForecastList;
        notifyDataSetChanged();
    }

    private String getFormattedDate(Date date) {

        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
        } else {
            // noinspection deprecation
            locale = context.getResources().getConfiguration().locale;
        }

        SimpleDateFormat myFormat = new SimpleDateFormat("dd.MM.yy", locale);

        return myFormat.format(date);
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        TextView tvDate;
        TextView tvDesc;
        TextView tvTempLow;
        TextView tvTempHigh;

        ItemHolder(final View view) {
            super(view);
            ivIcon = view.findViewById(R.id.item_day_forecast_icon);
            tvDate = view.findViewById(R.id.item_day_forecast_date);
            tvDesc = view.findViewById(R.id.item_day_forecast_desc);
            tvTempLow = view.findViewById(R.id.item_day_forecast_temp_low);
            tvTempHigh = view.findViewById(R.id.item_day_forecast_temp_high);
        }
    }

}
