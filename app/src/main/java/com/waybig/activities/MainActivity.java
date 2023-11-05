package com.waybig.activities;

import android.content.Intent;
import android.os.Bundle;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.waybig.R;
import com.waybig.databinding.ActivityMainBinding;
import com.waybig.fragments.ChatsFragment;
import com.waybig.fragments.HomeFragment;
import com.waybig.fragments.ProfileFragment;
import com.waybig.fragments.UsersFragment;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {
    
    private ActivityMainBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        FragmentManager fragmentManager = getSupportFragmentManager();
        SmoothBottomBar bottomBar = findViewById(R.id.bottom_navigation_view);
        Animation slideupAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_up);
        bottomBar.startAnimation(slideupAnim);
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener(){
            @Override
            public boolean onItemSelect(int itemIndex) {
                switch(itemIndex){
                     case 0:
                        HomeFragment homeFragment = new HomeFragment();
                        fragmentManager.beginTransaction().replace(R.id.content, homeFragment).commit();
                        break;
                     case 1:
                        ProfileFragment profileFragment = new ProfileFragment();
                        fragmentManager.beginTransaction().replace(R.id.content, profileFragment).commit();
                        break;
                     case 2:
                        UsersFragment usersFragment = new UsersFragment();
                        fragmentManager.beginTransaction().replace(R.id.content, usersFragment).commit();
                        break;
                     case 3:
                        ChatsFragment chatsFragment = new ChatsFragment();
                        fragmentManager.beginTransaction().replace(R.id.content, chatsFragment).commit();
                        break;
                 }
                 return true;
            }
        });
    }
    
    @Override
    protected void onStart() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null) {
        	
        }else{
            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            finish();
        }
        super.onStart();
    }
    
}
