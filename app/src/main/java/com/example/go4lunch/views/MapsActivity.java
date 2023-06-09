package com.example.go4lunch.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.go4lunch.R;

public class MapsActivity extends AppCompatActivity {
    private TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        name = findViewById(R.id.outputName_tv);

        String userName = getIntent().getExtras().getString("name");

        name.setText(userName);
    }
}