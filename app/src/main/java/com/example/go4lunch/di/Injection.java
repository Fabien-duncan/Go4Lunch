package com.example.go4lunch.di;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.dataSource.ApiService;
import com.example.go4lunch.dataSource.AutoCompleteApi;
import com.example.go4lunch.dataSource.FirebaseApi;
import com.example.go4lunch.dataSource.GooglePlacesDetailsApi;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.repository.ConnectedActivityRepository;
import com.example.go4lunch.repository.RestaurantDetailRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {

    public static AuthenticationRepository createAuthenticationRepository(Context context, Activity activity){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseApi firebaseApi = new FirebaseApi(context);
        GoogleSignInClient googleSignInClient = setupGoogleSignInOptions(context);
        return new AuthenticationRepository(firebaseAuth, firebaseFirestore, firebaseApi, googleSignInClient);
    }
    public static ConnectedActivityRepository createConnectedActivityRepository(Context context){
        Executor executor = Executors.newSingleThreadExecutor();
        MutableLiveData<List<Restaurant>> restaurantsMutableLiveData = new MutableLiveData<>(new ArrayList<>());
        ApiService apiService = new ApiService();
        AutoCompleteApi autoCompleteApi = new AutoCompleteApi(context);
        return new ConnectedActivityRepository(apiService, autoCompleteApi, restaurantsMutableLiveData, executor);
    }
    public static RestaurantDetailRepository createRestaurantDetailRepository(Context context){
        String key = BuildConfig.GMP_key;
        Places.initialize(context, key);
        PlacesClient placesClient = Places.createClient(context);
        GooglePlacesDetailsApi googlePlacesDetailsApi = new GooglePlacesDetailsApi();
        return new RestaurantDetailRepository(placesClient, googlePlacesDetailsApi);

    }

    private static GoogleSignInClient setupGoogleSignInOptions(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(context,gso);
    }

}
