package com.example.mpatlisantlo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mpatlisantlo.Adapter.AdapterImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import com.google.mlkit.vision.objects.defaults.PredefinedCategory;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddPostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    FirebaseAuth firebaseAuth;
    EditText title,descriptionEt,price1;
    ImageView imageView,user_icon;
    Button upload;
    RelativeLayout relativeLayout;
    Uri imge_uri=null;
    Spinner spinner,plocation,pRegion;
    DatabaseReference UserDbRef;
    private static final String[] paths = {"Rent","Sell"};
    //private FirebaseUser UserID;
    RecyclerView recyclerView;
    ArrayList<String> imageUrls;
    AdapterImageView adapterImageView;

    String name,email,uid,dp;
    String editTitle, editDescription,editImage,UserID;


    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;


    private StorageReference PostsImagesRefrence;
    private DatabaseReference UsersRef, PostsRef;

    Spinner spinner1,spinner2;
    List<String> subCategories = new ArrayList<>();


    //permission array
    String [] cameraPermission;
    String[] storagePermission;
    BottomNavigationView navigationView_top;

    //progress bar
    ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        title=findViewById(R.id.pTitleEt);
        descriptionEt=findViewById(R.id.pDescriptionET);

        //pRegion=findViewById(R.id.region);
        imageView=findViewById(R.id.pImageIv);
        upload=findViewById(R.id.pUpload_button);
        //recyclerView=findViewById(R.id.pImageIv2);
        price1=findViewById(R.id.price);
        //relativeLayout=findViewById(R.id.Layout2);
        user_icon=findViewById(R.id.user_Icon3);
        navigationView_top=findViewById(R.id.navigationView_topBack);

        navigationView_top.setOnNavigationItemSelectedListener(selectedListener);


        spinner = (Spinner)findViewById(R.id.Type_of_house);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddPostActivity.this,
                android.R.layout.simple_spinner_item,paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        List<String> category = new ArrayList<>();
        category.add("Francistown");
        category.add("Gaborone");
        category.add("Selibe Phikwe");
        category.add("Tutume");
        category.add("Serowe");
        category.add("Mahalapye");
        category.add("Palapye");
        category.add("Orapa");
        category.add("Jwaneng");

        plocation=(Spinner) findViewById(R.id.plocation2);
       // spinner = (Spinner)findViewById(R.id.Type_of_house);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(AddPostActivity.this,
                android.R.layout.simple_spinner_item,category);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        plocation.setAdapter(adapter2);
        plocation.setOnItemSelectedListener(this);

        pRegion=(Spinner) findViewById(R.id.pRegion);
        // spinner = (Spinner)findViewById(R.id.Type_of_house);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(AddPostActivity.this,
                android.R.layout.simple_spinner_item,paths);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pRegion.setAdapter(adapter3);
        pRegion.setOnItemSelectedListener(this);

        //get data through intent from previous activities adapter//posts method showMoreOptions()
        Intent intent=getIntent();
        final String isUpdateKey=""+intent.getStringExtra("key");
        final String editPostId=""+intent.getStringExtra("editPostId");
        //validate if we came here to update post i.e came from AdapterPost
        if(isUpdateKey.equals("editPost")){
            //update
            upload.setText("Update");
            loadPostData(editPostId);
        }else{
            //add

            upload.setText("Upload");

        }

        PostsImagesRefrence = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");


        firebaseAuth=FirebaseAuth.getInstance();
        UserID=firebaseAuth.getCurrentUser().getUid();
        checkUserStatus();
        uploadMyImage();


        //get info of current user
        UserDbRef=FirebaseDatabase.getInstance().getReference("User");
       final Query query=UserDbRef.orderByChild("Email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    //get data

                      name=""+ds.child("User_Name").getValue();
                      email=""+ds.child("Email").getValue();
                      dp=""+ds.child("User_Image").getValue();

                    try{
                        Picasso.get().load(dp).placeholder(R.drawable.ic_user_profile_foreground).into(user_icon);

                    }catch (Exception e) {

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "onCancelled: Error with the Query"+databaseError.getMessage());
            }
        });




        //init permission array
        cameraPermission=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        pd=new ProgressDialog(this);

        //get image from camera/gallery on click
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show image pick
                showImagePickDialog();
            }
        });

        //upload button click lister
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data(title,description) from EditTexts
                String title2=title.getText().toString().trim();
                String location=(String)plocation.getSelectedItem();
                String region=(String)pRegion.getSelectedItem();
                String price2=price1.getText().toString().trim();
                String description=descriptionEt.getText().toString().trim();
                String Spinner2= (String) spinner.getSelectedItem();



                if(TextUtils.isEmpty(title2)){
                    Toast.makeText(AddPostActivity.this, "Enter title...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(description)){
                    Toast.makeText(AddPostActivity.this, "Enter description...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(location)){
                    Toast.makeText(AddPostActivity.this, "Enter location...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(region)){
                    Toast.makeText(AddPostActivity.this, "Enter Region...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(price2)){
                    Toast.makeText(AddPostActivity.this, "Enter Price...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(imageView.getDrawable()==null){
                    Toast.makeText(AddPostActivity.this, "Enter add Image...", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(isUpdateKey.equals("editPost")){
                    updateWithNowImage(title2,description,editPostId,location,region,price2,Spinner2);
                }else{
                   uploadData(title2,description,location,region,price2,Spinner2);
                }

            }
        });


        //Spinner





        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, category);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pRegion.setAdapter(dataAdapter);

        plocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView.getItemAtPosition(i).equals("Francistown")) {
                    subCategories.clear();
                    subCategories.add("block 1");
                    subCategories.add("block 2");
                    subCategories.add("block 3");
                    subCategories.add("block 4");
                    subCategories.add("block 5");
                    subCategories.add("block 6");
                    subCategories.add("block 7");
                    subCategories.add("block 8");
                    subCategories.add("block 9");
                    subCategories.add("block 10");
                    subCategories.add("Monarch");
                    subCategories.add("Donga 12");
                    subCategories.add("Donga motse");
                    subCategories.add("Area A");
                    subCategories.add("Area G");
                    fillSpinner();
                } else if (adapterView.getItemAtPosition(i).equals("Gaborone")) {
                    subCategories.clear();
                    subCategories.add("Tsolamosese");
                    subCategories.add("Tlokweng");
                    subCategories.add("Phakalane");
                    subCategories.add("Mogoditshane");
                    subCategories.add("Broadhurst");
                    subCategories.add("Gabane");
                    subCategories.add("Mmopane");

                    fillSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    public void fillSpinner(){
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, subCategories);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pRegion.setAdapter(dataAdapter2);
    }






    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {


        switch (position) {
            case 0:
                // error when nothing selected
                ((TextView)spinner.getSelectedView()).setError("Error message");
                break;
            case 1:
                // Whatever you want to happen when the second item gets selected
                ;
                break;
            case 2:
                // Whatever you want to happen when the second item gets selected
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener= new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.backButton:
                            startActivity(new Intent(getApplicationContext(),MainPageActivity.class));
                            finish();
                            return true;


                    }

                    return false;
                }
            };

    private void uploadData(final String title2, final String description, final String location, final String region, final String price2, final String spinner2) {

        pd.setMessage("Publishing post");
        pd.show();

        Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        //image compress
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte [] data=baos.toByteArray();

        //for post_image name,post_id,past_publish_time
        final String timeStamp= String.valueOf(System.currentTimeMillis());

        String filePathAndName ="Post/"+"post_"+timeStamp;

        //post with image
        StorageReference ref= FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                final String downloadUri=uriTask.getResult().toString();

                UsersRef.child(UserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {

                            String name1 = "" + dataSnapshot.child("User_Name").getValue().toString();
                            String phone=""+dataSnapshot.child("Phone_Number").getValue().toString();
                            String email1 = "" + dataSnapshot.child("Email").getValue().toString();
                            String dp1 = "" + dataSnapshot.child("User_Image").getValue().toString();


                            HashMap<Object, String> hashMap = new HashMap<>();
                            hashMap.put("uid", uid);
                            hashMap.put("uName", name1);
                            hashMap.put("uEmail", email1);
                            hashMap.put("uDp", dp1);
                            hashMap.put("pId", timeStamp);
                            hashMap.put("pTitle", title2);
                            hashMap.put("pDescr", description);
                            hashMap.put("pImage", downloadUri);
                            hashMap.put("pTime", timeStamp);
                            hashMap.put("pLocation",location);
                            hashMap.put("Price",price2);
                            hashMap.put("pRegion",region);
                            hashMap.put("pLike", "0");
                            hashMap.put("pComments", "0");
                            hashMap.put("PropertyType",spinner2);
                            hashMap.put("Response","none");
                            hashMap.put("pPhoneNumber",phone);
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts").child(spinner2);
                            ref.child(timeStamp).setValue(hashMap);
                            HashMap<String,Object> result2=new HashMap<>();
                            result2.put("Original_Price","");
                            result2.put("OgUsername","");
                            result2.put("OgDp","");
                            result2.put("OgUserId","");
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference("Posts");
                            ref2.child(spinner2).child(timeStamp).child("Bids").updateChildren(result2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(AddPostActivity.this, "Post published...", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(AddPostActivity.this,MainPageActivity.class));
                                    finish();
                                    title.setText("");
                                    descriptionEt.setText("");
                                    imageView.setImageURI(null);
                                    imge_uri = null;

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(AddPostActivity.this, "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




                        }




        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: error with loading image"+e.getMessage());
            }
        });

    }

    private void checkUserStatus() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if(user!=null){
            email=user.getEmail();
            uid=user.getUid();
        }else{
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }


    }

   /* private void beginUpload(String title2, String description, String editPostId) {
        pd.setMessage("Updating Post...");
        pd.show();
        if(!editImage.equals("noImage")){
            //without image
            updateWasWithImage(title2,description,editPostId);
        }else if(imageView.getDrawable()!=null){
            //with image
            updateWithNowImage(title2,description,editPostId);

        }else {
            //without image
            updateWithoutImage(title2,description,editPostId);
        }
    }*/

   /* private void updateWithoutImage(String title2, String description, String editPostId) {
        HashMap<String,Object>hashMap=new HashMap<>();
        //put post info
        hashMap.put("uid",uid);
        hashMap.put("uName",name);
        hashMap.put("uEmail",email);
        hashMap.put("uDp",dp);
        hashMap.put("pTitle",title2);
        hashMap.put("pDescr",description);
        hashMap.put("pImage","noImage");
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
        ref.child(editPostId).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(AddPostActivity.this, "Update...", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddPostActivity.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    */

    private void updateWithNowImage(final String title2, final String description, final String editPostId, final String location, final String region, final String price2, String spinner2) {
        pd.setMessage("Updating Post...");
        pd.show();

        String timestamp=String.valueOf(System.currentTimeMillis());
        String filePathAndName="post/"+"post"+timestamp;
        //get image from imageView
        Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        //image compress
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte [] data=baos.toByteArray();
        StorageReference ref=FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image ipload get itd url
                        Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while(!uriTask.isSuccessful());
                        final String downloadUri=uriTask.getResult().toString();
                        if (uriTask.isSuccessful()){
                            UsersRef.child(UserID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String name1 = "" + dataSnapshot.child("User_Name").getValue().toString();
                                    String email1 = "" + dataSnapshot.child("Email").getValue().toString();
                                    String dp1 = "" + dataSnapshot.child("User_Image").getValue().toString();

                                    //uri is received , upload to firebase database
                                    HashMap<String,Object>hashMap=new HashMap<>();
                                    //put post info
                                    hashMap.put("uid",uid);
                                    hashMap.put("uName",name1);
                                    hashMap.put("uEmail",email);
                                    hashMap.put("uDp",dp1);
                                    hashMap.put("pTitle",title2);
                                    hashMap.put("pDescr",description);
                                    hashMap.put("pImage",downloadUri);
                                    hashMap.put("pLocation",location);
                                    hashMap.put("Price",price2);
                                    hashMap.put("pRegion",region);
                                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
                                    ref.child(editPostId).updateChildren(hashMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    pd.dismiss();
                                                    Toast.makeText(AddPostActivity.this, "Update...", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(AddPostActivity.this,MainPageActivity.class));
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(AddPostActivity.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });








                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddPostActivity.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
    });
    }


    private void updateWasWithImage(final String title2, final String description, final String editPostId) {
        //post is with image,delete previous image first
        StorageReference storageReference=FirebaseStorage.getInstance().getReferenceFromUrl(editImage);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //image deleted, upload new image
                        //for post-image name,post-id , publish-time
                        String timestamp=String.valueOf(System.currentTimeMillis());
                        String filePathAndName="post/"+"post"+timestamp;
                        //get image from imageView
                        Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos=new ByteArrayOutputStream();
                        //image compress
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                        byte [] data=baos.toByteArray();
                        StorageReference ref=FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        ref.putBytes(data)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        //image ipload get itd url
                                        Task<Uri>uriTask=taskSnapshot.getStorage().getDownloadUrl();
                                        while(!uriTask.isSuccessful());
                                            String downloadUri=uriTask.getResult().toString();
                                            if (uriTask.isSuccessful()){
                                                //uri is received , upload to firebase database
                                                HashMap<String,Object>hashMap=new HashMap<>();
                                                //put post info
                                                hashMap.put("uid",uid);
                                                hashMap.put("uName",name);
                                                hashMap.put("uEmail",email);
                                                hashMap.put("uDp",dp);
                                                hashMap.put("pTitle",title2);
                                                hashMap.put("pDescr",description);
                                                hashMap.put("pImage",downloadUri);
                                                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts");
                                                ref.child(editPostId).updateChildren(hashMap)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                pd.dismiss();
                                                                Toast.makeText(AddPostActivity.this, "Update...", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        pd.dismiss();
                                                        Toast.makeText(AddPostActivity.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();

                                                    }
                                                });



                                            }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(AddPostActivity.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(AddPostActivity.this, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });
    }


    private void loadPostData(String editPostId) {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        Query query=reference.orderByChild("pId").equalTo(editPostId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    //get data
                    editTitle=""+ds.child("pTitle").getValue();
                    editDescription=""+ds.child("pDescr").getValue();
                    editImage=""+ds.child("pImage").getValue();
                    String region=""+ds.child("pRegion").getValue();
                    String location=""+ds.child("pLocation").getValue();
                    String price=""+ds.child("Price").getValue();

                    title.setText(editTitle);
                    descriptionEt.setText(editDescription);
                    price1.setText(price);

                    plocation.setSelection(1);
                    pRegion.setSelection(1);


                  //  if(!editImage.equals("noImage")){
                        try {
                            Picasso.get().load(editImage).into(imageView);
                        }catch (Exception e){

                        }
                  //  }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private  void uploadMyImage(){
    DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        UsersRef.child(UserID).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            for(DataSnapshot ds:dataSnapshot.getChildren()){
                //get data
                editImage=""+ds.child("User_Image").getValue();
                try {
                    Picasso.get().load(editImage).into(user_icon);
                }catch (Exception e){

                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

}

    private void showImagePickDialog() {
        //option (Camera or gallery)to show in dialog
        String [] option ={"Camera","Gallery"};
        //dialog
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Choose Image from");
        //set options to dialog
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    //camera clicked
                    if (!checkCameraPermission()){
                        requestCameraPermission();
                    }else {
                        pickFromCamera();
                    }


                }
                if(which==1){
                    //gallery clicked
                    if (!checkStoragePermission()){
                        requestStoragePermission();
                    }else {
                        pickFromGallery();
                    }
                }

            }
        });
        //create and show
        builder.create().show();
    }

    private void pickFromCamera() {

        ContentValues cv=new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        imge_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imge_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);

    }

    private void pickFromGallery() {
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.
                permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
    return result;
    }

    private void requestStoragePermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE
        );
    }


    private boolean checkCameraPermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.
                permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result2= ContextCompat.checkSelfPermission(this,Manifest.
                permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result && result2;
    }

    private void requestCameraPermission(){
        //request runtime storage permission
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE
        );
    }

