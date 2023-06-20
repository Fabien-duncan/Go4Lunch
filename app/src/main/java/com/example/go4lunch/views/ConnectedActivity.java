package com.example.go4lunch.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.go4lunch.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.viewmodel.MainActivityViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

public class ConnectedActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView name;
    private TextView email;
    private BottomNavigationView menu;
    private MapViewFragment mMapViewFragment;
    private ListViewFragment mListViewFragment;
    private WorkmatesFragment mWorkmatesFragment;
    private MainActivityViewModel mMainActivityViewModel;
    private AuthenticationRepository mAuthenticationRepository;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private User currentUser;
    private ImageView profilePic;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        menu = findViewById(R.id.bottomNavigationView);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mToolbar = findViewById(R.id.toolbar);
        View sideBarView = mNavigationView.getHeaderView(0);
        name = sideBarView.findViewById(R.id.side_menu_display_name);
        email = sideBarView.findViewById(R.id.side_menu_email);
        profilePic = sideBarView.findViewById(R.id.side_bar_profile_img);


        mMapViewFragment = new MapViewFragment();
        mListViewFragment = new ListViewFragment();
        mWorkmatesFragment = new WorkmatesFragment();

        setSupportActionBar(mToolbar);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mMapViewFragment).commit();

        mAuthenticationRepository = new AuthenticationRepository(this);
        mMainActivityViewModel = new MainActivityViewModel(mAuthenticationRepository);
        //mMainActivityViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(MainActivityViewModel.class);
        mMainActivityViewModel.setupGoogleSignInOptions();

        String userName = getIntent().getExtras().getString("name");

        //name.setText(userName);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.bringToFront();
        mNavigationView.setNavigationItemSelectedListener(this);
        mMainActivityViewModel.getIsUserSignedIn().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!aBoolean)showMainActivity();
            }
        });
        mMainActivityViewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                name.setText(firebaseUser.getDisplayName());
                email.setText(firebaseUser.getEmail());
                Glide.with(sideBarView).load(firebaseUser.getPhotoUrl()).circleCrop().into(profilePic);
            }
        });

        /*mMainActivityViewModel.getCurrentUserMutableLiveData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                name.setText(user.getDisplayName());

            }
        });*/

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

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.side_bar_lunch:
                Toast.makeText(this, "view lunch!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.side_bar_settings:
                Toast.makeText(this, "view Settings!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.side_bar_logout:
                mMainActivityViewModel.signOut();
                //showMainActivity();
                System.out.println("singOut");
                break;
        }
        return true;
    }
}