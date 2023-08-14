package com.example.go4lunch.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModelTest {
    private AuthenticationRepository mAuthenticationRepository;
    private FirebaseUser mFirebaseUser;
    private MainActivityViewModel mMainActivityViewModel;
    private User newUser;
    @Before
    public void setUp() throws Exception {
        mAuthenticationRepository = Mockito.mock(AuthenticationRepository.class);
        mFirebaseUser = Mockito.mock(FirebaseUser.class);
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
    public void handleSignInResult() {
        Intent intent = Mockito.mock(Intent.class);
        mMainActivityViewModel.handleSignInResult(intent);
        Mockito.verify(mAuthenticationRepository).handleSignInResult(any(Intent.class));
    }

    @Test
    public void getGOOGLE_SIGN_IN() {
        Mockito.doReturn(123).when(mAuthenticationRepository).getGOOGLE_SIGN_IN();
        int signInCode = mMainActivityViewModel.getGOOGLE_SIGN_IN();
        assertEquals(123, signInCode);
    }

    @Test
    public void getUserData() {
        FirebaseUser tempUser =  mMainActivityViewModel.getUserData().getValue();
        assertEquals("Fabien", tempUser.getDisplayName());
    }

    @Test
    public void signIn(){
        mMainActivityViewModel.signIn();
        Mockito.verify(mAuthenticationRepository).signIn();
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
    }
}