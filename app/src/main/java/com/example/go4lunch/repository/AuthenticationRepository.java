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
        currentUserMutableLiveData = new MutableLiveData<>();
        isUserSignedIn = new MutableLiveData<>();
        //mFirebaseUserMutableLiveData = new MutableLiveData<>();
        workmatesMutableLiveData = new MutableLiveData<>(new ArrayList<>());
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mFirebaseApi = new FirebaseApi(mContext);
        this.mFirebaseUserMutableLiveData = mFirebaseApi.getFirebaseUserMutableLiveData();
        if(mAuth.getCurrentUser() != null){
            mFirebaseUserMutableLiveData.postValue(mAuth.getCurrentUser());
            setCurrentUser();
        }
    }
    //constructor for unit tests
    public AuthenticationRepository(Context context, Activity activity, FirebaseAuth auth, FirebaseFirestore db, FirebaseApi firebaseApi, GoogleSignInClient googleSignInClient, MutableLiveData<Boolean> isUserSignedIn, MutableLiveData<FirebaseUser> firebaseUserMutableLiveData, MutableLiveData<User> currentUserMutableLiveData,MutableLiveData<List<User>> workmatesMutableLiveData){
        this.mContext = context;
        this.mActivity = activity;
        this.mAuth = auth;
        this.db = db;
        this.mGoogleSignInClient = googleSignInClient;

        this.currentUserMutableLiveData = currentUserMutableLiveData;
        this.isUserSignedIn = isUserSignedIn;
        //this.mFirebaseUserMutableLiveData = firebaseUserMutableLiveData;
        this.workmatesMutableLiveData = workmatesMutableLiveData;

        mFirebaseApi = firebaseApi;
        this.mFirebaseUserMutableLiveData = mFirebaseApi.getFirebaseUserMutableLiveData();
        if(mAuth.getCurrentUser() != null){
            mFirebaseUserMutableLiveData.postValue(mAuth.getCurrentUser());
            setCurrentUser();
        }
    }


    public void setupGoogleSignInOptions(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext,gso);
        mAuth = FirebaseAuth.getInstance();
    }

    public void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mActivity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        isUserSignedIn.postValue(true);
    }
    public void signOut(){
        //FirebaseAuth.getInstance().signOut();
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        isUserSignedIn.postValue(false);
        //Toast.makeText(mContext, "signed Out", Toast.LENGTH_LONG).show();

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
    private void createFireStoreUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        DocumentReference documentReference = db.collection("users").document(userId);

        User newUser = new User(user.getDisplayName(),user.getEmail(), user.getPhotoUrl());

        documentReference.set(newUser).addOnSuccessListener(unused -> Log.d("Create User", "onSuccess: user Profile is created for" + userId));

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
        FirebaseUser user = mAuth.getCurrentUser();
        CollectionReference collectionReference = db.collection("users");
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> list = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User workmate = document.toObject(User.class);
                    assert user != null;
                    if(!workmate.getEmail().equals(user.getEmail())){
                        System.out.println("adding a workmate");
                        list.add(workmate);
                    }
                }
                workmatesMutableLiveData.postValue(list);
                Log.d("workmates", list.get(0).getDisplayName());
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
    }
    public void retrieveFilteredWorkmates(String restaurantId){
        FirebaseUser user = mAuth.getCurrentUser();
        CollectionReference collectionReference = db.collection("users");
        collectionReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<User> list = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    User workmate = document.toObject(User.class);
                    assert user != null;
                    if(!workmate.getEmail().equals(user.getEmail()) && workmate.getLunchChoiceId().equals(restaurantId)){
                        System.out.println("adding a workmate");
                        User tempUser = document.toObject(User.class);
                        if(tempUser.isToday())list.add(tempUser);
                    }
                }
                workmatesMutableLiveData.postValue(list);
                Log.d("TAG", list.toString());
            } else {
                Log.d("TAG", "Error getting documents: ", task.getException());
            }
        });
    }
    public void setCurrentUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        DocumentReference documentReference = db.collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();

                currentUserMutableLiveData.postValue(document.toObject(User.class));

                if (document.exists()) {
                    Log.d("setCurrentUser", "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d("setCurrentUser", "No such document");
                }
            } else {
                Log.d("setCurrentUser", "get failed with ", task.getException());
            }
        });
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
