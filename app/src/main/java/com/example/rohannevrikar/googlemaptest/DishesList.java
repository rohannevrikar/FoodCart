package com.example.rohannevrikar.googlemaptest;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DishesList extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Cursor cursor;
    private ImageView back;
    private TextView restaurantName;
    private Button btnCheckOut;
    private static final String TAG = "DishesList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dishes_list);
        databaseHelper = new DatabaseHelper(this);
        restaurantName = (TextView)findViewById(R.id.txtRestaurantName);
        back = (ImageView)findViewById(R.id.back);
        btnCheckOut = (Button)findViewById(R.id.btnCheckout);

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
        final ArrayList<DishInfo> cartItems = dishRecyclerAdapter.getCartItems();
        Log.d(TAG, "initializeDisplayContent: " + cartItems.toString());
        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DishesList.this, Cart.class);
                Bundle args = new Bundle();
                args.putSerializable("selectedItems",cartItems);
                intent.putExtra("bundle",args);
                startActivity(intent);

            }
        });

        


    }
}
