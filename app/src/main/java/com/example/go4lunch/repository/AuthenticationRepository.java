package com.example.go4lunch.repository;

import android.app.Activity;
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

import java.time.LocalDateTime;
import java.util.List;

public class AuthenticationRepository {
    private GoogleSignInClient mGoogleSignInClient;
    private final int GOOGLE_SIGN_IN = 100;
    private final MutableLiveData<FirebaseUser> mFirebaseUserMutableLiveData;

    private final MutableLiveData<User> currentUserMutableLiveData;
    private final MutableLiveData<List<User>> workmatesMutableLiveData;
    private FirebaseAuth mAuth;

    private final FirebaseFirestore db;
    private FirebaseApi mFirebaseApi;

    public AuthenticationRepository(FirebaseAuth auth, FirebaseFirestore db, FirebaseApi firebaseApi, GoogleSignInClient googleSignInClient){
        this.mAuth = auth;
        this.db = db;
        this.mGoogleSignInClient = googleSignInClient;

        //this.mFirebaseUserMutableLiveData = firebaseUserMutableLiveData;


        mFirebaseApi = firebaseApi;
        this.currentUserMutableLiveData = mFirebaseApi.getCurrentUserMutableLiveData();
        this.workmatesMutableLiveData = mFirebaseApi.getWorkmatesMutableLiveData();
        this.mFirebaseUserMutableLiveData = mFirebaseApi.getFirebaseUserMutableLiveData();
        if(mAuth.getCurrentUser() != null){
            mFirebaseUserMutableLiveData.postValue(mAuth.getCurrentUser());
            setCurrentUser();
        }
    }

    public Intent signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        return signInIntent;
    }
    public void signOut(){
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        //isUserSignedIn.postValue(false);

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
    public int getGOOGLE_SIGN_IN(){
        return GOOGLE_SIGN_IN;
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
    public void updateUserRestaurantChoice(String newChoiceId, String newChoiceName, LocalDateTime choiceTimeStamp){
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        db.collection("users").document(user.getUid()).update("lunchChoiceId", newChoiceId, "lunchChoiceName", FormatString.capitalizeEveryWord(newChoiceName), "choiceTimeStamp", choiceTimeStamp.toString());
        setCurrentUser();
    }
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
