package com.example.go4lunch.repository;

import static net.bytebuddy.matcher.ElementMatchers.any;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.location.Location;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.dataSource.ApiService;
import com.example.go4lunch.dataSource.AutoCompleteApi;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.google.android.libraries.places.api.model.RectangularBounds;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class ConnectedActivityRepositoryTest {

    @Mock
    private MutableLiveData<List<Restaurant>> restaurantsMutableLiveData;
    @Mock
    private ApiService mGooglePlacesReadTask;
    @Mock
    private AutoCompleteApi mAutoCompleteApi;

    private List<Restaurant> mRestaurantList;
    private ConnectedActivityRepository mConnectedActivityRepository;
    private List<User> workmateList;

    @Rule //initMocks
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        Executor mockExecutor = command -> command.run();
        mConnectedActivityRepository = new ConnectedActivityRepository(mGooglePlacesReadTask, mAutoCompleteApi, restaurantsMutableLiveData, mockExecutor);
        setRestaurantList();
        workmateList = new ArrayList<>();
        generateWorkmates();
    }

    @Test
    public void setGooglePlacesData() {
        Location newLocation = Mockito.mock(Location.class);
        when(newLocation.getLongitude()).thenReturn(50.0);
        when(newLocation.getLatitude()).thenReturn(52.5);

        mConnectedActivityRepository.setCurrentLocation(newLocation);
        when(mGooglePlacesReadTask.getGooglePlacesData(anyString(), ArgumentMatchers.any(Location.class))).thenReturn(mRestaurantList);

        mConnectedActivityRepository.setGooglePlacesData();

        verify(mGooglePlacesReadTask).getGooglePlacesData(anyString(), ArgumentMatchers.any(Location.class));
    }

    @Test
    public void getRestaurantsMutableLiveData() {
        //restaurantsMutableLiveData.setValue(mRestaurantList);
        when(restaurantsMutableLiveData.getValue()).thenReturn(mRestaurantList);
        List<Restaurant> resultRestaurants = mConnectedActivityRepository.getRestaurantsMutableLiveData().getValue();

        assertEquals(3, resultRestaurants.size());
        assertEquals("Zinc", resultRestaurants.get(0).getName());
        assertEquals("La Taverne", resultRestaurants.get(2).getName());
    }

    @Test
    public void updateAttending() {
        when(restaurantsMutableLiveData.getValue()).thenReturn(mRestaurantList);

        mConnectedActivityRepository.updateAttending(workmateList);

        verify(restaurantsMutableLiveData).getValue();
        verify(restaurantsMutableLiveData).postValue(anyList());
    }

    @Test
    public void autocomplete() {
        List<Restaurant> restaurants = new ArrayList<>();
        restaurants.add(mRestaurantList.get(2));
        MutableLiveData<List<Restaurant>> restaurantsMutableLiveData = Mockito.mock(MutableLiveData.class);
        List<Restaurant> restaurantsResult;
        Location location = mock(Location.class);
        when(mAutoCompleteApi.getRestaurantsMutableLiveData()).thenReturn(restaurantsMutableLiveData);
        when(restaurantsMutableLiveData.getValue()).thenReturn(restaurants);
        when(this.restaurantsMutableLiveData.getValue()).thenReturn(restaurants);

        mConnectedActivityRepository.setCurrentLocation(location);
        mConnectedActivityRepository.autocomplete("tav");
        restaurantsResult = mConnectedActivityRepository.getRestaurantsMutableLiveData().getValue();

        verify(mAutoCompleteApi).autocomplete(anyString(), anyList(), ArgumentMatchers.any(RectangularBounds.class), ArgumentMatchers.any(Location.class));
        verify(this.restaurantsMutableLiveData).postValue(anyList());
        assertEquals("La Taverne", restaurantsResult.get(0).getName());
    }

    @Test
    public void resetNearbyRestaurants() {
        mConnectedActivityRepository.resetNearbyRestaurants();

        verify(restaurantsMutableLiveData).postValue(anyList());
    }
    private void setRestaurantList(){
        mRestaurantList = new ArrayList<>();

        mRestaurantList.add(new Restaurant(
                "01",
                "Zinc",
                "16 ch du four",
                45,26,
                0,
                "https://media.istockphoto.com/id/1446478827/fr/photo/un-chef-cuisine-dans-la-cuisine-de-son-restaurant.jpg?s=1024x1024&w=is&k=20&c=_KRkTJnju8zm8pTSs-aOq9J4mdtzlPc31AucwKR54CY=",
                "french",
                "23:00",
                4.5,
                120,
                "+33 6 58 32 57 01"
        ));
        mRestaurantList.add(new Restaurant(
                "01",
                "Les deux Roch",
                "14 impasse, Les bas plans",
                45,20,
                2,
                "https://media.istockphoto.com/id/1446375027/fr/photo/r%C3%A9union-daffaires-dans-le-restaurant.jpg?s=1024x1024&w=is&k=20&c=fypbQnbN5F2zEI81FWLSHSA3EH5cpQXyHZDiSaWEQBY=",
                "french",
                "23:00",
                3.2,
                152,
                "+33 6 58 32 57 01"
        ));
        mRestaurantList.add(new Restaurant(
                "03", "La Taverne", "144 ch de Bargemon", 46,25,
                1,
                "https://media.istockphoto.com/id/1268553744/fr/photo/couples-asiatiques-de-sup%C3%A9rieur-commandant-la-nourriture-dans-un-restaurant-pendant.jpg?s=1024x1024&w=is&k=20&c=3OiKrP-4HweCQ404BjtzibMM9ji-ILQzFgkxWftJrus=",
                "french",
                "23:00",
                0.8,
                230,
                "+33 6 58 32 57 01"
        ));
    }
    private void generateWorkmates(){
        workmateList.add(new User());
        workmateList.get(0).setDisplayName("Fabien Duncan");
        workmateList.get(0).setLunchChoiceId("1");
        workmateList.add(new User());
        workmateList.get(1).setDisplayName("Marion Chenus");
        workmateList.get(1).setLunchChoiceId("2");
        workmateList.add(new User());
        workmateList.get(2).setDisplayName("Bob");
        workmateList.get(2).setLunchChoiceId("2");
    }
}