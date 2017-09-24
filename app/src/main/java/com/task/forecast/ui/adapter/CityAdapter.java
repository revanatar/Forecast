package com.task.forecast.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.task.forecast.R;
import com.task.forecast.data.model.City;

import java.util.List;


public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ItemHolder> {

    private List<City> cities;
    private OnItemClickListener listener;
    private Context context;

    public CityAdapter(@NonNull Context context, @NonNull List<City> cities, OnItemClickListener listener) {
        this.context = context;
        this.cities = cities;
        this.listener = listener;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_city, parent, false);

        return new ItemHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {

        City item = cities.get(position);

        holder.tvCityName.setText(item.name);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public void refreshData(List<City> cities) {
        this.cities = cities;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {

        void onCityItemClick(City item);
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        TextView tvCityName;

        ItemHolder(final View view) {
            super(view);
            tvCityName = view.findViewById(R.id.item_city_name);
        }

        public void bind(final City city,
                         final OnItemClickListener listener) {

            super.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCityItemClick(city);
                }
            });
        }
    }

}
