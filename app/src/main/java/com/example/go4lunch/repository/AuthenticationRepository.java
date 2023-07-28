package com.example.go4lunch.repository;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.util.FormatString;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuthenticationRepository {
    private Context mContext;
    private GoogleSignInClient mGoogleSignInClient;
    private Activity mActivity;
    private final int GOOGLE_SIGN_IN = 100;
    private MutableLiveData<FirebaseUser> mFirebaseUserMutableLiveData;
    private MutableLiveData<Boolean> isUserSignedIn;

    private MutableLiveData<User> currentUserMutableLiveData;
    private MutableLiveData<List<User>> workmatesMutableLiveData;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    public AuthenticationRepository(Context context){
        this.mContext = context;
        this.mActivity = (Activity)context;
        currentUserMutableLiveData = new MutableLiveData<>();
        isUserSignedIn = new MutableLiveData<>();
        mFirebaseUserMutableLiveData = new MutableLiveData<>();
        workmatesMutableLiveData = new MutableLiveData<>(new ArrayList<>());
        //initAllUsers();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if(mAuth.getCurrentUser() != null){
            System.out.println("getCurrentUser() is not null!");
            mFirebaseUserMutableLiveData.postValue(mAuth.getCurrentUser());
            setCurrentUser();
        }
    }

    /*private void initAllUsers() {
        List<User> tempUsers = new ArrayList<>();
        tempUsers.add(new User("Marion Chenus", "chenus.marion@gmail.com"));
        tempUsers.add(new User("Hugh Duncan", "hugh.duncan@gmail.com"));
        workmatesMutableLiveData.postValue(tempUsers);
    }*/

    public void setupGoogleSignInOptions(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext,gso);
        mAuth = FirebaseAuth.getInstance();
    }
    public boolean isUserAlreadySignIn(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return currentUser != null;
    }

    public void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        System.out.println("before on activity for result");
        mActivity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        System.out.println("After on activity for result");
        isUserSignedIn.postValue(true);
        //mFirebaseUserMutableLiveData.postValue(mAuth.getCurrentUser());
    }
    public void signOut(){
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut();
        //mAuth.signOut();
        //mFirebaseUserMutableLiveData = new MutableLiveData<>();
        isUserSignedIn.postValue(false);
        Toast.makeText(mContext, "signed Out", Toast.LENGTH_LONG).show();

    }
    public FirebaseUser getProfileInfo(){
        FirebaseUser account = FirebaseAuth.getInstance().getCurrentUser();
        if(account != null){

        }
        else{
            Toast.makeText(mContext, "No account info found.", Toast.LENGTH_LONG).show();
        }
        return account;
    }
    public void handleSignInResult(Intent data){
        System.out.println("in handleSignInResult");
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
        System.out.println("in FirebaseAuthWithGoogle");
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("TAG","signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            DocumentReference documentReference = db.collection("users").document(userId);

                            //retrieveAllWorkmates();
                            //currentUserMutableLiveData.postValue(newUser);
                            /*Map<String, Object> newUser = new HashMap<>();
                            newUser.put("displayName", user.getDisplayName());
                            newUser.put("email", user.getEmail());*/
                            Log.d("get user info", "about to get user info");
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d("get User info", "Document exists!");
                                        } else {
                                            Log.d("get User info", "Document does not exist!");
                                            createFireStoreUser();
                                        }
                                    }else{
                                        Log.d("get User info", "failed to create user");
                                    }
                                }
                            });

                            mFirebaseUserMutableLiveData.postValue(user);
                            //currentUserMutableLiveData.postValue(newUser);
                            Toast.makeText(mContext, "SignIn successfully!", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(mContext, "SignIn Failed!", Toast.LENGTH_LONG).show();
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }
    public void firebaseAuthWithEmailAndPassword(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignInWithPassword", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("SignInWithPassword", "signInWithEmail:failure", task.getException());
                            Toast.makeText(mContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    public void firebaseCreateUser(String email, String password, String displayName){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            DocumentReference documentReference = db.collection("users").document(userId);
                            User newUser = new User(displayName,email,Uri.parse("https://img.freepik.com/free-icon/user_318-563642.jpg"));
                            documentReference.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("Create User", "onSuccess: user Profile is created for" + userId);
                                }
                            });
                            mFirebaseUserMutableLiveData.postValue(user);
                            isUserSignedIn.postValue(true);
                        } else {
                            Toast.makeText(mContext, "Authentication failed!!!", Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }
    private void createFireStoreUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DocumentReference documentReference = db.collection("users").document(userId);

        User newUser = new User(user.getDisplayName(),user.getEmail(), user.getPhotoUrl());

        documentReference.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Create User", "onSuccess: user Profile is created for" + userId);
            }
        });

    }
    public int getGOOGLE_SIGN_IN(){
        return GOOGLE_SIGN_IN;
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        //mFirebaseUserMutableLiveData.postValue(mAuth.getCurrentUser());
        return mFirebaseUserMutableLiveData;
    }

    public MutableLiveData<Boolean> getIsUserSignedIn() {
        return isUserSignedIn;
    }
    public MutableLiveData<User> getCurrentUserMutableLiveData() {
        return currentUserMutableLiveData;
    }

    public void setCurrentUserMutableLiveData(MutableLiveData<User> currentUserMutableLiveData) {
        this.currentUserMutableLiveData = currentUserMutableLiveData;
    }

    public MutableLiveData<List<User>> getWorkmatesMutableLiveData() {
        return workmatesMutableLiveData;
    }
    public void retrieveAllWorkmates(){
        FirebaseUser user = mAuth.getCurrentUser();
        CollectionReference collectionReference = db.collection("users");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<User> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User workmate = document.toObject(User.class);
                        if(!workmate.equals(user.getEmail())){
                            System.out.println("adding a workmate");
                            list.add(workmate);
                        }
                    }
                    workmatesMutableLiveData.postValue(list);
                    Log.d("workmates", list.get(0).getDisplayName());
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    public void retrieveFilteredWorkmates(String restaurantId){
        FirebaseUser user = mAuth.getCurrentUser();
        CollectionReference collectionReference = db.collection("users");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<User> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        User workmate = document.toObject(User.class);
                        if(!workmate.getEmail().equals(user.getEmail()) && workmate.getLunchChoiceId().equals(restaurantId)){
                            System.out.println("adding a workmate");
                            list.add(document.toObject(User.class));
                        }
                    }
                    workmatesMutableLiveData.postValue(list);
                    Log.d("TAG", list.toString());
                } else {
                    Log.d("TAG", "Error getting documents: ", task.getException());
                }
            }
        });
    }
    public MutableLiveData<List<User>> getAllWorkmates(){
        return workmatesMutableLiveData;
    }
    public void setCurrentUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        DocumentReference documentReference = db.collection("users").document(user.getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    List<User> list = new ArrayList<>();
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
            }
        });
    }
    public void updateUserRestaurantChoice(String newChoiceId, String newChoiceName, LocalDateTime choiceTimeStamp){
        FirebaseUser user = mAuth.getCurrentUser();
        db.collection("users").document(user.getUid()).update("lunchChoiceId", newChoiceId, "lunchChoiceName", FormatString.capitalizeEveryWord(newChoiceName), "choiceTimeStamp", choiceTimeStamp.toString());
        setCurrentUser();
    }
    public void updateUserRestaurantFavorite(String restaurantID, String type){
        FirebaseUser user = mAuth.getCurrentUser();
        if(type.equals("add"))db.collection("users").document(user.getUid()).update("favoriteRestaurants", FieldValue.arrayUnion(restaurantID));
        else if (type.equals("remove")) {
            db.collection("users").document(user.getUid()).update("favoriteRestaurants", FieldValue.arrayRemove(restaurantID));
        }
        setCurrentUser();
    }

}
