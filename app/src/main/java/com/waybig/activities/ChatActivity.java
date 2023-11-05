package com.waybig.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.waybig.databinding.ActivityChatBinding;

public class ChatActivity extends AppCompatActivity {
    
    private ActivityChatBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
