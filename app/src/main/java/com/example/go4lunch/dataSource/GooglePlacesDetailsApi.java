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

/**
 * GooglePlacesDetailsApi to allow the RestaurantDetailRepository to retrieving detailed information about a restaurant from Google Places API.
 */
public class GooglePlacesDetailsApi {
    public Restaurant currentRestaurant;
    public MutableLiveData<Restaurant> mRestaurantDetailMutableLiveData;

    /**
     * Constructs an instance of GooglePlacesDetailsApi.
     */
    public GooglePlacesDetailsApi(){
        mRestaurantDetailMutableLiveData = new MutableLiveData<>();
    }

    /**
     * Used for when less details need to be retrieved as the rest is already available.
     *
     * @param restaurant   The Restaurant object to update with small details.
     * @param placesClient The PlacesClient instance to fetch place details.
     * @param placeId      The place ID of the restaurant.
     */
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

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                Log.e("Places detail", "Place not found: " + exception.getMessage());
            }
        });
    }
    /**
     * Retrieves all required details of the restaurant.
     *
     * @param restaurant   The Restaurant object to update with all details.
     * @param placesClient The PlacesClient instance to fetch place details.
     * @param placeId      The place ID of the restaurant.
     */
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
            //if there is no photo data return
            if (metadata == null || metadata.isEmpty()) {
                Log.w("PlaceImage", "No photo metadata.");
                return;
            }
            final PhotoMetadata photoMetadata = metadata.get(0);

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(700)
                    .setMaxHeight(500)
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
