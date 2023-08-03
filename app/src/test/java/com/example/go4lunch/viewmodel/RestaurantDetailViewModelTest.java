package com.example.go4lunch.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doAnswer;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.repository.RestaurantDetailRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDetailViewModelTest {
    private AuthenticationRepository mAuthenticationRepository;
    private RestaurantDetailRepository mRestaurantDetailRepository;
    private RestaurantDetailViewModel mRestaurantDetailViewModel;
    private List<User> workmateList;
    private Restaurant currentRestaurant;

    @Before
    public void setUp() throws Exception {
        mAuthenticationRepository = Mockito.mock(AuthenticationRepository.class);
        mRestaurantDetailRepository = Mockito.mock(RestaurantDetailRepository.class);
        Context context = Mockito.mock(Context.class);
        workmateList = new ArrayList<>();
        generateWorkmates();
        currentRestaurant = new Restaurant(
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
        );
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                for (Object rawRestaurant : invocation.getArguments()) {
                    Restaurant newRestaurant=(Restaurant) rawRestaurant;
                    currentRestaurant = newRestaurant;
                }

                return(null);
            }
        }).when(mRestaurantDetailRepository).setDetail(ArgumentMatchers.any(Restaurant.class));
        MutableLiveData<List<User>> mutableWorkmates = Mockito.spy(new MutableLiveData<>(workmateList));
        Mockito.doReturn(mutableWorkmates).when(mAuthenticationRepository).getWorkmatesMutableLiveData();
        mRestaurantDetailViewModel = new RestaurantDetailViewModel(mAuthenticationRepository, mRestaurantDetailRepository);
    }

    @Test
    public void setDetail() {
        mRestaurantDetailViewModel.setDetail(currentRestaurant);
        Mockito.verify(mRestaurantDetailRepository).setDetail(currentRestaurant);
    }

    @Test
    public void getAllWorkmates() {
        List<User> workmates = mRestaurantDetailViewModel.getAllWorkmates().getValue();
        assertEquals(workmates.get(0).getDisplayName(), "Fabien Duncan");
        assertEquals(workmates.get(2).getDisplayName(), "Bob");
    }

    @Test
    public void retrieveFilteredWorkmates() {

    }

    @Test
    public void getCurrentRestaurantMutableLiveDate() {
    }
    private void generateWorkmates(){
        workmateList.add(new User());
        workmateList.get(0).setDisplayName("Fabien Duncan");
        workmateList.add(new User());
        workmateList.get(1).setDisplayName("Marion Chenus");
        workmateList.add(new User());
        workmateList.get(2).setDisplayName("Bob");

        /*workmateList.add(new User("Marion Chenus", "marion.chenus@gmail.com", Uri.parse("https://img.freepik.com/free-icon/user_318-563642.jpg")));
        workmateList.add(new User("Bob", "bob@gmail.com", Uri.parse("https://img.freepik.com/free-icon/user_318-563642.jpg")));*/

    }
}