package com.example.go4lunch.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.repository.ConnectedActivityRepository;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

public class ConnectedActivityViewModelTest {

    private AuthenticationRepository mAuthenticationRepository;
    private ConnectedActivityRepository mConnectedActivityRepository;
    private FirebaseUser mFirebaseUser;
    private ConnectedActivityViewModel mConnectedActivityViewModel;
    private User newUser;
    private boolean isUserSignedIn;
    private MutableLiveData<Boolean> isUserSignedInMutable;
    private User currentUser;
    @Before
    public void setUp() throws Exception {
        mAuthenticationRepository = Mockito.mock(AuthenticationRepository.class);
        mConnectedActivityRepository = Mockito.mock(ConnectedActivityRepository.class);
        mFirebaseUser = Mockito.mock(FirebaseUser.class);
        isUserSignedIn = false;

        isUserSignedInMutable = Mockito.spy(new MutableLiveData<>(false));
        Mockito.doReturn("Fabien").when(mFirebaseUser).getDisplayName();

        MutableLiveData<FirebaseUser> mutableFirebaseUser= Mockito.spy(new MutableLiveData<>(mFirebaseUser));
        Mockito.doReturn(mutableFirebaseUser).when(mAuthenticationRepository).getFirebaseUserMutableLiveData();

        generateUser();
        setUpRepositoryMethods();

        mConnectedActivityViewModel = new ConnectedActivityViewModel(mAuthenticationRepository, mConnectedActivityRepository);
    }
    @Test
    public void setupGoogleSignInOptions() {
        mConnectedActivityViewModel.setupGoogleSignInOptions();
        Mockito.verify(mAuthenticationRepository).setupGoogleSignInOptions();
    }

    @Test
    public void signOut() {
        isUserSignedIn = true;
        mConnectedActivityViewModel.signOut();
        Mockito.verify(mAuthenticationRepository).signOut();
        assertEquals(false, isUserSignedIn);

    }
    @Test
    public void getUserData() {
        FirebaseUser tempUser =  mConnectedActivityViewModel.getUserData().getValue();
        assertEquals("Fabien", tempUser.getDisplayName());
    }

    @Test
    public void getIsUserSignedIn() {
        boolean isSignedIn = mConnectedActivityViewModel.getIsUserSignedIn().getValue();
        Mockito.verify(mAuthenticationRepository).getIsUserSignedIn();
        assertEquals(false, isSignedIn);
    }

    @Test
    public void getCurrentUserMutableLiveData() {
        User user = mConnectedActivityViewModel.getCurrentUserMutableLiveData().getValue();
        assertEquals("Fabien", user.getDisplayName());
        assertEquals("fab@gmail.com", user.getEmail());

    }

    @Test
    public void setCurrentWorkmates() {
    }

    @Test
    public void getAllWorkmates() {
    }

    @Test
    public void setGooglePlacesData() {
    }

    @Test
    public void getRestaurantsMutableLiveData() {
    }

    @Test
    public void updateUserRestaurantChoice() {
    }

    @Test
    public void updateUserRestaurantFavorite() {
    }

    @Test
    public void updateAttending() {
    }

    @Test
    public void resetNearbyRestaurants() {
    }

    @Test
    public void autocomplete() {
    }

    @Test
    public void setCurrentLocation() {
    }

    private void setUpRepositoryMethods(){
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                isUserSignedIn = false;
                return(null);
            }
        }).when(mAuthenticationRepository).signOut();

        Mockito.doReturn(isUserSignedInMutable).when(mAuthenticationRepository).getIsUserSignedIn();

        MutableLiveData<User> currentUserMutable= Mockito.spy(new MutableLiveData<>(currentUser));
        Mockito.doReturn((currentUserMutable)).when(mAuthenticationRepository).getCurrentUserMutableLiveData();
    }
    private void generateUser(){
        currentUser = new User();
        currentUser.setEmail("fab@gmail.com");
        currentUser.setDisplayName("Fabien");
        currentUser.setLunchChoiceId("123");
        currentUser.setChoiceTimeStamp("2023-08-04T12:02:55.959097");
    }
}