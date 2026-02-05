package com.example.testaitool;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TestActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("这是 TestActivity2");
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20f);
        setContentView(tv);
    }
}
