package com.example.go4lunch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.go4lunch.injection.ViewModelFactory;
import com.example.go4lunch.viewmodel.MainActivityViewModel;
import com.example.go4lunch.views.MapsActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private MainActivityViewModel mMainActivityViewModel;
    private Button signInWithGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInWithGoogle = findViewById(R.id.gmail_signin_btn);

        mMainActivityViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(MainActivityViewModel.class);
        mMainActivityViewModel.setupGoogleSignInOptions();

       /*try {
            String signoutStatus = getIntent().getExtras().getString("signout");

            if (signoutStatus.equals("true")) mMainActivityViewModel.signOut();
        }catch (Exception e){}*/
        System.out.println("in Main Activity");
        mMainActivityViewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser != null) {
                    System.out.println("Name: " + firebaseUser.getDisplayName());
                    showMapsActivity(firebaseUser);
                }
            }
        });

        signInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivityViewModel.signIn();
            }
        });
    }
    private void showMapsActivity(FirebaseUser account) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("name", account.getDisplayName());
        startActivity(intent);
        finish();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode,data);

        System.out.println("an activity result");
        if(requestCode == mMainActivityViewModel.getGOOGLE_SIGN_IN()){
            mMainActivityViewModel.handleSignInResult(data);
        }
    }
}