//handle permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        pickFromCamera();
                    }else {
                        Toast.makeText(this, "Camera and Storage both permission are necessary...", Toast.LENGTH_SHORT).show();
                    }
                }else {

                }
           }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0){
                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if ( storageAccepted){
                        pickFromGallery();
                    }else {
                        Toast.makeText(this, "Storage permission is necessary...", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, @Nullable Intent data) {
            if (requestCode==IMAGE_PICK_GALLERY_CODE ){
                       imge_uri=data.getData();
                       imageView.setImageURI(imge_uri);

                InputImage image;
                try {
                    image = InputImage.fromFilePath(getApplicationContext(), data.getData());
                    // To use default options:
                    ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

// Or, to set the minimum confidence required:
 //ImageLabelerOptions options =
    // new ImageLabelerOptions.Builder()
    //     .setConfidenceThreshold(0.10f)
       //  .build();
// ImageLabeler labeler = ImageLabeling.getClient(options);

                    labeler.process(image)
                            .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                                @Override
                                public void onSuccess(List<ImageLabel> labels) {
                                    // Task completed successfully
                                    // ...
                                    for (ImageLabel label : labels) {
                                        String text = label.getText();
                                        float confidence = label.getConfidence();
                                        int index = label.getIndex();
                                        descriptionEt.append(text+"    "+confidence+"\n");
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(requestCode==IMAGE_PICK_CAMERA_CODE){
             //   imge_uri=data.getData();
                imageView.setImageURI(imge_uri);
                InputImage image;
                try {
                    image = InputImage.fromFilePath(getApplicationContext(), imge_uri);
                    // To use default options:
                    //ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);

// Or, to set the minimum confidence required:
                    ImageLabelerOptions options =
                    new ImageLabelerOptions.Builder()
                        .setConfidenceThreshold(0.1f)
                     .build();
 ImageLabeler labeler = ImageLabeling.getClient(options);

                    labeler.process(image)
                            .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                                @Override
                                public void onSuccess(List<ImageLabel> labels) {
                                    // Task completed successfully
                                    // ...
                                    for (ImageLabel label : labels) {
                                        String text = label.getText();
                                        float confidence = label.getConfidence();
                                        int index = label.getIndex();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Task failed with an exception
                                    // ...
                                }
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }




    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    }
