package com.example.go4lunch.views;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.util.VectorDrawableConverter;
import com.example.go4lunch.viewmodel.ConnectedActivityViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Objects;

public class MapViewFragment extends SupportMapFragment{

    private GoogleMap mMap;
    private View mapView;
    private List<Restaurant> nearbyRestaurants;

    private Location currentLocation;

    @SuppressLint("MissingPermission")
    private void initGoogleMap(boolean hasRestaurants) {
        getMapAsync(googleMap -> {
            mMap = googleMap;
            mMap.clear();
            if(currentLocation!=null) {
                LatLng myLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(myLocation).title("My location").icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(),R.drawable.restaurant_marker_green))));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            }

            mMap.setMyLocationEnabled(true);
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            // position on right bottom
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);rlp.setMargins(0,0,30,30);

            //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));

            if(hasRestaurants)placeNearbyRestaurants();

        });

    }
    @SuppressLint("MissingPermission")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ConnectedActivityViewModel connectedActivityViewModel = ((ConnectedActivity) requireActivity()).getConnectedActivityViewModel();

        connectedActivityViewModel.getRestaurantsMutableLiveData().observe(requireActivity(), restaurants -> {
            nearbyRestaurants = restaurants;
            if(nearbyRestaurants.size()>0){

                try{
                    currentLocation = ((ConnectedActivity) requireActivity()).getCurrentLocation();
                }catch (Exception e){

                }
                initGoogleMap(true);
            }else {
                restaurants.size();
                try {
                    currentLocation = ((ConnectedActivity) requireActivity()).getCurrentLocation();
                    initGoogleMap(false);
                } catch (Exception e) {

                }
            }
        });

        mapView = this.getView();

    }
    private void placeNearbyRestaurants(){

        for(int i = 0; i < nearbyRestaurants.size();i++){
            LatLng restaurantLocation = new LatLng(nearbyRestaurants.get(i).getLat(),nearbyRestaurants.get(i).getLng());
            if(nearbyRestaurants.get(i).getAttendanceNum()<=0)
                Objects.requireNonNull(mMap.addMarker(new MarkerOptions().title(nearbyRestaurants.get(i).getName()).position(restaurantLocation).icon(BitmapDescriptorFactory.fromBitmap(VectorDrawableConverter.getBitmapFromVectorDrawable(getContext(), R.drawable.restaurant_marker_orange))))).setTag(nearbyRestaurants.get(i).getId());
            else Objects.requireNonNull(mMap.addMarker(new MarkerOptions().title(nearbyRestaurants.get(i).getName()).position(restaurantLocation).icon(BitmapDescriptorFactory.fromBitmap(VectorDrawableConverter.getBitmapFromVectorDrawable(getContext(), R.drawable.restaurant_marker_green))))).setTag(nearbyRestaurants.get(i).getId());

        }
        mMap.setOnMarkerClickListener(marker -> {
            RestaurantDetailDialogue restaurantDetailDialogue = RestaurantDetailDialogue.newInstance();
            int index = 0;
            for(int i = 0; i<nearbyRestaurants.size(); i++){
                if(nearbyRestaurants.get(i).getId().equals(marker.getTag())) index = i;
            }
            restaurantDetailDialogue.setCurrentRestaurant(nearbyRestaurants.get(index));
            restaurantDetailDialogue.show(requireActivity().getSupportFragmentManager(),getString(R.string.restaurant_details));
            return false;
        });
    }
}