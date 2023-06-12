package com.example.go4lunch.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.go4lunch.R;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MapsActivity extends AppCompatActivity {
    private TextView name;
    private BottomNavigationView menu;
    private MapViewFragment mMapViewFragment = new MapViewFragment();
    private ListViewFragment mListViewFragment = new ListViewFragment();
    private WorkmatesFragment mWorkmatesFragment = new WorkmatesFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        name = findViewById(R.id.outputName_tv);
        menu = findViewById(R.id.bottomNavigationView);

        String userName = getIntent().getExtras().getString("name");

        name.setText(userName);

        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mapView:
                        return true;
                    case R.id.listView:
                        return true;
                    case R.id.workmates:
                        return true;

                }
                return false;
            }
        });
    }
}