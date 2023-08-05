package com.example.nasadailyimage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class About extends Welcome {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        handleCommonComponents();
    }
}