package com.example.mpatlisantlo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.example.mpatlisantlo.R.id.Register_name_input;

public class RegisterActivity extends AppCompatActivity {
private Button Register_btn;
private EditText input_userName,input_cellNumber,input_email,input_password,input_Confirm;
private FirebaseAuth nAuth;
private LinearLayout loading;
private FirebaseFirestore fstore;
private String UserId;
private FirebaseUser UserID;
    private static final String TAG = RegisterActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Register_btn=(Button)findViewById(R.id.Register_button);
        input_cellNumber=(EditText)findViewById(R.id.Register_phone_number_input);
        input_userName=(EditText)findViewById(Register_name_input);
        input_password=(EditText)findViewById(R.id.Register_Password_input);
        input_Confirm=(EditText)findViewById(R.id.Register_Confirm_Password_input);
        input_email=(EditText)findViewById(R.id.Register_email_input);
        nAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        loading= (LinearLayout)findViewById(R.id.back);
        loading.setVisibility(View.GONE);



       /* if(nAuth.getCurrentUser() !=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }*/



        Register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreaterAcount();
            }
        });
    }

    private void CreaterAcount() {
        final String name=input_userName.getText().toString();
        final String phoneNumber=input_cellNumber.getText().toString();
        final String password=input_password.getText().toString();
        String confirmPassword=input_Confirm.getText().toString();
        final String email=input_email.getText().toString();

        if(TextUtils.isEmpty(name)){
            input_userName.setError("Please write your name ......");

        }
        else if(TextUtils.isEmpty(phoneNumber)){
            input_cellNumber.setError("Please write your phone number...");

        }else if(TextUtils.isEmpty(email)){
            input_cellNumber.setError("Please write your Email...");

        }
       else if(TextUtils.isEmpty(password)){
            input_password.setError("Please write your password ......");

        }
       else if(TextUtils.isEmpty(confirmPassword)){
            input_Confirm.setError("Please write your password again ...");

        }else if(!(confirmPassword.matches(password))){
           input_Confirm.setError("Password does not match");
        }
       else{
            loading.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


            nAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        //sent verification link
                        FirebaseUser user=nAuth.getCurrentUser();
                      /*  user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RegisterActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: Email not sent");

                            }
                        });*/
                        loading.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(RegisterActivity.this, "User Created..", Toast.LENGTH_SHORT).show();

                        //Storing details in the database
                        UserID=nAuth.getCurrentUser();
                        String email=user.getEmail();
                        String uid=UserID.getUid();
                        /*
                        DocumentReference documentReference=fstore.collection("Users").document(UserID);
                        Map<String,Object>userdb=new HashMap<>();
                        userdb.put("User Name",name);
                        userdb.put("Email",email);
                        userdb.put("Phone Number",phoneNumber);
                        userdb.put("Password",password);
                        userdb.put("User Image"," ");
                        userdb.put("Location"," ");

                        documentReference.set(userdb)*/

                        Map<String,Object>userdb=new HashMap<>();
                        userdb.put("User_Name",name);
                        userdb.put("uid",uid);
                        userdb.put("Email",email);
                        userdb.put("Phone_Number",phoneNumber);
                        userdb.put("Password",password);
                        userdb.put("User_Image","");
                        userdb.put("onlineStatus","online");
                        userdb.put("typingTo","noOne");
                        userdb.put("Location","");
                        userdb.put("User_Preferred_area", "");
                        userdb.put("User_Preferred_Location","");
                        userdb.put("User_Preferred_Price","");
                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                        DatabaseReference reference=database.getReference("Users");
                        reference.child(uid).setValue(userdb)

                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: user Profile is create for "+ UserID);
                                startActivity(new Intent(RegisterActivity.this,CustomizeActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: User Profile creation failed "+e.getMessage());
                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                finish();
                            }
                        });

                    }else{
                        loading.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(RegisterActivity.this, "Error..!"+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            });


        }
    }

    private void ValidatePhoneNumber(final String name, final String phoneNumber, final String password) {
        nAuth=FirebaseAuth.getInstance();

    }

}
