package com.example.go4lunch.repository;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.R;
import com.example.go4lunch.model.User;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class AuthenticationRepository {
    private Context mContext;
    private GoogleSignInClient mGoogleSignInClient;
    private Activity mActivity;
    private final int GOOGLE_SIGN_IN = 100;
    private MutableLiveData<FirebaseUser> mFirebaseUserMutableLiveData;
    private MutableLiveData<Boolean> isUserSignedIn;

    private MutableLiveData<User> currentUserMutableLiveData;
    private MutableLiveData<List<User>> allUsersMutableLiveData;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    public AuthenticationRepository(Context context){
        this.mContext = context;
        this.mActivity = (Activity)context;
        mFirebaseUserMutableLiveData = new MutableLiveData<>();
        isUserSignedIn = new MutableLiveData<>();
        mFirebaseUserMutableLiveData = new MutableLiveData<>();
        allUsersMutableLiveData = new MutableLiveData<>(new ArrayList<>());
        initAllUsers();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if(mAuth.getCurrentUser() != null){
            System.out.println("getCurrentUser() is not null!");
            mFirebaseUserMutableLiveData.postValue(mAuth.getCurrentUser());
        }
    }

    private void initAllUsers() {
        List<User> tempUsers = new ArrayList<>();
        tempUsers.add(new User("Marion Chenus", "chenus.marion@gmail.com"));
        tempUsers.add(new User("Hugh Duncan", "hugh.duncan@gmail.com"));
        allUsersMutableLiveData.postValue(tempUsers);
    }

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
        mFirebaseUserMutableLiveData = new MutableLiveData<>();
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
                            User newUser = new User(user.getDisplayName(),user.getEmail());
                            //currentUserMutableLiveData.postValue(newUser);
                            /*Map<String, Object> newUser = new HashMap<>();
                            newUser.put("displayName", user.getDisplayName());
                            newUser.put("email", user.getEmail());*/
                            documentReference.set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG", "onSuccess: user Profile is created for" + userId);
                                }
                            });
                            mFirebaseUserMutableLiveData.postValue(user);
                            Toast.makeText(mContext, "SignIn successfully!", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(mContext, "SignIn Failed!", Toast.LENGTH_LONG).show();
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                        }
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

    public MutableLiveData<List<User>> getAllUsersMutableLiveData() {
        return allUsersMutableLiveData;
    }

/*public User getCurrentUser(){
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DocumentReference documentReference = db.collection("users").document(userId);
        User currentUser = documentReference.get().getResult().toObject(User.class);
        return currentUser;
    }*/
}
