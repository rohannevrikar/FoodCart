package com.example.rohannevrikar.googlemaptest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;

import android.database.Cursor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    private EditText password;
    private DatabaseHelper helper;
    private TextView token;
    private BroadcastReceiver receiver;
    private Cursor cursor;
    private  EditText email;
    private Button login;
    private TextView linkSignup;
    CallbackManager callbackManager;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        token = (TextView) findViewById(R.id.txtToken);
        if(isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
            startActivity(intent);
        }

        // Set up the login form.
//        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                token.setText(SharedPrefManager.getInstance(LoginActivity.this).getToken());
                Log.d("Broadcast", "onReceive: Code here");
            }
        };
        registerReceiver(receiver, new IntentFilter(MyFirebaseInstanceIdService.TOKEN_BROADCAST));
        if(SharedPrefManager.getInstance(this).getToken()!=null){
            token.setText(SharedPrefManager.getInstance(this).getToken());
        }
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        helper = new DatabaseHelper(this);
        Log.d("SharedPrefToken", "onCreate: " + SharedPrefManager.getInstance(this).getToken() );
        Log.d("OnApplicationBroadcast", "onCreate: " + getApplicationContext().toString());

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
                cursor = helper.getUser(email.getText().toString(), password.getText().toString());
                if (cursor.moveToFirst()) {
                    sendTokenToServer(email.getText().toString());
                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                    startActivity(intent);
                } else
                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();


            }
        });


        linkSignup = (TextView) findViewById(R.id.link_signup);
        linkSignup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        callbackManager = CallbackManager.Factory.create();

            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            String userId = loginResult.getAccessToken().getUserId();
                            GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    displayUserInfo(object);
                                }
                            });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "first_name, last_name, email, id");
                            graphRequest.setParameters(parameters);
                            graphRequest.executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException error) {

                        }


                    });
        }




    public void displayUserInfo(JSONObject object){
        String first_name="";
        String last_name="";
        String email="";
        String id="";
        try {
            first_name = object.getString("first_name");
            last_name = object.getString("last_name");
            email = object.getString("email");
            id = object.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
        intent. putExtra("first_name",first_name);
        intent. putExtra("last_name",last_name);
        intent. putExtra("email",email);
        intent. putExtra("id",id);
        startActivity(intent);


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void sendTokenToServer(String email){
        if(SharedPrefManager.getInstance(this).getToken()!=null){
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    "",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);


        }else
            Toast.makeText(this, "Token not generated", Toast.LENGTH_SHORT).show();
    }
    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }



}


