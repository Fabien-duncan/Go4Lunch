package com.example.go4lunch.dataSource;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseApi{
    private Context mContext;
    private FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final MutableLiveData<FirebaseUser> mFirebaseUserMutableLiveData;
    private final MutableLiveData<User> currentUserMutableLiveData;
    private final MutableLiveData<List<User>> workmatesMutableLiveData;
    private final MutableLiveData<String> authMessageMutableLiveData;

    public FirebaseApi(){
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mFirebaseUserMutableLiveData = new MutableLiveData<>();
        currentUserMutableLiveData = new MutableLiveData<>();
        workmatesMutableLiveData = new MutableLiveData<>(new ArrayList<>());
        authMessageMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<String> getAuthMessageMutableLiveData() {
        return authMessageMutableLiveData;
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

    public void firebaseAuthWithGoogle(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d("TAG","signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        String userId = user.getUid();
                        DocumentReference documentReference = db.collection("users").document(userId);

                        Log.d("get user info", "about to get user info");
                        documentReference.get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DocumentSnapshot document = task1.getResult();
                                if (document.exists()) {
                                    Log.d("get User info", "Document exists!");
                                } else {
                                    Log.d("get User info", "Document does not exist!");
                                    createFireStoreUser();
                                }
                            }else{
                                Log.d("get User info", "failed to create user");
                            }
                        });

                        mFirebaseUserMutableLiveData.postValue(user);
                        authMessageMutableLiveData.postValue("SignIn successfully!");
                    }else{
                        authMessageMutableLiveData.postValue("SignIn failed!");
                        Log.w("TAG", "signInWithCredential:failure", task.getException());
                    }
                });
    }
    public void firebaseAuthWithEmailAndPassword(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SignInWithPassword", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        mFirebaseUserMutableLiveData.postValue(user);
                        authMessageMutableLiveData.postValue("SignIn successfully!");

                    } else {
                        authMessageMutableLiveData.postValue("SignIn failed!");
                        Log.w("SignInWithPassword", "signInWithEmail:failure", task.getException());
                    }
                });
    }
    public void firebaseCreateUser(String email, String password, String displayName){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        String userId = user.getUid();
                        DocumentReference documentReference = db.collection("users").document(userId);
                        User newUser = new User(displayName,email, Uri.parse("https://img.freepik.com/free-icon/user_318-563642.jpg"));
                        documentReference.set(newUser).addOnSuccessListener(unused -> Log.d("Create User", "onSuccess: user Profile is created for" + userId));
                        mFirebaseUserMutableLiveData.postValue(user);
                        authMessageMutableLiveData.postValue("SignIn successfully!");
                    } else {
                        authMessageMutableLiveData.postValue("SignIn failed!");

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
    private void createFireStoreUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        String userId = user.getUid();
        DocumentReference documentReference = db.collection("users").document(userId);

        User newUser = new User(user.getDisplayName(),user.getEmail(), user.getPhotoUrl());

        documentReference.set(newUser).addOnSuccessListener(unused -> Log.d("Create User", "onSuccess: user Profile is created for" + userId));

    }



}
