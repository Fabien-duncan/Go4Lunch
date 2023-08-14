package com.example.go4lunch.repository;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.R;
import com.example.go4lunch.dataSource.FirebaseApi;
import com.example.go4lunch.model.User;
import com.example.go4lunch.util.FormatString;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuthenticationRepository {
    private final Context mContext;
    private GoogleSignInClient mGoogleSignInClient;
    private final Activity mActivity;
    private final int GOOGLE_SIGN_IN = 100;
    private final MutableLiveData<FirebaseUser> mFirebaseUserMutableLiveData;
    private final MutableLiveData<Boolean> isUserSignedIn;

    private final MutableLiveData<User> currentUserMutableLiveData;
    private final MutableLiveData<List<User>> workmatesMutableLiveData;
    private FirebaseAuth mAuth;

    private final FirebaseFirestore db;
    private FirebaseApi mFirebaseApi;

    public AuthenticationRepository(Context context){
        this.mContext = context;
        this.mActivity = (Activity)context;
        //currentUserMutableLiveData = new MutableLiveData<>();
        isUserSignedIn = new MutableLiveData<>();
        //mFirebaseUserMutableLiveData = new MutableLiveData<>();
        //workmatesMutableLiveData = new MutableLiveData<>(new ArrayList<>());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mFirebaseApi = new FirebaseApi(mContext);

        workmatesMutableLiveData = mFirebaseApi.getWorkmatesMutableLiveData();
        currentUserMutableLiveData = mFirebaseApi.getCurrentUserMutableLiveData();
        this.mFirebaseUserMutableLiveData = mFirebaseApi.getFirebaseUserMutableLiveData();
        if(mAuth.getCurrentUser() != null){
            mFirebaseUserMutableLiveData.postValue(mAuth.getCurrentUser());
            setCurrentUser();
        }
    }
    //constructor for unit tests
    public AuthenticationRepository(Context context, Activity activity, FirebaseAuth auth, FirebaseFirestore db, FirebaseApi firebaseApi, GoogleSignInClient googleSignInClient, MutableLiveData<Boolean> isUserSignedIn){
        this.mContext = context;
        this.mActivity = activity;
        this.mAuth = auth;
        this.db = db;
        this.mGoogleSignInClient = googleSignInClient;


        this.isUserSignedIn = isUserSignedIn;
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

    public void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mActivity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        isUserSignedIn.postValue(true);
    }
    public void signOut(){
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        isUserSignedIn.postValue(false);

    }
    public void handleSignInResult(Intent data){
        try{
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e){
            Log.w("TAG","SignInResult: failed code=" + e.getStatusCode());
            Toast.makeText(mContext, "SignIn Failed!", Toast.LENGTH_LONG).show();
            Log.d("TAG", "SignIn Failed.");
        }
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

    public MutableLiveData<Boolean> getIsUserSignedIn() {
        return isUserSignedIn;
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
