package com.example.dkolosovskiy.payapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

public class Result extends AppCompatActivity {
    Button b;
    TextView result;
    Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
        final Activity act = this;
        Intent intent = getIntent();
        /**Show message */
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String text = (String) msg.obj;
                Logger.lg("text " + text);
                result.setText(text);
            }
        };
        /**ini*/
        b = findViewById(R.id.button2);
        result = findViewById(R.id.textView3);
        Message msg = new Message();
        msg.obj = intent.getStringExtra("Result");
        handler.sendMessage(msg);
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Result.this, SimChooser.class);
                startActivity(intent);
            }
        });
    }
}
