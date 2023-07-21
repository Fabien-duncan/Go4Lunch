package com.example.go4lunch.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.dataSource.ApiService;
import com.example.go4lunch.viewmodel.ConnectedActivityViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapViewFragment extends SupportMapFragment{

    private GoogleMap mMap;
    private View mapView;
    private List<Restaurant> nearbyRestaurants;
    private ApiService googlePlacesReadTask;

    private boolean mLocationPermissionGranted = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private ConnectedActivityViewModel mConnectedActivityViewModel;
    //private AuthenticationRepository mAuthenticationRepository;

    private void initGoogleMap(boolean hasRestaurants) {
        getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.clear();
                if(currentLocation!=null) {
                    LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(myLocation).title("My location").icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(),R.drawable.restaurant_marker_green))));
                    mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
                }

                mMap.setMyLocationEnabled(true);
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                // position on right bottom
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);rlp.setMargins(0,0,30,30);

                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));


                if(hasRestaurants)placeNearbyRestaurants();

            }


        });

    }
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable =  AppCompatResources.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    @SuppressLint("MissingPermission")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        googlePlacesReadTask = new ApiService();

        //mAuthenticationRepository = new AuthenticationRepository(getContext());
        //mConnectedActivityViewModel = new ConnectedActivityViewModel(mAuthenticationRepository);
        mConnectedActivityViewModel = ((ConnectedActivity) getActivity()).getConnectedActivityViewModel();



        mConnectedActivityViewModel.getRestaurantsMutableLiveData().observe(getActivity(), new Observer<List<Restaurant>>() {
            @Override
            public void onChanged(List<Restaurant> restaurants) {
                nearbyRestaurants = restaurants;
                if(nearbyRestaurants.size()>0){
                    Log.d("Map Fragment", nearbyRestaurants.get(0).getName()
                            +  ", Lat: " + nearbyRestaurants.get(0).getLat()
                            +  ", Long: " + nearbyRestaurants.get(0).getLng()
                            + ",rating: " + nearbyRestaurants.get(0).getRating()
                            + ", attending " + nearbyRestaurants.get(0).getAttendanceNum());
                    //placeNearbyRestaurants();

                    try{
                        currentLocation = ((ConnectedActivity) getActivity()).getCurrentLocation();
                    }catch (Exception e){

                    }
                    initGoogleMap(true);
                }else if(nearbyRestaurants!= null && restaurants.size() == 0){
                    try{
                        currentLocation = ((ConnectedActivity) getActivity()).getCurrentLocation();
                        initGoogleMap(false);
                    }catch (Exception e){

                    }
                }
            }
        });
        /*mConnectedActivityViewModel.getGooglePlacesLiveData().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                //Log.d("Places live Data", s);
                mConnectedActivityViewModel.setRestaurantsMutableLiveData();
            }
        });*/


        mapView = this.getView();
        if(!mLocationPermissionGranted)return;

    }
    private void placeNearbyRestaurants(){

        for(int i = 0; i < nearbyRestaurants.size();i++){
            LatLng restaurantLocation = new LatLng(nearbyRestaurants.get(i).getLat(),nearbyRestaurants.get(i).getLng());
            if(nearbyRestaurants.get(i).getAttendanceNum()<=0)mMap.addMarker(new MarkerOptions().title(nearbyRestaurants.get(i).getName()).position(restaurantLocation).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(),R.drawable.restaurant_marker_orange))));
            else mMap.addMarker(new MarkerOptions().title(nearbyRestaurants.get(i).getName()).position(restaurantLocation).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(),R.drawable.restaurant_marker_green))));

        }
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                Log.d("click listener", "clicked on " + marker.getId().substring(1));
                DialogFragment restaurantDetailDialogue = RestaurantDetailDialogue.newInstance();
                //int index = Integer.parseInt(marker.getId().substring(1));
                int index = 0;
                for(int i = 0; i<nearbyRestaurants.size(); i++){
                    if(nearbyRestaurants.get(i).getName().equals(marker.getTitle())) index = i;
                }
                ((RestaurantDetailDialogue)restaurantDetailDialogue).setCurrentRestaurant(nearbyRestaurants.get(index));
                restaurantDetailDialogue.show(getActivity().getSupportFragmentManager(),"Restaurant Details");
                return false;
            }
        });
    }
}