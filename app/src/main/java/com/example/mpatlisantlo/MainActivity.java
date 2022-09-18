package com.example.mpatlisantlo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.TestLooperManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.example.mpatlisantlo.R.id.Connect_with_facebook;
import static com.example.mpatlisantlo.R.id.Create_Account;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private static final String TAG = "FacebookAuthentication";
    private Button create_acc_btn, join_with_facebook_btn,test;
    private TextView login;
    private FirebaseAuth nAuth;
    private CallbackManager callbackManager;
    private TextView textViewUser;
    private ImageView mlogo;
    private FirebaseAuth.AuthStateListener authStateListener;
    private int backpress=0;
    private LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //    getApplicationContext();
        // AppEventsLogger.activateApp(getApplication());
        // FacebookSdk.sdkInitialize(getApplicationContext());
        // AppEventsLogger.activateApp(this);
        // AppEventsLogger.newLogger(application, application.getString(R.string.facebook_app_id));
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);
        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(getApplication());

        //  FacebookSdk.sdkInitialize(getApplicationContext());
        // AppEventsLogger.activateApp(this);

        create_acc_btn = (Button) findViewById(Create_Account);
        // join_with_facebook_btn = (Button) findViewById(Connect_with_facebook);
        LoginButton facebookloginButton = (LoginButton) findViewById(Connect_with_facebook);
        facebookloginButton.setReadPermissions("email","public_profile");
        //login=0
        nAuth=FirebaseAuth.getInstance();


        facebookloginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>(){

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"OnSuccess"+ loginResult);
                handleFacebookToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG,"OnCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"OnError"+ error);
            }
        });


        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){
                    updateUI(user);
                    loadData(user);
                }else{
                    updateUI(null);
                }
            }
        };

        AccessTokenTracker  accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken ==null){
                    nAuth.signOut();
                }
            }
        };

        if(nAuth.getCurrentUser() !=null) {
            startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
            finish();


        }

        create_acc_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }

    private void getLocation() {
        try{
            locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500,5,(LocationListener) this);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private void checkLocationIsEnabledOrNot() {
        LocationManager lm= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled=false;
        boolean networkEnabled=false;
        try{
            gpsEnabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(!gpsEnabled && !networkEnabled){
            new AlertDialog.Builder(MainActivity.this).setTitle("Enable GPS Service")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("Cancel",null).show();

        }
    }

    private void grantPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }
    }

    private void handleFacebookToken(AccessToken accessToken) {
        Log.d(TAG,"handleFacebookToken" + accessToken);

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        nAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(TAG,"sign with credential: successful");
                    FirebaseUser user = nAuth.getCurrentUser();
                    updateUI(user);
                } else{
                    Log.d(TAG,"Sign in failed", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI(FirebaseUser user) {
        if (user !=null){
            Intent intent=new Intent(MainActivity.this,HomeFragment.class);
            finish();

        }else{
            Toast.makeText(this, "Please sign in to continue", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {

        backpress = (backpress + 1);
        Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

        if (backpress > 1) {
            super.onBackPressed();
        }


    }

    private void loadData(FirebaseUser user){
        if(user !=null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            if(email==null){
                email="null";
            }
            String uid=nAuth.getCurrentUser().getUid();
            String photoURL=user.getPhotoUrl().toString();
            Map<String, Object> userdb = new HashMap<>();
            userdb.put("User_Name", name);
            userdb.put("uid", uid);
            userdb.put("Email", email);
            userdb.put("Phone_Number", "none");
            userdb.put("User_Type","none");
            userdb.put("User_Image", photoURL);
            userdb.put("onlineStatus", "online");
            userdb.put("Location", "userLocation");
            userdb.put("typingTo", "noOne");
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("Users");
            reference.child(uid).setValue(userdb);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        nAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener!=null){
            nAuth.removeAuthStateListener(authStateListener);
        }
    }


    @Override
    public void onLocationChanged(Location location) {

        try{
            Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String userLocation=addresses.get(0).getLocality();
            String uid=nAuth.getCurrentUser().getUid();
            Map<String, Object> userdb = new HashMap<>();
            userdb.put("Location", userLocation);
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("Users");
            reference.child(uid).updateChildren(userdb);


        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

