package com.example.go4lunch.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.RectangularBounds;

public class AutoCompleteBounds {
    /**
     * Calculates and sets the rectangular bounds for nearby restaurant search based on the current location.
     */
    public static RectangularBounds setBounds(Location currentLocation) {
        int mDistanceInMeters = 400;
        double latRadian = Math.toRadians(currentLocation.getLatitude());

        double degLatKm = 110.574235;
        double degLongKm = 110.572833 * Math.cos(latRadian);
        double deltaLat = mDistanceInMeters / 1000.0 / degLatKm;
        double deltaLong = mDistanceInMeters / 1000.0 / degLongKm;

        double minLat = currentLocation.getLatitude() - deltaLat;
        double minLong = currentLocation.getLongitude() - deltaLong;
        double maxLat = currentLocation.getLatitude() + deltaLat;
        double maxLong = currentLocation.getLongitude() + deltaLong;

        return RectangularBounds.newInstance(
                new LatLng(minLat, minLong),
                new LatLng(maxLat, maxLong));
    }
}
