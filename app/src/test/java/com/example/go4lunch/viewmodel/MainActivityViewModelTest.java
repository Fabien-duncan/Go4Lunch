package com.example.go4lunch.viewmodel;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doAnswer;

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
        mMainActivityViewModel.firebaseCreateUser("fab@gmail.com", "passwrod123", "Fabien Duncan");
        assertEquals("fab@gmail.com", newUser.getEmail());
    }

    @Test
    public void signInWithEmail() {
    }

    @Test
    public void handleSignInResult() {
    }

    @Test
    public void getGOOGLE_SIGN_IN() {
    }

    @Test
    public void getUserData() {
        FirebaseUser tempUser =  mMainActivityViewModel.getUserData().getValue();
        assertEquals("Fabien", tempUser.getDisplayName());
    }

    @Test
    public void signIn() {
    }

    @Test
    public void setupGoogleSignInOptions() {
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