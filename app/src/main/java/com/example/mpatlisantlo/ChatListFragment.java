package com.example.mpatlisantlo;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {

    public ChatListFragment() {
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
                    startActivity(new Intent(getContext(), HomeFragment.class));
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(getContext(), "Double click back to exit",Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                        System.exit(0);
                    }
                }, 2000);
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                FirebaseUser UserID = fAuth.getCurrentUser();

            }
        };



        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false);


    }

}
