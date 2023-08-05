package com.example.nasadailyimage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class PhotoInfo extends Welcome {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        handleCommonComponents();
        //receive data from main activity
        Bundle data = getIntent().getExtras();
        //pass data to detail fragment
        DetailsFragment detailsFragment = new DetailsFragment();
        detailsFragment.setArguments(data);
        //replace empty view with fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.cResult, detailsFragment)
                .commit();
    }
}