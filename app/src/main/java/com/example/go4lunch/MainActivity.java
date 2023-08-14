package com.example.go4lunch;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.di.Injection;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.viewmodel.MainActivityViewModel;
import com.example.go4lunch.views.ConnectedActivity;
import com.example.go4lunch.views.CreateAccountFragment;
import com.example.go4lunch.views.SignInFragment;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CreateAccountFragment.CreateAccountListener, SignInFragment.SignInListener {
    private MainActivityViewModel mMainActivityViewModel;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        com.example.go4lunch.databinding.ActivityMainBinding activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);

        AuthenticationRepository authenticationRepository = Injection.createAuthenticationRepository(this, this);
        mMainActivityViewModel = new MainActivityViewModel(authenticationRepository);
        //mMainActivityViewModel.setupGoogleSignInOptions();
        getNotificationPermission();

        mMainActivityViewModel.getUserData().observe(this, firebaseUser -> {
            if(firebaseUser != null) {
                System.out.println("Name: " + firebaseUser.getDisplayName());
                showMapsActivity(firebaseUser);
            }
        });

        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d("signInLauncher", "loggin code: " + result.getResultCode());
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        mMainActivityViewModel.handleSignInResult(result.getData());
                        Log.d("signInLauncher", "has launched");
                    } else {
                        Log.d("signInLauncher", "failed to launch");
                    }
                });

        activityMainBinding.gmailSigninBtn.setOnClickListener(view1 -> {
            Intent intent = mMainActivityViewModel.signIn();
            signInLauncher.launch(intent);
        });
        activityMainBinding.mainCreateAccountBtn.setOnClickListener(view12 -> {
            DialogFragment createAccountFragment = new CreateAccountFragment();
            createAccountFragment.show(getSupportFragmentManager(), getString(R.string.create_account));
        });
        activityMainBinding.loginSigninBtn.setOnClickListener(view13 -> {
            SignInFragment signInFragment = new SignInFragment();
            signInFragment.show(getSupportFragmentManager(), getString(R.string.sign_in));
        });
    }
    private void showMapsActivity(FirebaseUser account) {
        Intent intent = new Intent(this, ConnectedActivity.class);
        intent.putExtra("name", account.getDisplayName());
        startActivity(intent);
        finish();
    }
    private void getNotificationPermission(){
        Dexter.withContext(this).withPermissions(Manifest.permission.POST_NOTIFICATIONS).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                    Log.d("mainActicity", "message permissions granted");

                }

                // check for permanent decline of any permission
                if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                    Log.d("mainActicity", "message NOT permissions granted");

                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).onSameThread().check();

    }

    @Override
    public void userInformation(String displayName, String email, String password) {
        Log.d("userInformation", "creating this user: " + displayName + " email: " + email);
        mMainActivityViewModel.firebaseCreateUser(email,password,displayName);
    }

    @Override
    public void userSingInInformation(String email, String password) {
        Log.d("userInformation", "Sign in with this email " + email);
        mMainActivityViewModel.signInWithEmail(email,password);
    }
}