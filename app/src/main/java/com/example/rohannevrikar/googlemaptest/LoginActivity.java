package com.example.rohannevrikar.googlemaptest;

import android.content.Intent;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;

import android.database.Cursor;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    private EditText password;
    private DatabaseHelper helper;

    private Cursor cursor;
    private  EditText email;
    private Button login;
    private TextView linkSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
//        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        helper = new DatabaseHelper(this);
       // helperRestaurant = new DatabaseHelperRestaurant(this);
        try {
            helper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            helper.openDataBase();
        } catch (SQLException sqle) {
            throw new SQLException("error while opening databse");
        }

        login = (Button) findViewById(R.id.btn_login);
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor = helper.getUser(email.getText().toString(),password.getText().toString());
                if(cursor.moveToFirst()){
                    Intent intent = new Intent(LoginActivity.this,MapsActivity.class);
                    startActivity(intent);
                }
                else
                    Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_SHORT).show();

            }
        });


        linkSignup = (TextView)findViewById(R.id.link_signup);
        linkSignup.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }
}

