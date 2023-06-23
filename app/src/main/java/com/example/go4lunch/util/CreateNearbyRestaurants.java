package com.example.go4lunch.util;

import com.example.go4lunch.model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateNearbyRestaurants {
    public List<Restaurant> parse(JSONObject jsonObject) {
        JSONArray jsonArray = null;
        try {
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getRestaurants(jsonArray);
    }

    private List<Restaurant> getRestaurants(JSONArray jsonArray) {
        int placesCount = jsonArray.length();
        List<Restaurant> restaurantsList = new ArrayList<>();
        Restaurant aRestaurant = null;

        for (int i = 0; i < placesCount; i++) {
            try {
                aRestaurant = getRestaurant((JSONObject) jsonArray.get(i));
                restaurantsList.add(aRestaurant);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return restaurantsList;
    }

    private Restaurant getRestaurant(JSONObject googlePlaceJson) {
        Restaurant restaurant = null;
        String name = "-NA-";
        String address = "-NA-";
        String id;
        double lat;
        double lng;


        String latitude = "";
        String longitude = "";

        try {
            if (!googlePlaceJson.isNull("name")) {
                name = googlePlaceJson.getString("name");
            }
            if (!googlePlaceJson.isNull("vicinity")) {
                address = googlePlaceJson.getString("vicinity");
            }
            latitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            id = googlePlaceJson.getString("place_id");

            //convert to Restaurant object
            lat = Double.parseDouble(latitude);
            lng = Double.parseDouble(longitude);

            restaurant = new Restaurant(id,name,address,lat,lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return restaurant;
    }
}
