package com.example.mpatlisantlo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class CustomizeActivity extends AppCompatActivity {

    private EditText prefer_price,prefer_Location,prefer_area;
    private Button submit_btn;
    FirebaseAuth firebaseAuth;
    DatabaseReference UserDbRef;
    FirebaseDatabase firebaseDatabase;
    String UserID;
    String email,uid;
    ProgressDialog pd;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);
        prefer_area=findViewById(R.id.Prefer_Area);
        prefer_price=findViewById(R.id.Prefer_Price);
        prefer_Location=findViewById(R.id.Prefer_Location);
        submit_btn=findViewById(R.id.Submit_Preference);

        firebaseAuth=FirebaseAuth.getInstance();
        UserID=firebaseAuth.getCurrentUser().getUid();
        firebaseDatabase=FirebaseDatabase.getInstance();

        checkUserStatus();

        pd=new ProgressDialog(this);


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setMessage("Submitting Preferences");
                pd.show();
                final String area=prefer_area.getText().toString();
                String location=prefer_Location.getText().toString();
                String price=prefer_price.getText().toString();
                if(TextUtils.isEmpty(area)){
                    pd.dismiss();
                    prefer_area.setError("Please write your preferred Area ......");


                }
                else if(TextUtils.isEmpty(location)){
                    pd.dismiss();
                    prefer_Location.setError("Please write your preferred Location...");

                }else if(TextUtils.isEmpty(price)) {
                    pd.dismiss();
                    prefer_price.setError("Please write your preferred Price...");
                }

                //Query myRef= FirebaseDatabase.getInstance().getReference("Users");

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("User_Preferred_area", area);
                hashMap.put("User_Preferred_Location",location);
                hashMap.put("User_Preferred_Price",price);
               // UserDbRef=firebaseDatabase.getReference("Users");
                databaseReference=firebaseDatabase.getReference("Users");
                databaseReference.child(UserID).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pd.dismiss();
                        Toast.makeText(CustomizeActivity.this, "Preference Submitted...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(CustomizeActivity.this,MainPageActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(CustomizeActivity.this, "Error Submitting..."+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
}
