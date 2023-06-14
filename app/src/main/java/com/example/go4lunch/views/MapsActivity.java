package com.example.go4lunch.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.go4lunch.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.viewmodel.MainActivityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MapsActivity extends AppCompatActivity {
    private TextView name;
    private BottomNavigationView menu;
    private MapViewFragment mMapViewFragment;
    private ListViewFragment mListViewFragment;
    private WorkmatesFragment mWorkmatesFragment;
    private MainActivityViewModel mMainActivityViewModel;
    private FloatingActionButton signOutbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        name = findViewById(R.id.outputName_tv);
        menu = findViewById(R.id.bottomNavigationView);
        signOutbtn = findViewById(R.id.signOut_fb);

        mMapViewFragment = new MapViewFragment();
        mListViewFragment = new ListViewFragment();
        mWorkmatesFragment = new WorkmatesFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mMapViewFragment).commit();

        mMainActivityViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(MainActivityViewModel.class);

        String userName = getIntent().getExtras().getString("name");

        name.setText(userName);

        mMainActivityViewModel.getIsUserSignedIn().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!aBoolean)showMainActivity();
            }
        });
        signOutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivityViewModel.signOut();
                System.out.println("singOut");
            }
        });

        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mapView:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mMapViewFragment).commit();
                        System.out.println("Maps");
                        break;
                    case R.id.listView:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mListViewFragment).commit();
                        System.out.println("List");
                        break;
                    case R.id.workmates:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mWorkmatesFragment).commit();
                        System.out.println("Workmates");
                        break;
                }
                return true;
            }
        });
    }
    private void showMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        //intent.putExtra("signout", "true");
        //mMainActivityViewModel.signOut();
        startActivity(intent);

        finish();
    }
}