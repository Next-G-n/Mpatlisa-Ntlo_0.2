package com.example.mpatlisantlo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
private EditText email_input,password_input;
private Button submit;
private FirebaseAuth nAuth;
private LinearLayout loading;
private TextView create_account;
private TextView forgot_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email_input=(EditText)findViewById(R.id.Login_email_input);
        password_input=(EditText)findViewById(R.id.Login_Password_input);
        submit=(Button) findViewById(R.id.Login_button);
        create_account=findViewById(R.id.Create_Account);
        forgot_Password=findViewById(R.id.forgot_password);
        nAuth=FirebaseAuth.getInstance();
        loading= (LinearLayout)findViewById(R.id.back);
        loading.setVisibility(View.GONE);

        if(nAuth.getCurrentUser() !=null) {
            startActivity(new Intent(getApplicationContext(), MainPageActivity.class));
            finish();
        }

        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });

        forgot_Password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  startActivity(new Intent(LoginActivity.this,forgotPassword.class));
               // finish();
                showRecoveryPasswordDialog();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = email_input.getText().toString();
                String password = password_input.getText().toString();


                if (TextUtils.isEmpty(password)) {
                    password_input.setError("Please write your Email or phone number...");

                } else if (TextUtils.isEmpty(phoneNumber)) {
                    email_input.setError("Please write your password ......");

                }else{
                    loading.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                    nAuth.signInWithEmailAndPassword(phoneNumber,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                final FirebaseUser user=nAuth.getCurrentUser();

                                /*if(!user.isEmailVerified()){
                                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loading.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                            FirebaseAuth.getInstance().signOut();
                                            Toast.makeText(LoginActivity.this, "you still have not verified you email!...Verify first, before login.", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(LoginActivity.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            loading.setVisibility(View.GONE);
                                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            Log.d("tag", "onFailure: Email not sent");
                                        }
                                    });
                                }else{*/
                                    loading.setVisibility(View.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(LoginActivity.this, "Login..", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),MainPageActivity.class));
                               // }


                            }else{
                                loading.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(LoginActivity.this, "Error..!"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }
            }
        });
    }

    private void showRecoveryPasswordDialog() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Reset Password");
        builder.setMessage("");
        builder.setCancelable(false);
        LinearLayout linearLayout = new LinearLayout(this);
        final EditText emailInp = new EditText(this);
        emailInp.setHint("Email");
        emailInp.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailInp.setMinEms(16);

        linearLayout.addView(emailInp);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);

        //reset button
        builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            //input email
              String email= emailInp.getText().toString().trim();
              beginRecovery(email);
            }
        });
        //cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
      //show dialog
        builder.create().show();
    }

    private void beginRecovery(String email) {
        nAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this ,"Email Sent",Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(LoginActivity.this ,"Failed",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this ,""+ e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
