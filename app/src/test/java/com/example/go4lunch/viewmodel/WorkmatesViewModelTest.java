package com.example.go4lunch.viewmodel;

import static org.junit.Assert.*;


import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesViewModelTest {
    private AuthenticationRepository mAuthenticationRepository;
    private WorkmatesViewModel mWorkmatesViewModel;
    private List<User> workmateList;
    @Before
    public void setUp() throws Exception {
       mAuthenticationRepository = Mockito.mock(AuthenticationRepository.class);
       workmateList = new ArrayList<>();
       generateWorkmates();
       MutableLiveData<List<User>> mutableWorkmates = Mockito.spy(new MutableLiveData<>(workmateList));
       Mockito.doReturn(mutableWorkmates).when(mAuthenticationRepository).getWorkmatesMutableLiveData();
       mWorkmatesViewModel = new WorkmatesViewModel(mAuthenticationRepository);
    }

    @Test
    public void setCurrentWorkmates() {
        mWorkmatesViewModel.setCurrentWorkmates();
        Mockito.verify(mAuthenticationRepository).retrieveAllWorkmates();
    }

    @Test
    public void getAllWorkmates() {
        List<User> workmates = mWorkmatesViewModel.getAllWorkmates().getValue();
        assertEquals(workmates.get(0).getDisplayName(), "Fabien Duncan");
        assertEquals(workmates.get(2).getDisplayName(), "Bob");
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
    private void setUpRepositoryMethods(){


    }

}