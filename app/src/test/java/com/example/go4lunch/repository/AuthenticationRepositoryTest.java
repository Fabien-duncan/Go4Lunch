package com.example.go4lunch.repository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.hardware.biometrics.BiometricPrompt;
import android.net.Uri;
import android.os.Process;

public class AuthenticationRepositoryTest {
    private AuthenticationRepository mAuthenticationRepository;
    @Mock
    private Context mContext;
    @Mock
    private FirebaseAuth mAuth;
    @Mock
    private Activity mActivity;
    @Mock
    private GoogleSignInClient mGoogleSignInClient;
    @Mock
    FirebaseFirestore db;
    @Mock
    MutableLiveData<Boolean> isSignedIn;
    @Mock
    MutableLiveData<FirebaseUser> mFirebaseUserMutableLiveData;
    @Mock
    MutableLiveData<User> mCurrentUserMutableLiveData;
    @Mock
    MutableLiveData<List<User>> workmatesMutableLiveData;


    @Rule //initMocks
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        mAuthenticationRepository = new AuthenticationRepository(mContext,mActivity,mAuth,db,mGoogleSignInClient, isSignedIn,mFirebaseUserMutableLiveData,mCurrentUserMutableLiveData, workmatesMutableLiveData);

    }

    @Test
    public void setupGoogleSignInOptions() {

    }

    @Test
    public void signIn() {
        Intent mockSignInIntent = Mockito.mock(Intent.class);

        when(mGoogleSignInClient.getSignInIntent()).thenReturn(mockSignInIntent);

        // Call the method
        mAuthenticationRepository.signIn();

        // Verify interactions
        verify(mActivity).startActivityForResult(eq(mockSignInIntent), eq(mAuthenticationRepository.getGOOGLE_SIGN_IN()));
        verify(isSignedIn).postValue(any());

    }

    @Test
    public void signOut() {
        Intent mockSignInIntent = Mockito.mock(Intent.class);

        when(mGoogleSignInClient.getSignInIntent()).thenReturn(mockSignInIntent);

        // Call the method
        mAuthenticationRepository.signOut();

        // Verify interactions
        verify(mGoogleSignInClient).signOut();
        verify(mAuth).signOut();
        verify(isSignedIn).postValue(any());
    }

    @Test
    public void handleSignInResult() {

    }

    @Test
    public void firebaseAuthWithGoogle() {
    }

    @Test
    public void firebaseAuthWithEmailAndPassword() {
        FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
        Task<AuthResult> mockTask = Mockito.mock(Task.class);

        when(mAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockTask);
        when(mockTask.isSuccessful()).thenReturn(true);
        when(mAuth.getCurrentUser()).thenReturn(mockUser);

        // Call the method
        mAuthenticationRepository.firebaseAuthWithEmailAndPassword("test@example.com", "password");

        // Verify interactions
        verify(mockTask).addOnCompleteListener(any());
        verify(mAuth).getCurrentUser();

    }

    @Test
    public void firebaseCreateUser() {
    }


    @Test
    public void getGOOGLE_SIGN_IN() {
    }

    @Test
    public void getFirebaseUserMutableLiveData() {
        MutableLiveData<FirebaseUser> result = mAuthenticationRepository.getFirebaseUserMutableLiveData();

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(mFirebaseUserMutableLiveData, result);
    }

    @Test
    public void getIsUserSignedIn() {
        MutableLiveData<Boolean> result = mAuthenticationRepository.getIsUserSignedIn();

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(isSignedIn, result);
    }

    @Test
    public void getCurrentUserMutableLiveData() {
        MutableLiveData<User> result = mAuthenticationRepository.getCurrentUserMutableLiveData();

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(mCurrentUserMutableLiveData, result);
    }

    @Test
    public void getWorkmatesMutableLiveData() {
        MutableLiveData<List<User>> result = mAuthenticationRepository.getWorkmatesMutableLiveData();

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(workmatesMutableLiveData, result);
    }

    @Test
    public void retrieveAllWorkmates() {
    }

    @Test
    public void retrieveFilteredWorkmates() {
    }

    @Test
    public void setCurrentUser() {
    }

    @Test
    public void updateUserRestaurantChoice() {
    }

    @Test
    public void updateUserRestaurantFavorite() {
    }
}