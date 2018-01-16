package com.example.rohannevrikar.googlemaptest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Cart extends AppCompatActivity {
    private static final String TAG = "Cart";
    private int totalAmount = 0;
    private Button btnDone;
    private ImageView back;
    TextView amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("bundle");
        ArrayList<DishInfo> cartItems = (ArrayList<DishInfo>) args.getSerializable("selectedItems");
        Log.d(TAG, "Size: " + cartItems.size()  );
        for(DishInfo c : cartItems){
            totalAmount += Integer.parseInt(c.getPrice());
        }
        ListAdapter listAdapter = new CartAdapter(this, cartItems);
        ListView listCart = (ListView)findViewById(R.id.listCart);
        listCart.setAdapter(listAdapter);
        amount = (TextView)findViewById(R.id.txtBillAmount);
        amount.setText(Integer.toString(totalAmount));
        btnDone = (Button)findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(Cart.this, DeliveryAddress.class);
                startActivity(startIntent);


            }
        });
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(Cart.this, DishesList.class);
                startActivity(startIntent);


            }
        });


    }
}
