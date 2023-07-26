package com.example.go4lunch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.go4lunch.repository.AuthenticationRepository;
import com.example.go4lunch.viewmodel.MainActivityViewModel;
import com.example.go4lunch.views.ConnectedActivity;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MainActivityViewModel mMainActivityViewModel;
    private AuthenticationRepository mAuthenticationRepository;
    private Button signInWithGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInWithGoogle = findViewById(R.id.gmail_signin_btn);

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

        signInWithGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivityViewModel.signIn();
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

}