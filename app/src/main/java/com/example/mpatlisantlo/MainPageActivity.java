package com.example.mpatlisantlo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.Token;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainPageActivity extends AppCompatActivity implements LocationListener {
    private Button sign_out_btn;
    FirebaseAuth nAuth;
    ActionBar actionBar;
    BottomNavigationView navigationView_top, navigationVie_bottom;
    String myUid, myEmail;
    private LocationManager locationManager;
    private TextView mProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        actionBar = getSupportActionBar();
        navigationView_top = findViewById(R.id.navigationView_top);
        navigationVie_bottom = findViewById(R.id.navigationView_bottom);

        nAuth = FirebaseAuth.getInstance();


        navigationView_top.setOnNavigationItemSelectedListener(selectedListener);
        navigationVie_bottom.setOnNavigationItemSelectedListener(selectedListener2);
        checkUserStatues();
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        onLocationChanged(location);*/
        HomeFragment profile=new HomeFragment();
        FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
        ft1.replace(R.id.content,profile,"");
        ft1.commit();
        navigationView_top.setVisibility(View.VISIBLE);
        grantPermission();
        checkLocationIsEnabledOrNot();
        getLocation();


    }

public void updateToken(String token){
      DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
    //  @SuppressLint("RestrictedApi") Token mToken = new Token(token noneone);
     // ref.child(myUid).setValue(mToken);

}
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener2=new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.Home_button:
                            HomeFragment profile=new HomeFragment();
                            FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                            ft1.replace(R.id.content,profile,"");
                            ft1.commit();
                            navigationView_top.setVisibility(View.VISIBLE);
                            return true;
                        case R.id.Notification_button:
                            NotificationFragment notification=new NotificationFragment();
                            FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                            ft2.replace(R.id.content,notification,"");
                            ft2.commit();
                            navigationView_top.setVisibility(View.VISIBLE);
                            return true;
                        case R.id.all_users:
                            UserFragment all_user=new UserFragment();
                            FragmentTransaction ft3=getSupportFragmentManager().beginTransaction();
                            ft3.replace(R.id.content,all_user,"");
                            ft3.commit();
                            navigationView_top.setVisibility(View.GONE);
                            return true;
                        case R.id.Search_button:
                            SearchFragment search=new SearchFragment();
                            FragmentTransaction ft4=getSupportFragmentManager().beginTransaction();
                            ft4.replace(R.id.content,search,"");
                            ft4.commit();
                            navigationView_top.setVisibility(View.VISIBLE);
                            return true;

                    }
                    return false;
                }
            };
private BottomNavigationView.OnNavigationItemSelectedListener selectedListener= new
        BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()){
                   case R.id.SignOut_button:
                    //actionBar.setTitle("Log Out");
                         //  FirebaseAuth.getInstance().signOut();
                       FragmentManager manager = getSupportFragmentManager();
                       DialogFragment dialogFragment = new DialogFragment();
                       dialogFragment.show(manager,"DialogFragment");


                           //finish();
                       return true;
                   case R.id.user_avt:
                      // actionBar.setTitle("User Name");
                       ProfileFragment profile=new ProfileFragment();
                       FragmentTransaction ft1=getSupportFragmentManager().beginTransaction();
                       ft1.replace(R.id.content,profile,"");
                       ft1.commit();
                       navigationView_top.setVisibility(View.GONE);
                       return true;
                   case R.id.chat:
                   ChatListFragment chat=new ChatListFragment();
                   FragmentTransaction ft2=getSupportFragmentManager().beginTransaction();
                   ft2.replace(R.id.content,chat,"");
                   ft2.commit();
                   return true;

               }

                return false;
            }
        };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_bar,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void checkUserStatues(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            //user is signed in
            myEmail=user.getEmail();
            myUid=user.getUid();


        }else if(nAuth.getCurrentUser() ==null)
        {
            //User not signed in, go to the log in pages
          //  FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainPageActivity.this,LoginActivity.class));
            finish();
        }
    }
    private void grantPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
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
            new AlertDialog.Builder(MainPageActivity.this).setTitle("Enable GPS Service")
                    .setCancelable(false)
                    .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    }).setNegativeButton("Cancel",null).show();

        }
    }


    private void getLocation() {
        try{
            locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500,5,(LocationListener) this);
        }catch (SecurityException e){
            e.printStackTrace();
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
            userdb.put("User_Preferred_area", "works");
            userdb.put("User_Preferred_Location", userLocation);
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
