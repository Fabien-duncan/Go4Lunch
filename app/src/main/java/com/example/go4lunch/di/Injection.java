package com.example.go4lunch.di;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.R;
import com.example.go4lunch.dataSource.FirebaseApi;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Injection {

    public static AuthenticationRepository createAuthenticationRepository(Context context, Activity activity){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseApi firebaseApi = new FirebaseApi(context);
        GoogleSignInClient googleSignInClient = setupGoogleSignInOptions(context);
        MutableLiveData<Boolean> isUserSignedIn = new MutableLiveData<>();
        return new AuthenticationRepository(context,activity, firebaseAuth, firebaseFirestore, firebaseApi, googleSignInClient, isUserSignedIn);
    }
    private static GoogleSignInClient setupGoogleSignInOptions(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(context,gso);
    }
}
