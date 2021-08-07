package com.example.mediguide;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser mFirebaseUser=mFirebaseAuth.getCurrentUser();
        System.out.println(mFirebaseAuth.getCurrentUser());

        if(mFirebaseUser != null){
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
            finish();
        }
        else{
            new CountDownTimer(5000,1000){
                @Override
                public void onTick(long millisUntilFinished){}

                @Override
                public void onFinish(){
                    openCarouselActivity();
                    finish();
                }
            }.start();
        }
    }

    public void openCarouselActivity(){
        Intent intent = new Intent(this, CarouselActivity.class);
        startActivity(intent);
    }

}