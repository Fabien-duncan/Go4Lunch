package com.example.go4lunch.repository;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationRepository {
    private Context mContext;
    private GoogleSignInClient mGoogleSignInClient;
    private Activity mActivity;
    private final int GOOGLE_SIGN_IN = 100;
    private MutableLiveData<FirebaseUser> mFirebaseUserMutableLiveData;
    private FirebaseAuth mAuth;

    public AuthenticationRepository(Context context){
        this.mContext = context;
        this.mActivity = (Activity) context;
        mFirebaseUserMutableLiveData = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            mFirebaseUserMutableLiveData.postValue(mAuth.getCurrentUser());
        }
    }
    public void setupGoogleSignInOptions(){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
    }
}
