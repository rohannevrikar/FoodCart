package com.example.rohannevrikar.googlemaptest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity {


    private EditText password;
    private DatabaseHelper helper;
    private boolean result;
    private  EditText email;
    private EditText name;
    private EditText phone;
    private String TAG="message";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Set up the login form.
//        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        name = (EditText)findViewById(R.id.name);
        phone = (EditText)findViewById(R.id.phone);
        helper = new DatabaseHelper(this);
        Button signup = (Button) findViewById(R.id.btn_signup);
        signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                result = helper.addUser(email.getText().toString(),password.getText().toString(),name.getText().toString(),phone.getText().toString());

                if(result){
                    Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                    email.setText("");
                    password.setText("");
                    name.setText("");
                    phone.setText("");
                    Toast.makeText(SignUpActivity.this,"Something went wrong, please try again",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}

