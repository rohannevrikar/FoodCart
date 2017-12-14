package com.example.rohannevrikar.googlemaptest;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsList extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private Cursor cursor;
    private ImageView backArrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants_list);
        backArrow = (ImageView)findViewById(R.id.back);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RestaurantsList.this,MapsActivity.class);
                startActivity(intent);
            }
        });
        initializeDisplayContent();
    }
    private void initializeDisplayContent(){
        dbHelper = new DatabaseHelper(this);
        RecyclerView restaurantsRecyclerView = (RecyclerView)findViewById(R.id.restaurantsList);
        LinearLayoutManager restaurantsLayoutManager = new LinearLayoutManager(this);
        restaurantsRecyclerView.setLayoutManager(restaurantsLayoutManager);
        ArrayList<RestaurantInfo> listRestaurants = new ArrayList<>();
        cursor = dbHelper.getRestaurants();
        if(cursor.moveToFirst()){
            do{
                RestaurantInfo info = new RestaurantInfo();
                info.setId(cursor.getInt(cursor.getColumnIndex("id")));
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
