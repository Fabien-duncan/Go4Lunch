package com.example.go4lunch.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModelTest {
    @Mock
    private AuthenticationRepository mAuthenticationRepository;
    @Mock
    private FirebaseUser mFirebaseUser;
    private MainActivityViewModel mMainActivityViewModel;
    @Mock
    private MutableLiveData<String> mAuthMessageMutableLiveData;
    private User newUser;
    @Rule //initMocks
    public MockitoRule rule = MockitoJUnit.rule();
    @Before
    public void setUp() throws Exception {
        Mockito.doReturn("Fabien").when(mFirebaseUser).getDisplayName();

        MutableLiveData<FirebaseUser> mutableFirebaseUser= Mockito.spy(new MutableLiveData<>(mFirebaseUser));
        Mockito.doReturn(mutableFirebaseUser).when(mAuthenticationRepository).getFirebaseUserMutableLiveData();

        setUpRepositoryMethods();
        mMainActivityViewModel = new MainActivityViewModel(mAuthenticationRepository);
    }

    @Test
    public void firebaseCreateUser() {
        mMainActivityViewModel.firebaseCreateUser("fab@gmail.com", "password123", "Fabien Duncan");
        Mockito.verify(mAuthenticationRepository).firebaseCreateUser(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        assertEquals("fab@gmail.com", newUser.getEmail());
    }

    @Test
    public void signInWithEmail() {
        mMainActivityViewModel.signInWithEmail("fab@gmail.com", "password123");
        Mockito.verify(mAuthenticationRepository).firebaseAuthWithEmailAndPassword(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void getUserData() {
        FirebaseUser tempUser =  mMainActivityViewModel.getUserData().getValue();
        assertEquals("Fabien", tempUser.getDisplayName());
    }

    @Test
    public void getAuthMessageMutableLiveData(){
        MutableLiveData<String> result = mMainActivityViewModel.getAuthMessageMutableLiveData();
        String message = result.getValue();

        assertNotNull(result);
        assertEquals(mAuthMessageMutableLiveData, result);
        assertEquals("test message", message);
    }

    @Test
    public void signIn(){
        mMainActivityViewModel.signIn();
        Mockito.verify(mAuthenticationRepository).signIn();
    }
    @Test
    public void firebaseAuthWithGoogle(){
        mMainActivityViewModel.firebaseAuthWithGoogle("testToken");

        verify(mAuthenticationRepository).firebaseAuthWithGoogle(anyString());
    }


    private void setUpRepositoryMethods(){
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String email = (String)invocation.getArguments()[0];
                newUser = new User();
                newUser.setEmail(email);
                return(null);
            }
        }).when(mAuthenticationRepository).firebaseCreateUser(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        when(mAuthenticationRepository.getAuthMessageMutableLiveData()).thenReturn(mAuthMessageMutableLiveData);
        when(mAuthMessageMutableLiveData.getValue()).thenReturn("test message");
    }
}