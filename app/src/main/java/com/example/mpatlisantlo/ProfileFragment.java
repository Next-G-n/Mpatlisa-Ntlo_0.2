package com.example.mpatlisantlo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.media.session.IMediaControllerCallback;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mpatlisantlo.Adapter.AdapterPosts;
import com.example.mpatlisantlo.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private TextView user_name,phone_number,email,location;
    private ImageView current_image,cover_image;
    private Button ft,post;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fShore;
    private FirebaseUser user;
    private LinearLayout loading;
    //private String UserID;
    private FirebaseUser UserID;
    //private FloatingActionButton ft;
    private FirebaseFirestore fstore;
    private DatabaseReference databaseReference;
    private DocumentReference documentReference;
    private ListenerRegistration listenerRegistration;
    private FirebaseDatabase firebaseDatabase;
    ProgressDialog pd;
    Dialog dialog;

    RecyclerView postsRecycleView;

    List<ModelPost> postList;
    AdapterPosts adapterPosts;
    String uid;


    //permissions constants
    private static  final int CAMERA_REQUEST_CODE=100;
    private static  final int STORAGE_REQUEST_CODE=200;
    private static  final int IMAGE_PICK_GALLERY_REQUEST_CODE=300;
    private static  final int CAMERA_PICK_CAMERA_REQUEST_CODE=400;

    String cameraPermission[];
    String storagePermission[];

    //Storage
    StorageReference storageReference;
    FirebaseStorage storage;
    //path where image of user profile and cover will be stored
    String StoragePath="User_Profile_Cover_Image/";

    //uri of image
    Uri image_uri;

    //for checking profile or cover
    String profileOrCover;
    ImageView user_icon;
    BottomNavigationView navigationView_top;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment

        user_name=view.findViewById(R.id.user_name);
        phone_number=view.findViewById(R.id.user_phoneNumber);
        email=view.findViewById(R.id.user_email);
        //cover_image=view.findViewById(R.id.CoverPhoto);
        current_image=view.findViewById(R.id.User_image);
        location=view.findViewById(R.id.user_location);
        ft=view.findViewById(R.id.float_button);
        fAuth=FirebaseAuth.getInstance();
        post=view.findViewById(R.id.upload_button);
        user_icon=view.findViewById(R.id.User_Icon3);
        dialog=new Dialog(getContext());
        navigationView_top=view.findViewById(R.id.navigationView_topBack);

        navigationView_top.setOnNavigationItemSelectedListener(selectedListener);


        postsRecycleView=view.findViewById(R.id.recycleView_post);

       // databaseReference1=fShore.getR
       // fShore=FirebaseFirestore.getInstance();
        UserID=fAuth.getCurrentUser();
        fShore = FirebaseFirestore.getInstance();
        loading= view.findViewById(R.id.back);
       storageReference= FirebaseStorage.getInstance().getReference();
        pd=new ProgressDialog(getActivity());
        postList=new ArrayList<>();

        checkUserStatus();

        //int array of permissions
        cameraPermission=new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        firebaseDatabase=FirebaseDatabase.getInstance();
      databaseReference=firebaseDatabase.getReference("Users");

        Query query=databaseReference.orderByChild("Email").equalTo(UserID.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //check until require data get
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    //get data
                    String name=""+ds.child("User_Name").getValue();
                    String user_email=""+ds.child("Email").getValue();
                    String phone=""+ds.child("Phone_Number").getValue();
                    String Location=""+ds.child("Location").getValue();
                    String user_image=""+ds.child("User_Image").getValue();

                    //set data

                    user_name.setText(name);
                    phone_number.setText(phone);
                    location.setText(Location);

                    email.setText(user_email);
                    try{
                        Picasso.get().load(user_image).into(current_image);
                    }catch (Exception e){
                        //if there is any execption  getting image then set default
                        Picasso.get().load(R.drawable.ic_user_icon);
                    }
                    try{
                        Picasso.get().load(user_image).into(user_icon);
                    }catch (Exception e){
                        //if there is any execption  getting image then set default
                        Picasso.get().load(R.drawable.ic_user_profile_foreground);
                    }



                    }

                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "onCancelled: error displaying"+databaseError.getMessage());

            }
        });

        if(postsRecycleView!=null){
            loadMyPost();
        }






       /* documentReference=fShore.collection("Users").document(UserID);
        listenerRegistration = documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                //assert documentSnapshot != null;
                if (documentSnapshot != null) {
                    phone_number.setText(documentSnapshot.getString("Phone Number"))
                    ;
                    user_name.setText(documentSnapshot.getString("User Name"));
                    email.setText(documentSnapshot.getString("Email"));

                    try {
                        Picasso.get().load(documentSnapshot.getString("User Image")).into(current_image);


                    } catch (Exception e1) {
                        Picasso.get().load(R.drawable.ic_add_image_foreground).into(current_image);

                        Log.d("TAG", "onEvent: error displaying image"+e1.getMessage());

                    }
                }else{
                    Log.d("tag'", "onEvent: read failed  "+e.getMessage());
                }




            }



        });*/



        //floating action button when clicked
        ft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),AddPostActivity.class));
                getActivity().finish();
            }
        });
        return view;

    }

    private void loadMyPost() {
        //linear layout for recycleVIew
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        //show newest post first,for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recycle view
        postsRecycleView.setLayoutManager(layoutManager);

        //init post list
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
        //qurey to load post
        Query query=ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelPost modelPost=ds.getValue(ModelPost.class);
                    postList.add(modelPost);
                    adapterPosts=new AdapterPosts(getActivity(),postList);
                    postsRecycleView.setAdapter(adapterPosts);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void SearchMyPost(final String searchQuery) {
        //linear layout for recycleVIew
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        //show newest post first,for this load from last
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recycle view
        postsRecycleView.setLayoutManager(layoutManager);

        //init post list
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
        //qurey to load post
        Query query=ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelPost modelPost=ds.getValue(ModelPost.class);

                    if(modelPost.getpTitle().toLowerCase().contains(searchQuery)||
                            modelPost.getpDescr().toLowerCase().contains(searchQuery)){
                        postList.add(modelPost);

                    }
                    adapterPosts=new AdapterPosts(getActivity(),postList);
                    postsRecycleView.setAdapter(adapterPosts);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private boolean CheckCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);
        boolean result2= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
    return result && result2;
    }

    private boolean CheckStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private  void requestStoragePermission(){
        requestPermissions(storagePermission, STORAGE_REQUEST_CODE);
    }

    private  void requestCameraPermission(){
        requestPermissions(cameraPermission, CAMERA_REQUEST_CODE);
    }

    private void showEditProfileDialog() {
        String options[]={"Edit Profile Picture","Edit Location","Edit Name","Edit Phone"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("choose Action");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    //edit profile image
                    pd.setMessage("Updating Image");
                    ShowImagePicDialog();
                    profileOrCover ="User_Image";


                }else if(which==1){
                    //edit Location
                    pd.setMessage("Updating location");
                    showNamePhoneUpdateDialog("Location");


                }else if(which==2){
                    //edit name
                    pd.setMessage("Updating name");
                    showNamePhoneUpdateDialog("User_Name");


                }else if(which==3){
                    //edit phone
                    pd.setMessage("Updating phone number");
                    showNamePhoneUpdateDialog("Phone_Number");


                }
            }
        });
        builder.create().show();
    }

    private void showNamePhoneUpdateDialog(final String key) {
        //cutomo
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Update "+key);
        LinearLayout linearLayout=new LinearLayout(getActivity());
        linearLayout.setOrientation(linearLayout.VERTICAL);
        linearLayout.setPadding(10,10,10,10);
        final EditText editText=new EditText(getActivity());
        editText.setHint("Enter  "+key);
        linearLayout.addView(editText);
        builder.setView(linearLayout);
        builder.setPositiveButton("update ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String valve = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(valve)) {

                    pd.show();

                    //dp
                    /*

                    // UserID = fAuth.getCurrentUser().getUid();
                    //documentReference = fstore.collection("Users").document(UserID);
                    // HashMap<String, Object> result = new HashMap<>();
                    //result.put(key, valve);
                    documentReference.update(key, valve).*/

                    HashMap<String,Object> result=new HashMap<>();
                    result.put(key,valve);
                    databaseReference.child(UserID.getUid()).updateChildren(result)


                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //progress bar
                            pd.dismiss();
                            Toast.makeText(getActivity(), "update successful", Toast.LENGTH_SHORT).show();


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //pragess bar
                            pd.dismiss();
                            Log.d("tag", "onFailure: update failed  "+e.getMessage());
                            Toast.makeText(getActivity(), "Error..!"+e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });
                    //if user edit his name, also change it from his post
                    if(key.equals("User_Name")){
                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts").child("Rent");
                        Query query=ref.orderByChild("uid").equalTo(uid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String Child =ds.getKey();
                                    dataSnapshot.getRef().child(Child).child("uName").setValue(valve);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("Comments")){
                                        String child1=""+dataSnapshot.child(child).getKey();
                                        Query child2=FirebaseDatabase.getInstance().getReference("Posts").child("Rent").child(child1).child("Comments").orderByChild("uid").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String child=ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("uName").setValue(valve);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("Comments")){
                                        String child1=""+dataSnapshot.child(child).getKey();
                                        Query child2=FirebaseDatabase.getInstance().getReference("Posts").child("Sell").child(child1).child("Comments").orderByChild("uid").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String child=ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("uName").setValue(valve);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("Comments")){
                                        String child1=""+dataSnapshot.child(child).getKey();
                                        Query child2=FirebaseDatabase.getInstance().getReference("Posts").child("Rent").child(child1).child("Bids").orderByChild("OgUserId").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String child=ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("OgUsername").setValue(valve);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("TopBids")){
                                        String child1=""+dataSnapshot.child(child).getKey();
                                        Query child2=FirebaseDatabase.getInstance().getReference("Posts").child("Rent").child(child1).child("Bids").child("TopBids").orderByChild("Bid_UserId").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String child=ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("Bid_Username").setValue(valve);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("Bids")){
                                        String child1=""+dataSnapshot.child(child).getKey();
                                        Query child2=FirebaseDatabase.getInstance().getReference("Posts").child("Sell").child(child1).child("Bids").orderByChild("OgUserId").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String child=ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("OgUsername").setValue(valve);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("Bids")){
                                        String child1=""+dataSnapshot.child(child).getKey();
                                        Query child2=FirebaseDatabase.getInstance().getReference("Posts").child("Sell").child(child1).child("Bids").child("TopBids").orderByChild("Bid_UserId").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String child=ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("Bid_Username").setValue(valve);

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                } else {

                }
            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    private void ShowImagePicDialog() {
        String options[]={"Camera","Gallery"};
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick Image Form");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    //camera clicked
                    if(!CheckCameraPermission()){
                        requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }

                }else if(which==1){
                    //gallery clicked
                    if(!CheckStoragePermission()){
                        requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean whiteStorageAccepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && whiteStorageAccepted){
                        //permission enable
                        pickFromCamera();
                    }else{
                        //permission denied
                        Toast.makeText(getActivity(), "Please enable camera & storage permission...", Toast.LENGTH_SHORT).show();

                    }
                }
                break;
            }
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean whiteStorageAccepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    if(whiteStorageAccepted){
                        //permission enable
                        pickFromGallery();
                    }else{
                        //permission denied
                        Toast.makeText(getActivity(), "Please enable camera & storage permission...", Toast.LENGTH_SHORT).show();

                    }
                }
                break;

            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       /* if (resultCode==RESULT_OK){
            if(requestCode==IMAGE_PICK_GALLERY_REQUEST_CODE){
                //image is picked from gallery,get uri of image
                image_uri=data.getData();
                uploadProfileCover(image_uri);

            }
            if(requestCode==CAMERA_PICK_CAMERA_REQUEST_CODE){
                uploadProfileCover(image_uri);

            }
        }*/


       try {
            //if (requestCode==1 && resultCode == RESULT_OK && data!=null && data.getData()!=null) {
                if (requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE ) {
                    //image is picked from gallery,get uri of image
                    image_uri = data.getData();
                    uploadProfileCover(image_uri);


                }
              else  if (requestCode == CAMERA_PICK_CAMERA_REQUEST_CODE ) {
                    //image picked from camera
                    uploadProfileCover(image_uri);


                //}
            }else {
                Log.d("TAG", "onActivityResult: Error ");
            }

        }catch (Exception e){
            Log.d("TAG", "onActivityResult: Error "+e.getMessage());
            Toast.makeText(getActivity(), "error"+e.getMessage(), Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
    }

    private void uploadProfileCover(final Uri uri) {
        pd.show();
        /*
        //test 1 with firebaseFirStore
        fShore=FirebaseFirestore.getInstance();
        UserID=fAuth.getCurrentUser().getUid();
         */

        //realtime database
        String filePathAndName= StoragePath+" "+profileOrCover+" "+UserID.getUid();
        StorageReference storageReference2nd=storageReference.child(filePathAndName);
/*
        //test 1 with firebaseFireStore
        String filePathAndName= StoragePath+" "+profileOrCover+" "+UserID;
        StorageReference storageReference2nd=storageReference.child(filePathAndName);

 */

        storageReference2nd.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                final Uri downloadUri=uriTask.getResult();
                if(uriTask.isSuccessful()){
                    //image url upload
                    //change code to firestorefirebase

                    //realtime database
                    HashMap<String,Object>result=new HashMap<>();
                    result.put(profileOrCover,downloadUri.toString());
                    databaseReference.child(UserID.getUid()).updateChildren(result)

/*
                    //test 1 with firebaseFireStore
                   // documentReference=fstore.collection("Users").document(UserID);
                    HashMap<String,Object>result=new HashMap<>();
                    result.put(profileOrCover,downloadUri.toString());
                    documentReference.update(result)

 */


                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(getActivity(),"Image Upload", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();


                            Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("tag", "onFailure: update image1 failed  "+e.getMessage());


                        }
                    });

                    if(profileOrCover.equals("User_Image")){
                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts").child("Rent");
                        Query query=ref.orderByChild("uid").equalTo(uid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String Child =ds.getKey();
                                    dataSnapshot.getRef().child(Child).child("uDp").setValue(downloadUri.toString());

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("Comments")){
                                        String child1=""+dataSnapshot.child(child).getKey();
                                        Query child2=FirebaseDatabase.getInstance().getReference("Posts").child("Rent").child(child1).child("Comments").orderByChild("uid").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String child=ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("Comments")){
                                        String child1=""+dataSnapshot.child(child).getKey();
                                        Query child2=FirebaseDatabase.getInstance().getReference("Posts").child("Sell").child(child1).child("Comments").orderByChild("uid").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String child=ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("Bids")){
                                        String child1=""+dataSnapshot.child(child).getKey();
                                        Query child2=FirebaseDatabase.getInstance().getReference("Posts").child("Rent").child(child1).child("Bids").orderByChild("OgUserId:").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String child=ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("OgDp").setValue(downloadUri.toString());

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("Bids")){
                                        String child1=""+dataSnapshot.child(child).getKey();
                                        Query child2=FirebaseDatabase.getInstance().getReference("Posts").child("Sell").child(child1).child("Bids").orderByChild("OgUserId:").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String child=ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("OgDp").setValue(downloadUri.toString());

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("TopBids")){
                                        String child1=""+dataSnapshot.child(child).getKey();
                                        Query child2=FirebaseDatabase.getInstance().getReference("Posts").child("Rent").child(child1).child("Bids").child("TopBids").orderByChild("Bid_UserId:").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String child=ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("Bid_Dp").setValue(downloadUri.toString());

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    String child =ds.getKey();
                                    if (dataSnapshot.child(child).hasChild("TopBids")){
                                        String child1=""+dataSnapshot.child(child).getKey();
                                        Query child2=FirebaseDatabase.getInstance().getReference("Posts").child("Sell").child(child1).child("Bids").child("TopBids").orderByChild("Bid_UserId:").equalTo(uid);
                                        child2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                                    String child=ds.getKey();
                                                    dataSnapshot.getRef().child(child).child("Bid_Dp").setValue(downloadUri.toString());

                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }



                }else {
                    //error
                    pd.dismiss();
                    Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //progresss bar
                Toast.makeText(getActivity(),e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("tag", "onFailure: update image failed  "+e.getMessage());
            }
        });

    }

    private void pickFromCamera() {
        //Intent of picking image from device camera
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        //put image uri
        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, CAMERA_PICK_CAMERA_REQUEST_CODE);

    }

    private void pickFromGallery() {
        //pick from gallery
        Intent galleryIntent=new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_REQUEST_CODE);


    }

    private  void checkUserStatus(){
        FirebaseUser user=fAuth.getCurrentUser();
        if(user !=null){
            uid=user.getUid();
        }else{
            startActivity(new Intent(getActivity(),LoginActivity.class));
            getActivity().finish();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener= new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.backButton:
                            startActivity(new Intent(getActivity(),MainPageActivity.class));
                            getActivity().finish();
                            return true;


                    }

                    return false;
                }
            };



}
