package com.example.mpatlisantlo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mpatlisantlo.Adapter.AdapterUser;
import com.example.mpatlisantlo.models.ModelUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;



public class UserFragment extends Fragment {

    private RecyclerView recyclerView;
    AdapterUser adapterUser;
    List<ModelUser> userList;
    private DocumentReference documentReference;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fShore;
    private String UserID;
    private ListenerRegistration listenerRegistration;
    BottomNavigationView navigationVie_bottom;


    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view=inflater.inflate(R.layout.fragment_user, container, false);
       recyclerView=view.findViewById(R.id.user_recycler);
       recyclerView.setHasFixedSize(true);
       recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        fAuth=FirebaseAuth.getInstance();
        UserID=fAuth.getCurrentUser().getUid();
        fShore = FirebaseFirestore.getInstance();



        //int user list
        userList=new ArrayList<>();
        //get all user
        getAllUser();


   return view;
    }

    private void getAllUser() {
        //old method

        //get current usr
        final FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();
        //get path of database name "Users" containing user info
       DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        //get all data from path
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUser modelUser=ds.getValue(ModelUser.class);
                    if(!modelUser.getUid().equals(fuser.getUid())){
                        userList.add(modelUser);
                    }
                    adapterUser=new AdapterUser(getActivity(),userList);
                    recyclerView.setAdapter(adapterUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        /*documentReference=fShore.collection("Users").document(UserID);
        listenerRegistration = documentReference
        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                 userList.clear();
                if (documentSnapshot != null){
                   ModelUser modelUser=documentSnapshot.get("User",ModelUser.class);
                    assert modelUser != null;
                    if(!UserID.equals(modelUser)){
                       userList.add(modelUser);
                   }else{
                        Log.d("TAG", "onEvent: error with modeluser "+e.getMessage());
                    }
                   adapterUser= new AdapterUser(getActivity(),userList);
                   recyclerView.setAdapter(adapterUser);

                 }
            }
        });*/
    }


}
