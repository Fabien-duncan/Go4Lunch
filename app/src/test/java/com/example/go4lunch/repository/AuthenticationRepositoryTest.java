package com.example.go4lunch.repository;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.dataSource.FirebaseApi;
import com.example.go4lunch.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private FirebaseFirestore db;
    @Mock
    private FirebaseApi mFirebaseApi;
    @Mock
    private MutableLiveData<Boolean> isSignedIn;
    @Mock
    private MutableLiveData<FirebaseUser> mFirebaseUserMutableLiveData;
    @Mock
    private MutableLiveData<User> mCurrentUserMutableLiveData;
    @Mock
    private MutableLiveData<List<User>> mWorkmatesMutableLiveData;
    @Mock
    FirebaseUser firebaseUser;

    private User currentUser;
    private List<User> workmateList;

    @Rule //initMocks
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void setUp() throws Exception {
        when(mFirebaseUserMutableLiveData.getValue()).thenReturn(firebaseUser);
        when(firebaseUser.getEmail()).thenReturn("testUser@gmail.com");
        when(mFirebaseApi.getFirebaseUserMutableLiveData()).thenReturn(mFirebaseUserMutableLiveData);
        generateCurrentUser();
        when(mFirebaseApi.getCurrentUserMutableLiveData()).thenReturn(mCurrentUserMutableLiveData);
        when(mCurrentUserMutableLiveData.getValue()).thenReturn(currentUser);
        generateWorkmates();
        when(mFirebaseApi.getWorkmatesMutableLiveData()).thenReturn(mWorkmatesMutableLiveData);
        when(mWorkmatesMutableLiveData.getValue()).thenReturn(workmateList);

        mAuthenticationRepository = new AuthenticationRepository(mContext,mActivity,mAuth,db, mFirebaseApi, mGoogleSignInClient, isSignedIn);
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
        mAuthenticationRepository.firebaseAuthWithGoogle("testToken");
        FirebaseUser resultUser = mAuthenticationRepository.getFirebaseUserMutableLiveData().getValue();

        verify(mFirebaseApi).firebaseAuthWithGoogle(anyString());
        assertEquals("testUser@gmail.com", resultUser.getEmail());
    }

    @Test
    public void firebaseAuthWithEmailAndPassword() {
        mAuthenticationRepository.firebaseAuthWithEmailAndPassword("testUser@gmail.com", "password");

        FirebaseUser resultUser = mAuthenticationRepository.getFirebaseUserMutableLiveData().getValue();

        verify(mFirebaseApi).firebaseAuthWithEmailAndPassword(anyString(), anyString());
        assertEquals("testUser@gmail.com", resultUser.getEmail());
    }

    @Test
    public void firebaseCreateUser() {
        mAuthenticationRepository.firebaseCreateUser("testUser@gmail.com", "password", "testUser");

        FirebaseUser resultUser = mAuthenticationRepository.getFirebaseUserMutableLiveData().getValue();

        verify(mFirebaseApi).firebaseCreateUser(anyString(), anyString(), anyString());
        assertEquals("testUser@gmail.com", resultUser.getEmail());
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
        assertEquals(mWorkmatesMutableLiveData, result);
    }

    @Test
    public void retrieveAllWorkmates() {
        List<User> resultWorkmates;

        mAuthenticationRepository.retrieveAllWorkmates();
        resultWorkmates = mAuthenticationRepository.getWorkmatesMutableLiveData().getValue();

        verify(mFirebaseApi).retrieveAllWorkmates();
        assertEquals(3, resultWorkmates.size());
        assertEquals("Fabien Duncan", resultWorkmates.get(0).getDisplayName());
        assertEquals("Bob",resultWorkmates.get(2).getDisplayName());
    }

    @Test
    public void retrieveFilteredWorkmates() {
        List<User> resultWorkmates;
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String id = (String)invocation.getArguments()[0];
                List<User> filteredWorkmates = new ArrayList<>();
                for(int i =0; i < workmateList.size(); i++){
                    if(workmateList.get(i).getLunchChoiceId().equals(id)){
                        filteredWorkmates.add(workmateList.get(i));
                    }
                }
                workmateList = filteredWorkmates;

                return(null);
            }
        }).when(mFirebaseApi).retrieveFilteredWorkmates(anyString());

        mAuthenticationRepository.retrieveFilteredWorkmates("2");

        verify(mFirebaseApi).retrieveFilteredWorkmates(anyString());
        assertEquals(2, workmateList.size());
        assertEquals("Marion Chenus", workmateList.get(0).getDisplayName());
        assertEquals("Bob", workmateList.get(1).getDisplayName());
    }

    @Test
    public void setCurrentUser() {
        User resultUser;

        mAuthenticationRepository.setCurrentUser();
        resultUser = mAuthenticationRepository.getCurrentUserMutableLiveData().getValue();

        verify(mFirebaseApi).setCurrentUser();
        assertEquals("testUser@gmail.com", resultUser.getEmail());
    }

    @Test
    public void updateUserRestaurantChoice() {
        // Mock Firebase user and authentication
        FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
        LocalDateTime choiceTimeStamp = LocalDateTime.now();

        // Mock Firebase user and authentication
        when(mAuth.getCurrentUser()).thenReturn(mockUser);

        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        // Mock Firestore collection reference and query snapshot
        when(mockUser.getUid()).thenReturn("userID");
        when(db.collection("users")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockTask);

        // Call the method
        mAuthenticationRepository.updateUserRestaurantChoice("newChoiceId", "newChoiceName", choiceTimeStamp);

        // Verify interactions
        verify(mockDocumentReference).update(
                "lunchChoiceId", "newChoiceId",
                "lunchChoiceName", "Newchoicename",
                "choiceTimeStamp", choiceTimeStamp.toString()
        );
    }
    @Test
    public void testUpdateUserRestaurantFavoriteAdd() {
        // Mock Firebase user and authentication
        FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
        when(mAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("testUserId");

        WriteBatch mockWriteBatch = Mockito.mock(WriteBatch.class);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        // Mock Firestore collection reference and query snapshot
        when(mockUser.getUid()).thenReturn("userID");
        when(db.collection("users")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockTask);
        when(db.batch()).thenReturn(mockWriteBatch);

        // Call the method to add a favorite restaurant
        mAuthenticationRepository.updateUserRestaurantFavorite("restaurant123", "add");

        // Verify interactions
        verify(mockDocumentReference).update(anyString(), any(FieldValue.class));
    }
    @Test
    public void testUpdateUserRestaurantFavoriteRemove() {
        // Mock Firebase user and authentication
        FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
        when(mAuth.getCurrentUser()).thenReturn(mockUser);
        when(mockUser.getUid()).thenReturn("testUserId");

        WriteBatch mockWriteBatch = Mockito.mock(WriteBatch.class);
        DocumentReference mockDocumentReference = Mockito.mock(DocumentReference.class);
        CollectionReference mockCollectionReference = Mockito.mock(CollectionReference.class);
        Task<DocumentSnapshot> mockTask = Mockito.mock(Task.class);
        // Mock Firestore collection reference and query snapshot
        when(mockUser.getUid()).thenReturn("userID");
        when(db.collection("users")).thenReturn(mockCollectionReference);
        when(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference);
        when(mockDocumentReference.get()).thenReturn(mockTask);
        when(db.batch()).thenReturn(mockWriteBatch);

        // Call the method to add a favorite restaurant
        mAuthenticationRepository.updateUserRestaurantFavorite("restaurant123", "remove");

        // Verify interactions
        verify(mockDocumentReference).update(anyString(), any(FieldValue.class));
    }

    private void generateCurrentUser(){
        currentUser = new User();
        currentUser.setEmail("testUser@gmail.com");
        currentUser.setDisplayName("testUser");
    }
    private void generateWorkmates(){
        workmateList = new ArrayList<>();
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