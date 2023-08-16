package com.example.go4lunch;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.di.Injection;
import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.viewmodel.MainActivityViewModel;
import com.example.go4lunch.views.ConnectedActivity;
import com.example.go4lunch.views.CreateAccountFragment;
import com.example.go4lunch.views.SignInFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements CreateAccountFragment.CreateAccountListener, SignInFragment.SignInListener {
    private static final int REQUEST_CODE = 1;
    private MainActivityViewModel mMainActivityViewModel;
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        com.example.go4lunch.databinding.ActivityMainBinding activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);

        AuthenticationRepository authenticationRepository = Injection.createAuthenticationRepository(this);
        mMainActivityViewModel = new MainActivityViewModel(authenticationRepository);
        //mMainActivityViewModel.setupGoogleSignInOptions();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)getNotificationPermission();

        mMainActivityViewModel.getUserData().observe(this, firebaseUser -> {
            if(firebaseUser != null) {
                Log.d("mainActivity", "Name: " + firebaseUser.getDisplayName());
                showMapsActivity(firebaseUser);
            }
        });
        mMainActivityViewModel.getAuthMessageMutableLiveData().observe(this, s -> {
            if(s!=null){
                String msg = "";
                if(s.equals("success")) msg = getString(R.string.auth_success);
                else if(s.equals("fail")) msg = getString(R.string.auth_failed);
                if(!msg.isEmpty())Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }

        });

        signInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d("signInLauncher", "login code: " + result.getResultCode());
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try{
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            mMainActivityViewModel.firebaseAuthWithGoogle(account.getIdToken());
                        } catch (ApiException e){
                            Log.w("TAG","SignInResult: failed code=" + e.getStatusCode());
                            Log.d("TAG", "SignIn Failed.");
                        }
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
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void getNotificationPermission(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_CODE);
            Log.d("mainActicity", "message NOT permissions granted");
        } else {
            Log.d("mainActicity", "message permissions granted");
            // Permission is already granted
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Log.d("mainActicity", "permission granted");
            } else {
                Log.d("mainActicity", "permission denied");
                // Permission denied
            }
        }
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