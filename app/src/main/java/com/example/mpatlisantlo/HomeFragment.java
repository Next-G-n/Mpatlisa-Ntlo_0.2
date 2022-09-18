package com.example.mpatlisantlo;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mpatlisantlo.Adapter.AdapterBid;
import com.example.mpatlisantlo.Adapter.AdapterPostLatest;
import com.example.mpatlisantlo.Adapter.AdapterPostLiked;
import com.example.mpatlisantlo.Adapter.AdapterPosts;
import com.example.mpatlisantlo.Adapter.AdapterPostsRecommended;
import com.example.mpatlisantlo.models.ModelBids;
import com.example.mpatlisantlo.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static android.media.MediaCodec.MetricsConstants.MODE;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements LocationListener {

    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView,recyclerView3,recyclerView2,recyclerView4;
    List<ModelPost>postList;
    AdapterPosts adapterPost;
    TextView phone_Number,close_btn;
    Button submet;
    AdapterPostLiked adapterPostLiked;
    AdapterPostLatest adapterPostLatest;
    AdapterPostsRecommended adapterPostsRecommended;
    ImageView imageView;
    RadioGroup userType;
    private FirebaseAuth nAuth;
    private DatabaseReference databaseReference;
    private FirebaseAuth fAuth;
    private FirebaseUser UserID;
    CardView landlordView;
    SwitchCompat switchCompat;
    RelativeLayout layout;
    LinearLayout display1;
    private DatabaseReference usersDbRef,usersDbRef2;
    FirebaseDatabase firebaseDatabase;

    String myUid;
    Dialog dialog;
    int backpress;
    private LocationManager locationManager;

    private DatabaseReference UsersRef,PostsRef;
    String postId;

    String spinner;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            boolean doubleBackToExitPressedOnce=false;
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
               /* if (doubleBackToExitPressedOnce) {
                    doubleBackToExitPressedOnce = true;
                    requireActivity().onBackPressed()
                    backpress = (backpress + 1);
                    System.exit(0);
                }else{

                }*/
                if (doubleBackToExitPressedOnce) {
                    //ActivityCompat.finishAffinity( getActivity() );
                    Log.e( "Click", "double back" );
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(getContext(), "Double click back to exit",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed( new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                        System.exit(0);
                    }
                }, 2000);
                fAuth=FirebaseAuth.getInstance();
                UserID=fAuth.getCurrentUser();

            }
        };
             /*  // Toast.makeText(getContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

                private void closeFragment() {
                    // Disable to close fragment
                    this.setEnabled(false);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                }
            }
        };*/
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        imageView=view.findViewById(R.id.pImageIV);
        layout=view.findViewById(R.id.postRecycleImageView);
        switchCompat=view.findViewById(R.id.switchButton);
        display1=view.findViewById(R.id.LayoutDisplay1);
        myUid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        dialog=new Dialog(getContext());
        landlordView=dialog.findViewById(R.id.completeDetails);



        //recycle view and its properties
        recyclerView=view.findViewById(R.id.postRecycleView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(layoutManager);



        //recycle view and its properties
        recyclerView2=view.findViewById(R.id.postRecycleView2);
        LinearLayoutManager layoutManager2=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        layoutManager2.setStackFromEnd(true);
        layoutManager2.setReverseLayout(true);

        recyclerView2.setLayoutManager(layoutManager2);


        //recycle view and its properties
        recyclerView4=view.findViewById(R.id.postRecycleView4);
        LinearLayoutManager layoutManager4=new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        layoutManager4.setStackFromEnd(true);
        layoutManager4.setReverseLayout(true);

        recyclerView4.setLayoutManager(layoutManager4);
        checkInfo();

        SharedPreferences sharedPreferences= Objects.requireNonNull(getContext()).getSharedPreferences("Save", Context.MODE_PRIVATE);
        switchCompat.setChecked(sharedPreferences.getBoolean("value",true));



        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switchCompat.isChecked()){
                    loadAllPosts("Rent");
                    loadPostsRecommend("Rent");
                    loadPostsLatest("Rent");
                    loadPostsLiked("Rent");
                }else{
                    loadAllPosts("Sell");
                    loadPostsRecommend("Sell");
                    loadPostsLatest("Sell");
                    loadPostsLiked("Sell");
                }
            }
        });

        //init post
        postList=new ArrayList<>();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(switchCompat.isChecked()){
            loadAllPosts("Rent");
            loadPostsRecommend("Rent");
            loadPostsLatest("Rent");
            loadPostsLiked("Rent");

        }else{
            loadAllPosts("Sell");
            loadPostsRecommend("Sell");
            loadPostsLatest("Sell");
            loadPostsLiked("Sell");
        }
    }


    private void loadAllPosts(String spinner2) {
        //path of all post
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts").child(spinner2);
        ref.orderByChild("pId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelPost modelPost=ds.getValue(ModelPost.class);

                    postList.add(modelPost);
                    adapterPost= new AdapterPosts(getActivity(),postList);
                    //set adaptet to recycle
                    recyclerView4.setAdapter(adapterPost);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                //  Toast.makeText(getActivity(), "Error "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadPostsRecommend(String spinner2) {
        //path of all post
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts").child(spinner2);
        ref.orderByChild("pId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelPost modelPost=ds.getValue(ModelPost.class);

                    postList.add(modelPost);
                    adapterPostsRecommended= new AdapterPostsRecommended(getActivity(),postList);
                    //set adaptet to recycle
                    recyclerView2.setAdapter(adapterPostsRecommended);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                //  Toast.makeText(getActivity(), "Error "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadPostsLatest(String spinner2) {
        //path of all post
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts").child(spinner2);
        ref.orderByChild("Price").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                postList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelPost modelPost=ds.getValue(ModelPost.class);

                    postList.add(modelPost);

                    adapterPostLatest= new AdapterPostLatest(getActivity(),postList);
                    //set adaptet to recycle
                    recyclerView.setAdapter(adapterPostLatest);




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                //  Toast.makeText(getActivity(), "Error "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void loadPostsLiked(String spinner2) {
        //path of all post
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts").child(spinner2);
        ref.orderByChild("pLike").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    postList.clear();
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        ModelPost modelPost=ds.getValue(ModelPost.class);

                        postList.add(modelPost);
                        adapterPostLiked= new AdapterPostLiked(getActivity(),postList);
                        //set adaptet to recycle

                       // recyclerView3.setAdapter(adapterPostLiked);
                        //recyclerView3.setItemViewCacheSize(3);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                //  Toast.makeText(getActivity(), "Error "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void searchPost(String searchQuery){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
        ref.orderByChild("pId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelPost modelPost=ds.getValue(ModelPost.class);
                   // if(modelPost.getpTitle().contains()||modelPost.getpDescr())

                    postList.add(modelPost);
                    adapterPost= new AdapterPosts(getActivity(),postList);

                    recyclerView.setAdapter(adapterPost);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                //Toast.makeText(getActivity(), "Error "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void grantPermission() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

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

 /*  @Override
 public void onBackPressed() {
     backpress = (backpress + 1);
     Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

     if (backpress > 1) {
         super.onBackPressed();
     }
        */


    private void getData(){
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
        dialog.setContentView(R.layout.prefference);
        phone_Number=(EditText)dialog.findViewById(R.id.enterPhoneNumber);
        submet=(Button)dialog.findViewById(R.id.sumbit);
        userType=(RadioGroup)dialog.findViewById(R.id.type_of_user);
        close_btn=(TextView)dialog.findViewById(R.id.close_btn2);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        nAuth=FirebaseAuth.getInstance();
        final String uid=nAuth.getCurrentUser().getUid();
                    final String[] team = new String[1];
                    userType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                            RadioButton rb= (RadioButton) dialog.findViewById(checkedId);
                            team[0] = rb.getText().toString();
                        }
                    });
                    String test1=phone_Number.getText().toString();

                        Map<String, Object> userdb = new HashMap<>();
                        userdb.put("Phone_Number",test1 );
                        userdb.put("User_Type",team);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("Users");
                        reference.child(myUid).updateChildren(userdb);







    }

    private  void checkInfo(){
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference("Users");
        recyclerView3=(RecyclerView)dialog.findViewById(R.id.recycleView_Bids);
        Query query=databaseReference.orderByChild("uid").equalTo(myUid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until require data get
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //get data
                    String name=""+ds.child("User_Name").getValue();
                    String user_email=""+ds.child("User_Type").getValue();
                    String phone=""+ds.child("Phone_Number").getValue();

                    //set data
                    if(phone.equals("none") || user_email.equals("none")){
                        landlordView.setVisibility(View.VISIBLE);
                   getData();
                    }else{

                    }



                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "onCancelled: error displaying"+databaseError.getMessage());

            }
        });
    }
}
