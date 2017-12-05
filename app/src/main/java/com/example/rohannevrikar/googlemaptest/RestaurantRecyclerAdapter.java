package com.example.rohannevrikar.googlemaptest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Rohan Nevrikar on 03-12-2017.
 */

public class RestaurantRecyclerAdapter extends RecyclerView.Adapter<RestaurantRecyclerAdapter.ViewHolder> {
    private final Context mContext;
    private final List<RestaurantInfo> listRestaurants;
    private final LayoutInflater layoutInflater;
    private DatabaseHelper dbHelper;

    public RestaurantRecyclerAdapter(Context mContext, List listRestaurants) {
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
        this.listRestaurants = listRestaurants;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.activity_restaurant_item, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.restaurantName.setText(listRestaurants.get(position).getRestaurantName());
        holder.cuisine.setText(listRestaurants.get(position).getCuisine());
        holder.deliveryTime.setText(listRestaurants.get(position).getDeliveryTime() + " MINS");
        holder.priceForTwo.setText(listRestaurants.get(position).getPriceForTwo() + " FOR TWO");

    }

    @Override
    public int getItemCount() {
        return listRestaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView restaurantName;
        public final TextView cuisine;
        public final TextView deliveryTime;
        public final TextView priceForTwo;

        public ViewHolder(View itemView) {
            super(itemView);
            restaurantName = (TextView)itemView.findViewById(R.id.txtRestaurantName);
            cuisine = (TextView)itemView.findViewById(R.id.txtCuisine);
            deliveryTime = (TextView)itemView.findViewById(R.id.txtDeliveryTime);
            priceForTwo = (TextView)itemView.findViewById(R.id.txtPrice);
        }
    }
}
