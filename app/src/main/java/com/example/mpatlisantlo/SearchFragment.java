package com.example.mpatlisantlo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.mpatlisantlo.Adapter.AdapterUser;
import com.example.mpatlisantlo.models.ModelUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private RecyclerView recyclerView;
    AdapterUser adapterUser;
    List<ModelUser> userList;
    private DocumentReference documentReference;
    private FirebaseAuth fAuth;
    private FirebaseFirestore fShore;
    private ListenerRegistration listenerRegistration;
    BottomNavigationView navigationVie_bottom;
    private EditText search_bar;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView=view.findViewById(R.id.search_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        search_bar=view.findViewById(R.id.search_bar);

        //int user list
        userList=new ArrayList<>();
        adapterUser=new AdapterUser(getContext(),userList);
        recyclerView.setAdapter(adapterUser);

       readUsers();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString().toLowerCase());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }
    private void searchUser(final String s){
        Query query=FirebaseDatabase.getInstance().getReference("Users").orderByChild("User_Name")
              .startAt(s)
              .endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ModelUser modelUser=ds.getValue(ModelUser.class);
                       Log.d("TAG", "onDataChange: no error failed to display");

                }

                adapterUser.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "onCancelled: Error with search "+databaseError.getMessage());

            }
        });
    }


    private void readUsers(){


        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(search_bar.getText().toString().equals("")){
                   userList.clear();
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        ModelUser modelUser=ds.getValue(ModelUser.class);

                            userList.add(modelUser);

                       }


                   adapterUser.notifyDataSetChanged();
                }else{
                    Log.d("TAG", "onDataChange: error Edit text ");
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "onCancelled: Error with search "+databaseError.getMessage());

            }
        });
    }

}
