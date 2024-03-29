package com.example.go4lunch.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.data_source.FirebaseApi;
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
import com.google.firebase.firestore.WriteBatch;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class AuthenticationRepositoryTest {
    private AuthenticationRepository mAuthenticationRepository;
    @Mock
    private FirebaseAuth mAuth;
    @Mock
    private GoogleSignInClient mGoogleSignInClient;
    @Mock
    private FirebaseFirestore db;
    @Mock
    private FirebaseApi mFirebaseApi;
    @Mock
    private MutableLiveData<FirebaseUser> mFirebaseUserMutableLiveData;
    @Mock
    private MutableLiveData<User> mCurrentUserMutableLiveData;
    @Mock
    private MutableLiveData<List<User>> mWorkmatesMutableLiveData;
    @Mock
    private MutableLiveData<String> mAuthMessageMutableLiveData;
    @Mock
    FirebaseUser firebaseUser;

    private User currentUser;
    private List<User> workmateList;

    @Rule //initMocks
    public MockitoRule rule = MockitoJUnit.rule();

    @Before
    public void setUp() {
        when(mFirebaseUserMutableLiveData.getValue()).thenReturn(firebaseUser);
        when(firebaseUser.getEmail()).thenReturn("testUser@gmail.com");
        when(mFirebaseApi.getFirebaseUserMutableLiveData()).thenReturn(mFirebaseUserMutableLiveData);
        generateCurrentUser();
        when(mFirebaseApi.getCurrentUserMutableLiveData()).thenReturn(mCurrentUserMutableLiveData);
        when(mCurrentUserMutableLiveData.getValue()).thenReturn(currentUser);
        generateWorkmates();
        when(mFirebaseApi.getWorkmatesMutableLiveData()).thenReturn(mWorkmatesMutableLiveData);
        when(mWorkmatesMutableLiveData.getValue()).thenReturn(workmateList);
        when(mFirebaseApi.getAuthMessageMutableLiveData()).thenReturn(mAuthMessageMutableLiveData);
        when(mAuthMessageMutableLiveData.getValue()).thenReturn("test message");

        mAuthenticationRepository = new AuthenticationRepository(mAuth,db, mFirebaseApi, mGoogleSignInClient);
    }


    @Test
    public void signIn() {
        Intent mockSignInIntent = Mockito.mock(Intent.class);
        when(mGoogleSignInClient.getSignInIntent()).thenReturn(mockSignInIntent);

        assertNotNull(mAuthenticationRepository.signIn());

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
    }

    @Test
    public void handleSignInResult() {

    }

    @Test
    public void firebaseAuthWithGoogle() {
        mAuthenticationRepository.firebaseAuthWithGoogle("testToken");
        FirebaseUser resultUser = mAuthenticationRepository.getFirebaseUserMutableLiveData().getValue();

        verify(mFirebaseApi).firebaseAuthWithGoogle(anyString());
        assert resultUser != null;
        assertEquals("testUser@gmail.com", resultUser.getEmail());
    }

    @Test
    public void firebaseAuthWithEmailAndPassword() {
        mAuthenticationRepository.firebaseAuthWithEmailAndPassword("testUser@gmail.com", "password");

        FirebaseUser resultUser = mAuthenticationRepository.getFirebaseUserMutableLiveData().getValue();

        verify(mFirebaseApi).firebaseAuthWithEmailAndPassword(anyString(), anyString());
        assert resultUser != null;
        assertEquals("testUser@gmail.com", resultUser.getEmail());
    }

    @Test
    public void firebaseCreateUser() {
        mAuthenticationRepository.firebaseCreateUser("testUser@gmail.com", "password", "testUser");

        FirebaseUser resultUser = mAuthenticationRepository.getFirebaseUserMutableLiveData().getValue();

        verify(mFirebaseApi).firebaseCreateUser(anyString(), anyString(), anyString());
        assert resultUser != null;
        assertEquals("testUser@gmail.com", resultUser.getEmail());
    }

    @Test
    public void getAuthMessageMutableLiveData(){
        MutableLiveData<String> result = mAuthenticationRepository.getAuthMessageMutableLiveData();

        assertNotNull(result);
        assertEquals(mAuthMessageMutableLiveData, result);
    }
    @Test
    public void getFirebaseUserMutableLiveData() {
        MutableLiveData<FirebaseUser> result = mAuthenticationRepository.getFirebaseUserMutableLiveData();

        // Verify interactions and assertions
        assertNotNull(result);
        assertEquals(mFirebaseUserMutableLiveData, result);
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
        assert resultWorkmates != null;
        assertEquals(3, resultWorkmates.size());
        assertEquals("Fabien Duncan", resultWorkmates.get(0).getDisplayName());
        assertEquals("Bob",resultWorkmates.get(2).getDisplayName());
    }

    @Test
    public void retrieveFilteredWorkmates() {
        List<User> resultWorkmates;
        doAnswer(invocation -> {
            String id = (String)invocation.getArguments()[0];
            List<User> filteredWorkmates = new ArrayList<>();
            for(int i =0; i < workmateList.size(); i++){
                if(workmateList.get(i).getLunchChoiceId().equals(id)){
                    filteredWorkmates.add(workmateList.get(i));
                }
            }
            workmateList = filteredWorkmates;

            return(null);
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
        assert resultUser != null;
        assertEquals("testUser@gmail.com", resultUser.getEmail());
    }

    @Test
    public void updateUserRestaurantChoice() {
        // Mock Firebase user and authentication
        FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);
        String choiceTimeStamp = "2023-08-15T16:00";

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
                "choiceTimeStamp", choiceTimeStamp
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