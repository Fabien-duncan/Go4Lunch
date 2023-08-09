package com.example.go4lunch.viewmodel;

import static org.junit.Assert.*;


import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

public class WorkmatesViewModelTest {
    @Mock
    private AuthenticationRepository mAuthenticationRepository;
    private WorkmatesViewModel mWorkmatesViewModel;
    private List<User> workmateList;
    @Rule //initMocks
    public MockitoRule rule = MockitoJUnit.rule();
    @Before
    public void setUp() throws Exception {
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
    }
    private void setUpRepositoryMethods(){


    }

}