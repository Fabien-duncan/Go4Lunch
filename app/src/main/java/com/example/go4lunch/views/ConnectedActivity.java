package com.example.go4lunch.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.MatrixCursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.adapter.AutocompleteRecyclerViewAdapter;
import com.example.go4lunch.adapter.RestaurantRecyclerViewInterface;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.viewmodel.ConnectedActivityViewModel;
import com.example.go4lunch.viewmodel.MainActivityViewModel;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectedActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RestaurantRecyclerViewInterface {
    private TextView name;
    private TextView email;
    private BottomNavigationView menu;
    private MapViewFragment mMapViewFragment;
    private ListViewFragment mListViewFragment;
    private WorkmatesFragment mWorkmatesFragment;
    private ConnectedActivityViewModel mConnectedActivityViewModel;
    private AuthenticationRepository mAuthenticationRepository;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private ImageView profilePic;
    private boolean isLocationGranted;
    private List<Restaurant> nearbyRestaurants;
    private List<Restaurant> filteredNearbyRestaurants;
    private Location currentLocation;
    private RectangularBounds bounds;
    private LinearLayout autocompleteDisplay;
    private RecyclerView autocompleteRV;
    private AutocompleteRecyclerViewAdapter autocompleteAdapter;
    private FusedLocationProviderClient fusedLocationClient;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);
        filteredNearbyRestaurants = new ArrayList<>();

        menu = findViewById(R.id.bottomNavigationView);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mToolbar = findViewById(R.id.toolbar);
        View sideBarView = mNavigationView.getHeaderView(0);
        name = sideBarView.findViewById(R.id.side_menu_display_name);
        email = sideBarView.findViewById(R.id.side_menu_email);
        profilePic = sideBarView.findViewById(R.id.side_bar_profile_img);

        autocompleteDisplay= findViewById(R.id.autocomplete_layout);
        autocompleteRV = findViewById(R.id.autocomplete_rv);
        autocompleteRV.setLayoutManager(new LinearLayoutManager(this));
        autocompleteAdapter = new AutocompleteRecyclerViewAdapter(this, filteredNearbyRestaurants, this);
        autocompleteRV.setAdapter(autocompleteAdapter);

        autocompleteDisplay.setVisibility(View.INVISIBLE);


        mMapViewFragment = new MapViewFragment();
        mListViewFragment = new ListViewFragment();
        mWorkmatesFragment = new WorkmatesFragment();

        setSupportActionBar(mToolbar);

        isLocationGranted = false;
        getLocationPermission();

        /*MatrixCursor cursor = new MatrixCursor(new String[]{"_id","suggest_text_1"});
        String[] myList = {"apple","banana", "cat", "andy", "dog"};
        for(int i = 0; i <myList.length; i++){
            Object[] row = new Object[]{i, myList[i]};

            cursor.addRow(row);
        }*/

        SearchView searchView = findViewById(R.id.toolbar_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                autocompleteDisplay.setVisibility(View.INVISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() >1){
                    Log.d("searchView", "text is " + newText);
                    autocompleteDisplay.setVisibility(View.VISIBLE);
                    autocomplete(newText);
                    autocompleteAdapter.setRestaurantList(filteredNearbyRestaurants);
                    return true;
                }else return false;

            }
        });
        /*searchView.setSuggestionsAdapter(new SimpleCursorAdapter(
                this, android.R.layout.simple_list_item_1, cursor,
                new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 },
                new int[] { android.R.id.text1 }, 0));*/

        mAuthenticationRepository = new AuthenticationRepository(this);
        mConnectedActivityViewModel = new ConnectedActivityViewModel(mAuthenticationRepository);
        mConnectedActivityViewModel.setupGoogleSignInOptions();

        String userName = getIntent().getExtras().getString("name");


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawerLayout,mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.bringToFront();
        mNavigationView.setNavigationItemSelectedListener(this);
        mConnectedActivityViewModel.getIsUserSignedIn().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!aBoolean)showMainActivity();
            }
        });
        mConnectedActivityViewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                name.setText(firebaseUser.getDisplayName());
                email.setText(firebaseUser.getEmail());
                Log.d("User data", "id: " + firebaseUser.getUid());
                Glide.with(sideBarView).load(firebaseUser.getPhotoUrl()).circleCrop().into(profilePic);
            }
        });
        /* ******************************************
            I don't think this is the best approach...
         */
        mConnectedActivityViewModel.getRestaurantsMutableLiveData().observe(this, new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                Log.d("get restaurants connected", "*********");
                if(restaurants != null && restaurants.size() > 0 && restaurants.get(0).getAttendanceNum() < 0){
                    mConnectedActivityViewModel.setCurrentWorkmates();
                    nearbyRestaurants = restaurants;
                }
            }
        });
        mConnectedActivityViewModel.getAllWorkmates().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.d("getAllWorkmates", "updating attending workmates");
                mConnectedActivityViewModel.updateAttending(users);
            }
        });

        menu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.mapView:
                        if(isLocationGranted)getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mMapViewFragment).commit();
                        else{
                            Toast.makeText(ConnectedActivity.this, "you have not granted permissions! ", Toast.LENGTH_SHORT).show();
                        }
                        System.out.println("Maps");
                        break;
                    case R.id.listView:
                        if(isLocationGranted)getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mListViewFragment).commit();
                        else{
                            Toast.makeText(ConnectedActivity.this, "you have not granted permissions! ", Toast.LENGTH_SHORT).show();
                        }
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
                mConnectedActivityViewModel.signOut();
                //showMainActivity();
                System.out.println("singOut");
                break;
        }
        return true;
    }
    private void getLocationPermission(){
        Dexter.withContext(this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    Toast.makeText(ConnectedActivity.this,"permission granted", Toast.LENGTH_SHORT).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mMapViewFragment).commit();
                    isLocationGranted =true;
                    getLocation();

                }

                // check for permanent decline of any permission
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(ConnectedActivity.this,"permission NOT granted!!!", Toast.LENGTH_SHORT).show();
                    isLocationGranted = false;
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).onSameThread().check();

    }
    @SuppressLint("MissingPermission")
    private void getLocation(){

        Toast.makeText(this,"permission granted", Toast.LENGTH_SHORT).show();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    System.out.println("we found last location " + location.getLongitude() + ", " + location.getLatitude());
                    currentLocation = location;

                    setBounds(location, 400);

                    mConnectedActivityViewModel.setGooglePlacesData(currentLocation);

                    // Logic to handle location object
                }
            }

        });
        System.out.println("finished getting restaurants from json");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Connected Activity", "resume activity");
    }

    public List<Restaurant> getRestaurants(){
        return new ArrayList<>();
    }
    public ConnectedActivityViewModel getConnectedActivityViewModel(){
        return this.mConnectedActivityViewModel;
    }
    public Location getCurrentLocation(){
        return currentLocation;
    }
    private void autocomplete(String text){
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Create a RectangularBounds object.
        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                .setLocationRestriction(bounds)
                .setOrigin(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .setCountries("FR")
                .setTypesFilter(Arrays.asList("restaurant"))
                .setSessionToken(token)
                .setQuery(text)
                .build();

        String key = BuildConfig.GMP_key;

        // Initialize Places.
        Places.initialize(this.getApplicationContext(), key);

        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
        List<String> placeIds = new ArrayList<>();
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                placeIds.add(prediction.getPlaceId());
                Log.i("autocomplete", prediction.getPlaceId());
                Log.i("autocomplete", prediction.getPrimaryText(null).toString());
            }
            filterRestaurantsByIds(placeIds);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("autocomplete", "Place not found: " + apiException.getStatusCode());
            }
        });

    }
    private void setBounds(Location location, int mDistanceInMeters ) {
        double latRadian = Math.toRadians(location.getLatitude());

        double degLatKm = 110.574235;
        double degLongKm = 110.572833 * Math.cos(latRadian);
        double deltaLat = mDistanceInMeters / 1000.0 / degLatKm;
        double deltaLong = mDistanceInMeters / 1000.0 / degLongKm;

        double minLat = location.getLatitude() - deltaLat;
        double minLong = location.getLongitude() - deltaLong;
        double maxLat = location.getLatitude() + deltaLat;
        double maxLong = location.getLongitude() + deltaLong;

        bounds = RectangularBounds.newInstance(
                new LatLng(minLat, minLong),
                new LatLng(maxLat, maxLong));

        Log.d("setBounds", "Min: " + Double.toString(minLat) + "," + Double.toString(minLong));
        Log.d("setBounds", "Max: " + Double.toString(maxLat) + "," + Double.toString(maxLong));
    }
    private void filterRestaurantsByIds(List<String> placeIds){
        filteredNearbyRestaurants = new ArrayList<>();
        for(int i = 0; i < placeIds.size(); i++){
            int j = 0;
            while(j<nearbyRestaurants.size()){
                if(placeIds.get(i).equals(nearbyRestaurants.get(j).getId())){
                    filteredNearbyRestaurants.add(nearbyRestaurants.get(j));
                    j=nearbyRestaurants.size();
                }
                j++;
            }
        }
        Log.d("filteredRestaurant", "size: " + filteredNearbyRestaurants.size());
    }

    @Override
    public void onItemClick(int position) {

    }
}