package com.example.go4lunch.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.adapter.AutocompleteRecyclerViewAdapter;
import com.example.go4lunch.adapter.RestaurantRecyclerViewInterface;
import com.example.go4lunch.di.Injection;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.repository.ConnectedActivityRepository;
import com.example.go4lunch.util.ReminderBroadcast;
import com.example.go4lunch.viewmodel.ConnectedActivityViewModel;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConnectedActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RestaurantRecyclerViewInterface {
    private TextView name;
    private TextView email;
    private MapViewFragment mMapViewFragment;
    private ListViewFragment mListViewFragment;
    private WorkmatesFragment mWorkmatesFragment;
    private ConnectedActivityViewModel mConnectedActivityViewModel;
    private DrawerLayout mDrawerLayout;
    private ImageView profilePic;
    private boolean isLocationGranted;
    private List<Restaurant> nearbyRestaurants;
    private Location currentLocation;

    private LinearLayout autocompleteDisplay;
    private AutocompleteRecyclerViewAdapter autocompleteAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    private String currentFragment;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private User currentUser;
    //private ActivityConnectedBinding mActivityConnectedBinding;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        List<Restaurant> filteredNearbyRestaurants = new ArrayList<>();

        BottomNavigationView menu = findViewById(R.id.bottomNavigationView);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        View sideBarView = navigationView.getHeaderView(0);
        name = sideBarView.findViewById(R.id.side_menu_display_name);
        email = sideBarView.findViewById(R.id.side_menu_email);
        profilePic = sideBarView.findViewById(R.id.side_bar_profile_img);

        autocompleteDisplay = findViewById(R.id.autocomplete_layout);
        RecyclerView autocompleteRV = findViewById(R.id.autocomplete_rv);
        autocompleteRV.setLayoutManager(new LinearLayoutManager(this));
        autocompleteAdapter = new AutocompleteRecyclerViewAdapter(this, filteredNearbyRestaurants, this);
        autocompleteRV.setAdapter(autocompleteAdapter);

        autocompleteDisplay.setVisibility(View.INVISIBLE);


        mMapViewFragment = new MapViewFragment();
        mListViewFragment = new ListViewFragment();
        mWorkmatesFragment = new WorkmatesFragment();

        setSupportActionBar(toolbar);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                currentLocation = locationResult.getLastLocation();
                mConnectedActivityViewModel.setCurrentLocation(currentLocation);
                mConnectedActivityViewModel.setGooglePlacesData();
                Log.d("locationChanged", "onLocationResult " + locationResult);

            }
        };

        isLocationGranted = false;
        getLocationPermission();

        SearchView searchView = findViewById(R.id.toolbar_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                autocompleteDisplay.setVisibility(View.INVISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 1) {
                    Log.d("searchView", "text is " + newText);
                    if (currentFragment.equals("map"))
                        autocompleteDisplay.setVisibility(View.VISIBLE);
                    mConnectedActivityViewModel.autocomplete(newText);
                } else {
                    Log.d("searView", "retrievingNearbyPlaces");
                    autocompleteDisplay.setVisibility(View.INVISIBLE);
                    mConnectedActivityViewModel.resetNearbyRestaurants();
                }
                return true;

            }
        });
        searchView.setOnCloseListener(() -> {
            autocompleteDisplay.setVisibility(View.INVISIBLE);
            mConnectedActivityViewModel.resetNearbyRestaurants();
            return false;
        });

        AuthenticationRepository authenticationRepository = Injection.createAuthenticationRepository(this);
        ConnectedActivityRepository connectedActivityRepository = Injection.createConnectedActivityRepository(this);
        mConnectedActivityViewModel = new ConnectedActivityViewModel(authenticationRepository, connectedActivityRepository);
       //mConnectedActivityViewModel.setupGoogleSignInOptions();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel();
        }

        setCalendar();



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        mConnectedActivityViewModel.getUserData().observe(this, firebaseUser -> {
            name.setText(firebaseUser.getDisplayName());
            email.setText(firebaseUser.getEmail());
            Log.d("User data", "id: " + firebaseUser.getUid());
            Glide.with(sideBarView).load(firebaseUser.getPhotoUrl()).circleCrop().into(profilePic);
        });
        /* ******************************************
            I don't think this is the best approach...
         */
        mConnectedActivityViewModel.getRestaurantsMutableLiveData().observe(this, restaurants -> {
            autocompleteAdapter.setRestaurantList(restaurants);
            if (restaurants != null && restaurants.size() > 0 && restaurants.get(0).getAttendanceNum() < 0) {
                mConnectedActivityViewModel.setCurrentWorkmates();

                //nearbyRestaurants = restaurants;
            }
        });
        mConnectedActivityViewModel.getAllWorkmates().observe(this, users -> {
            Log.d("getAllWorkmates", "updating attending workmates");
            if (users.size() > 0) mConnectedActivityViewModel.updateAttending(users);
        });
        mConnectedActivityViewModel.getCurrentUserMutableLiveData().observe(this, user -> {
            currentUser = user;
            name.setText(user.getDisplayName());
            Glide.with(sideBarView).load(currentUser.getPhotoUrl()).circleCrop().into(profilePic);
            //Log.d("currentUser", "size of favorite List: " + currentUser.getFavoriteRestaurants().size());
        });

        menu.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.mapView:
                    if (isLocationGranted) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mMapViewFragment).commit();
                        currentFragment = "map";
                        searchView.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(ConnectedActivity.this, R.string.location_permission_rejected, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.listView:
                    if (isLocationGranted) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mListViewFragment).commit();
                        currentFragment = "list";
                        searchView.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(ConnectedActivity.this, R.string.location_permission_rejected, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.workmates:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mWorkmatesFragment).commit();
                    currentFragment = "workmates";
                    searchView.setVisibility(View.INVISIBLE);

                    break;
            }
            return true;
        });

    }

    private void showMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.side_bar_lunch:
                if(!isLocationGranted){
                    Toast.makeText(this, R.string.need_location_permission_msg, Toast.LENGTH_LONG).show();
                }else if (!currentUser.isToday() || currentUser.getLunchChoiceId().isEmpty()) {
                    Toast.makeText(this, R.string.no_lunch_choice_msg, Toast.LENGTH_LONG).show();

                } else {
                    Restaurant tempRestaurant = new Restaurant();
                    tempRestaurant.setId(currentUser.getLunchChoiceId());
                    RestaurantDetailDialogue restaurantDetailDialogue = RestaurantDetailDialogue.newInstance();
                    restaurantDetailDialogue.setCurrentRestaurant(tempRestaurant);
                    restaurantDetailDialogue.show(this.getSupportFragmentManager(), getString(R.string.restaurant_details));
                }
                break;
            case R.id.side_bar_settings:
                //Toast.makeText(this, "view Settings!", Toast.LENGTH_SHORT).show();
                SettingsFragment settingsFragment = new SettingsFragment();
                settingsFragment.show(getSupportFragmentManager(), getString(R.string.settings));
                break;
            case R.id.side_bar_logout:
                mConnectedActivityViewModel.signOut();
                showMainActivity();
                break;
        }
        return true;
    }

    private void getLocationPermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    //Toast.makeText(ConnectedActivity.this,"permission granted", Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mMapViewFragment).commit();
                    currentFragment = "map";
                    isLocationGranted = true;
                    getLocation();

                }

                // check for permanent decline of any permission
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(ConnectedActivity.this, "permission NOT granted!!!", Toast.LENGTH_SHORT).show();
                    isLocationGranted = false;
                    showSettingsDialog();
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).onSameThread().check();

    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        //Toast.makeText(this,"permission granted", Toast.LENGTH_SHORT).show();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new com.google.android.gms.location.LocationRequest.Builder(8000).setMinUpdateDistanceMeters(100).build();
        LocationSettingsRequest locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            } else {
                if (task.getException() instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) task.getException();
                        resolvableApiException.startResolutionForResult(ConnectedActivity.this, 1001);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public ConnectedActivityViewModel getConnectedActivityViewModel() {
        return this.mConnectedActivityViewModel;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public void onItemClick(int position) {

    }

    public void setCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (Calendar.getInstance().after(calendar)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        //ReminderBroadcast.text="Fabien";
        Intent intent = new Intent(ConnectedActivity.this, ReminderBroadcast.class);
        intent.putExtra("Name", "bob");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }else{
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void notificationChannel() {
        CharSequence name = getString(R.string.restaurant_choice);
        String description = getString(R.string.notification_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("notifyRestaurant", name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(ConnectedActivity.this);

        // below line is the title for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs to use location permissions this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            // this method is called on click on positive button and on clicking shit button
            // we are redirecting our user from our app to the settings page of our app.
            dialog.cancel();
            // below is the intent from which we are redirecting our user.
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // this method is called when user click on negative button.
            dialog.cancel();
        });
        // below line is used to display our dialog
        builder.show();
    }
}