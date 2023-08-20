package com.example.go4lunch;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import com.example.go4lunch.util.NetworkUtils;
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
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        com.example.go4lunch.databinding.ActivityMainBinding activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = activityMainBinding.getRoot();
        setContentView(view);

        AuthenticationRepository authenticationRepository = Injection.createAuthenticationRepository(this);
        mMainActivityViewModel = new MainActivityViewModel(authenticationRepository);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)getNotificationPermission();

        isConnected = NetworkUtils.isNetworkAvailable(this);
        if (!isConnected){
            Toast.makeText(getApplicationContext(), "no internet! The app can not function", Toast.LENGTH_SHORT).show();
        }

        setUpObservers();

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
                            Toast.makeText(getApplicationContext(), getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                        }
                        Log.d("signInLauncher", "has launched");
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
                        Log.d("signInLauncher", "failed to launch");
                    }
                });

        setButtonsClickListeners(activityMainBinding);
    }

    private void setUpObservers() {
        mMainActivityViewModel.getUserData().observe(this, firebaseUser -> {
            if(firebaseUser != null && isConnected) {
                Log.d("mainActivity", "Name: " + firebaseUser.getDisplayName());
                showMapsActivity(firebaseUser);
            }
        });
        mMainActivityViewModel.getAuthMessageMutableLiveData().observe(this, s -> {
            if(s!=null){
                String msg;
                if(s.equals("success")){
                    msg = getString(R.string.auth_success);
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
                else{
                    msg = getString(R.string.auth_failed) + ": " + s;
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            }

        });
    }

    private void setButtonsClickListeners(ActivityMainBinding activityMainBinding) {
        activityMainBinding.gmailSigninBtn.setOnClickListener(view1 -> {
            if(isConnected){
                Intent intent = mMainActivityViewModel.signIn();
                signInLauncher.launch(intent);
            }else Toast.makeText(getApplicationContext(), "no internet!", Toast.LENGTH_SHORT).show();

        });
        activityMainBinding.mainCreateAccountBtn.setOnClickListener(view12 -> {
            if (isConnected){
                DialogFragment createAccountFragment = new CreateAccountFragment();
                createAccountFragment.show(getSupportFragmentManager(), getString(R.string.create_account));
            }else Toast.makeText(getApplicationContext(), "no internet!", Toast.LENGTH_SHORT).show();
        });
        activityMainBinding.loginSigninBtn.setOnClickListener(view13 -> {
            if (isConnected) {
                SignInFragment signInFragment = new SignInFragment();
                signInFragment.show(getSupportFragmentManager(), getString(R.string.sign_in));
            }else Toast.makeText(getApplicationContext(), "no internet!", Toast.LENGTH_SHORT).show();
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
            // Permission is already granted
            Log.d("mainActicity", "message permissions granted");
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Log.d("mainActicity", "permission granted");
            } else {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, this.getPackageName());
                this.startActivity(intent);
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