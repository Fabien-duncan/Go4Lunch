package com.example.go4lunch.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.repository.GooglePlacesReadTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapViewFragment extends SupportMapFragment {

    private GoogleMap mMap;
    private View mapView;
    private List<Restaurant> nearbyRestaurants;
    private GooglePlacesReadTask googlePlacesReadTask;

    private boolean mLocationPermissionGranted = false;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationClient;

    private void initGoogleMap() {
        getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                LatLng myLocation = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(myLocation).title("My location").icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(),R.drawable.restaurant_marker_green))));
                mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

                mMap.setMyLocationEnabled(true);
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                // position on right bottom
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);rlp.setMargins(0,0,30,30);

                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));

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
        googlePlacesReadTask = new GooglePlacesReadTask();
        getLocationPermission();
        mapView = this.getView();
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        /*if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/
        if(!mLocationPermissionGranted)return;

    }
    private void getLocationPermission(){
        Collection<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        Dexter.withContext(getContext()).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION).withListener(new MultiplePermissionsListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    Toast.makeText(getContext(),"permission granted", Toast.LENGTH_SHORT).show();
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
                    fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                System.out.println("we found last location " + location.getLongitude() + ", " + location.getLatitude());
                                currentLocation = location;
                                initGoogleMap();


                                ApplicationInfo applicationInfo = null;
                                try {
                                    applicationInfo = getContext().getPackageManager().getApplicationInfo(getContext().getPackageName(), PackageManager.GET_META_DATA);
                                } catch (PackageManager.NameNotFoundException e) {
                                    throw new RuntimeException(e);
                                }
                                if(applicationInfo!= null){
                                    String key = applicationInfo.metaData.getString("com.google.android.geo.API_KEY");

                                    System.out.println("Map api key: " + key);

                                    StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                                    googlePlacesUrl.append("location=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude());
                                    googlePlacesUrl.append("&radius=" + 5000);
                                    googlePlacesUrl.append("&types=" + "restaurant");
                                    googlePlacesUrl.append("&sensor=true");
                                    googlePlacesUrl.append("&key=" + key);

                                    System.out.println(googlePlacesUrl.toString());

                                    ExecutorService service = Executors.newSingleThreadExecutor();
                                    service.execute(new Runnable() {
                                        @Override
                                        public void run() {

                                            System.out.println("getting google url json");
                                            String googlePlaceData = googlePlacesReadTask.getGooglePlacesData(mMap, googlePlacesUrl.toString());
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    System.out.println("getting restaurants from json");
                                                    nearbyRestaurants = googlePlacesReadTask.getGooglePlacesRestaurants(googlePlaceData);

                                                }
                                            });
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Log.d("nearby restaurant", "first: " + nearbyRestaurants.get(0).getName());
                                                    placeNearbyRestaurants();
                                                }
                                            });

                                        }
                                    });


                                }

                                // Logic to handle location object
                            }
                        }

                    });
                    System.out.println("finished getting restaurants from json");
                    //Log.d("nearby restaurant", "first: " + nearbyRestaurants.get(0).getName());
                    mLocationPermissionGranted = true;
                }

                // check for permanent decline of any permission
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(getContext(),"permission NOT granted", Toast.LENGTH_SHORT).show();
                    // permission denied permanently, navigate user to app settings for granting permissions
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).onSameThread().check();

    }
    private void placeNearbyRestaurants(){
        for(int i = 0; i < nearbyRestaurants.size();i++){
            LatLng restaurantLocation = new LatLng(nearbyRestaurants.get(i).getLat(),nearbyRestaurants.get(i).getLng());
            mMap.addMarker(new MarkerOptions().position(restaurantLocation).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(getContext(),R.drawable.restaurant_marker_orange))));
        }

    }

}