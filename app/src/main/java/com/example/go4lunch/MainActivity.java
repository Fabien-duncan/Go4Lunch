package com.example.go4lunch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
    private AuthenticationRepository mAuthenticationRepository;
    private Button signInWithGoogle_btn;
    private Button createAccount_btn;
    private Button signIn_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInWithGoogle_btn = findViewById(R.id.gmail_signin_btn);
        createAccount_btn = findViewById(R.id.main_create_account_btn);
        signIn_btn = findViewById(R.id.login_signin_btn);

        mAuthenticationRepository = new AuthenticationRepository(this);
        mMainActivityViewModel = new MainActivityViewModel(mAuthenticationRepository);
        //mMainActivityViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(MainActivityViewModel.class);
        mMainActivityViewModel.setupGoogleSignInOptions();
        getNotificationPermission();
      /*  if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }*/

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

        signInWithGoogle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivityViewModel.signIn();
            }
        });
        createAccount_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment createAccountFragment = new CreateAccountFragment();
                createAccountFragment.show(getSupportFragmentManager(), "Create Account");
            }
        });
        signIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInFragment signInFragment = new SignInFragment();
                signInFragment.show(getSupportFragmentManager(), "Sign in");
            }
        });
    }
    private void showMapsActivity(FirebaseUser account) {
        Intent intent = new Intent(this, ConnectedActivity.class);
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