package com.example.rohannevrikar.googlemaptest;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsList extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private Cursor cursor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);
        initializeDisplayContent();
    }
    private void initializeDisplayContent(){
        dbHelper = new DatabaseHelper(this);
        RecyclerView restaurantsRecyclerView = (RecyclerView)findViewById(R.id.restaurantsList);
        LinearLayoutManager restaurantsLayoutManager = new LinearLayoutManager(this);
        restaurantsRecyclerView.setLayoutManager(restaurantsLayoutManager);
        List<RestaurantInfo> listRestaurants = new ArrayList<>();
        cursor = dbHelper.getRestaurants();
        if(cursor.moveToFirst()){
            do{
                RestaurantInfo info = new RestaurantInfo();
                info.setRestaurantName(cursor.getString(cursor.getColumnIndex("name")));
                info.setCuisine(cursor.getString(cursor.getColumnIndex("cuisine")));
                info.setDeliveryTime(cursor.getString(cursor.getColumnIndex("deliverytime")));
                info.setPriceForTwo(cursor.getString(cursor.getColumnIndex("pricefortwo")));
                listRestaurants.add(info);
            }while (cursor.moveToNext());
        }
        RestaurantRecyclerAdapter restaurantRecyclerAdapter = new RestaurantRecyclerAdapter(this,listRestaurants);
        restaurantsRecyclerView.setAdapter(restaurantRecyclerAdapter);


    }
}
