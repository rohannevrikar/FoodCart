package com.example.rohannevrikar.googlemaptest;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DishesList extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Cursor cursor;
    private ImageView back;
    private TextView restaurantName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dishes_list);
        databaseHelper = new DatabaseHelper(this);
        restaurantName = (TextView)findViewById(R.id.txtRestaurantName);
        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DishesList.this, RestaurantsList.class);
                startActivity(intent);
            }
        });
        restaurantName.setText(getIntent().getStringExtra("restaurantName"));
        initializeDisplayContent();
    }
    private void initializeDisplayContent(){
        RecyclerView dishesRecyclerView = (RecyclerView)findViewById(R.id.dishesList);
        LinearLayoutManager dishesLayoutManager = new LinearLayoutManager(this);
        dishesRecyclerView.setLayoutManager(dishesLayoutManager);
        ArrayList<DishInfo> dishesList = new ArrayList<>();
        cursor = databaseHelper.getDishes(getIntent().getIntExtra("restaurantId",0));
        if(cursor.moveToFirst()){
            do{
                DishInfo info = new DishInfo();
                info.setDishId(cursor.getInt(cursor.getColumnIndex("dish_id")));
                info.setDishName(cursor.getString(cursor.getColumnIndex("dishname")));
                info.setPrice(cursor.getString(cursor.getColumnIndex("price")));
                dishesList.add(info);
            }while (cursor.moveToNext());
        }
        DishRecyclerAdapter dishRecyclerAdapter = new DishRecyclerAdapter(this,dishesList);
        dishesRecyclerView.setAdapter(dishRecyclerAdapter);

    }
}
