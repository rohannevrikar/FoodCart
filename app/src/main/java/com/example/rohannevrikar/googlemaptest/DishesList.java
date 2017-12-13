package com.example.rohannevrikar.googlemaptest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DishesList extends AppCompatActivity {
    private TextView restaurantName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dishes_list);
        restaurantName = (TextView)findViewById(R.id.txtRestaurantName);
        restaurantName.setText(getIntent().getStringExtra("restaurantName"));
    }
}
