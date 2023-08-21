package com.example.go4lunch.repository;

import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.dataSource.FirebaseApi;
import com.example.go4lunch.model.User;
import com.example.go4lunch.util.FormatString;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Repository class that deals with Authentication and creation of User accounts both in the FirebaseAuth and the Firestore db
 */
public class AuthenticationRepository {
    private final GoogleSignInClient mGoogleSignInClient;
    private final MutableLiveData<FirebaseUser> mFirebaseUserMutableLiveData;

    private final MutableLiveData<User> currentUserMutableLiveData;
    private final MutableLiveData<List<User>> workmatesMutableLiveData;
    private final MutableLiveData<String> authMessageMutableLiveData;
    private final FirebaseAuth mAuth;

    private final FirebaseFirestore db;
    private final FirebaseApi mFirebaseApi;

    public AuthenticationRepository(FirebaseAuth auth, FirebaseFirestore db, FirebaseApi firebaseApi, GoogleSignInClient googleSignInClient){
        this.mAuth = auth;
        this.db = db;
        this.mGoogleSignInClient = googleSignInClient;
        this.mFirebaseApi = firebaseApi;
        this.authMessageMutableLiveData = mFirebaseApi.getAuthMessageMutableLiveData();
        this.currentUserMutableLiveData = mFirebaseApi.getCurrentUserMutableLiveData();
        this.workmatesMutableLiveData = mFirebaseApi.getWorkmatesMutableLiveData();
        this.mFirebaseUserMutableLiveData = mFirebaseApi.getFirebaseUserMutableLiveData();

        if(mAuth.getCurrentUser() != null && mGoogleSignInClient != null){
            mFirebaseUserMutableLiveData.postValue(mAuth.getCurrentUser());
            setCurrentUser();
        }
    }

    public MutableLiveData<String> getAuthMessageMutableLiveData() {
        return authMessageMutableLiveData;
    }

    public Intent signIn(){
        return mGoogleSignInClient.getSignInIntent();
    }
    public void signOut(){
        mAuth.signOut();
        mGoogleSignInClient.signOut();

    }
    public void firebaseAuthWithGoogle(String idToken){
        mFirebaseApi.firebaseAuthWithGoogle(idToken);
    }
    public void firebaseAuthWithEmailAndPassword(String email, String password){
        mFirebaseApi.firebaseAuthWithEmailAndPassword(email,password);
    }
    public void firebaseCreateUser(String email, String password, String displayName){
        mFirebaseApi.firebaseCreateUser(email,password,displayName);
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return mFirebaseUserMutableLiveData;
    }

    public MutableLiveData<User> getCurrentUserMutableLiveData() {
        return currentUserMutableLiveData;
    }

    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        return workmatesMutableLiveData;
    }
    public void retrieveAllWorkmates(){
        mFirebaseApi.retrieveAllWorkmates();
    }
    public void retrieveFilteredWorkmates(String restaurantId){
        mFirebaseApi.retrieveFilteredWorkmates(restaurantId);
    }
    public void setCurrentUser(){
        mFirebaseApi.setCurrentUser();
    }
    /**
     * Updates the user's chosen restaurant and related information.
     *
     * @param newChoiceId     The ID of the newly chosen restaurant.
     * @param newChoiceName   The name of the newly chosen restaurant.
     * @param choiceTimeStamp The timestamp indicating the time of the choice.
     */
    public void updateUserRestaurantChoice(String newChoiceId, String newChoiceName, String choiceTimeStamp){
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        db.collection("users").document(user.getUid()).update("lunchChoiceId", newChoiceId, "lunchChoiceName", FormatString.capitalizeEveryWord(newChoiceName), "choiceTimeStamp", choiceTimeStamp);
        setCurrentUser();
    }
    /**
     * Update the user's favorite restaurants by adding or removing a restaurant ID.
     *
     * @param restaurantID The ID of the restaurant to add or remove from favorites.
     * @param type         "add" to add or "remove" to remove the restaurant.
     */
    public void updateUserRestaurantFavorite(String restaurantID, String type){
        FirebaseUser user = mAuth.getCurrentUser();
        if(type.equals("add")) {
            assert user != null;
            db.collection("users").document(user.getUid()).update("favoriteRestaurants", FieldValue.arrayUnion(restaurantID));
        }
        else if (type.equals("remove")) {
            assert user != null;
            db.collection("users").document(user.getUid()).update("favoriteRestaurants", FieldValue.arrayRemove(restaurantID));
        }
        setCurrentUser();
    }

}
