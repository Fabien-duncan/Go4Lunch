package com.example.go4lunch.dataSource;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.util.FormatString;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class GooglePlacesDetailsApi {
    public Restaurant currentRestaurant;
    public MutableLiveData<Restaurant> mRestaurantDetailMutableLiveData;

    public GooglePlacesDetailsApi(){
        mRestaurantDetailMutableLiveData = new MutableLiveData<>();
    }

    public void setSmallDetail(Restaurant restaurant, PlacesClient placesClient, String placeId){
        currentRestaurant = restaurant;
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            currentRestaurant.setWebsite(place.getWebsiteUri());
            currentRestaurant.setPhoneNumber(place.getPhoneNumber());
            mRestaurantDetailMutableLiveData.postValue(currentRestaurant);
            Log.d("PlacesDetail", "webSite: " + currentRestaurant.getWebsite());

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e("Places detail", "Place not found: " + exception.getMessage());
            }
        });
    }
    public void setAllDetails(Restaurant restaurant, PlacesClient placesClient, String placeId){
        currentRestaurant = restaurant;
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME,Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI, Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS, Place.Field.RATING);

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();



            currentRestaurant.setName(FormatString.capitalizeEveryWord(place.getName()));
            currentRestaurant.setPhoneNumber(place.getPhoneNumber());
            currentRestaurant.setAddress(place.getAddress());
            if(place.getRating() != null)currentRestaurant.setRating(place.getRating());
            else currentRestaurant.setRating(0);
            currentRestaurant.setWebsite(place.getWebsiteUri());


            final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
            if (metadata == null || metadata.isEmpty()) {
                Log.w("PlaceImage", "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(700) // Optional.
                    .setMaxHeight(500) // Optional.
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener((fetchPhotoResponse) -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                currentRestaurant.setImageBitmap(bitmap);
                mRestaurantDetailMutableLiveData.postValue(currentRestaurant);
            }).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    Log.e("PlaceImage", "Place not found: " + exception.getMessage());
                }
            });
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e("Places detail", "Place not found: " + exception.getMessage());
            }
        });
    }
    public MutableLiveData<Restaurant> getRestaurantDetailMutableLiveData(){
        return mRestaurantDetailMutableLiveData;
    }
}
