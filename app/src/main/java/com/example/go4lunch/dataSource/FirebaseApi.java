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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseApi{
    private Context mContext;
    private FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final MutableLiveData<FirebaseUser> mFirebaseUserMutableLiveData;

    public FirebaseApi(Context context){
        this.mContext = context;
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        mFirebaseUserMutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return mFirebaseUserMutableLiveData;
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
                        Toast.makeText(mContext, "SignIn successfully!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(mContext, "SignIn Failed!", Toast.LENGTH_LONG).show();
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
                        //isUserSignedIn.postValue(true);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("SignInWithPassword", "signInWithEmail:failure", task.getException());
                        Toast.makeText(mContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
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
                        //isUserSignedIn.postValue(true);
                    } else {
                        Toast.makeText(mContext, "Authentication failed!!!", Toast.LENGTH_LONG).show();

                    }
                });
    }

}
