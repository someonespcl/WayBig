package com.waybig.activities;
import android.app.Application;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Check if the user is not null
        // Enable Firebase Realtime Database persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Users");
        myRef.keepSynced(true);
    }
}
