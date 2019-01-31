package com.example.dkolosovskiy.payapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    private Button btnEasy;
    private Button btnDev;


    /***/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_layout);
        btnEasy = findViewById(R.id.buttonTest);
        btnDev = findViewById(R.id.buttonDev);
        btnEasy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DevMain.class);
                startActivity(intent);
            }
        });
        btnDev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PermissionsChecker.class);
                startActivity(intent);
            }
        });
    }
}
