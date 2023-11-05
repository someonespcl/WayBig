package com.waybig.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.waybig.activities.MainActivity;
import com.waybig.R;
import com.waybig.databinding.ActivitySignUpBinding;
import com.waybig.viewModel.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private SignUpViewModel viewModel;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new Dialog(SignUpActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        
        // Create an instance of the SignUpViewModel to manage registration
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        binding.registerBtn.setOnClickListener(
                v -> {
                    String email = binding.emailInput.getText().toString().trim();
                    String password = binding.passwordInput.getText().toString().trim();
                    String name = binding.nameInput.getText().toString().trim();
                    String phoneNum = binding.phnNInput.getText().toString().trim();

                    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        binding.emailInput.setError(
                                email.isEmpty() ? "Cannot be empty" : "Invalid Email");
                    } else if (phoneNum.isEmpty() || phoneNum.length() < 10) {
                        binding.phnNInput.setError(
                                phoneNum.isEmpty() ? "Cannot be empty" : "Invalid phone number");
                    } else if (name.isEmpty() || name.length() < 4) {
                        binding.nameInput.setError(
                                name.isEmpty() ? "Cannot be empty" : "Too Short");
                    } else if (password.isEmpty() || password.length() < 6) {
                        binding.passwordInput.setError(
                                password.isEmpty() ? "Cannot be empty" : "Too Short");
                    } else {
                        binding.emailInput.setError(null);
                        binding.phnNInput.setError(null);
                        binding.nameInput.setError(null);
                        binding.passwordInput.setError(null);
                        dialog.show();
                        viewModel.registerWithEmailAndPassword(email, password, name, phoneNum);
                        Log.d("SignUpActivity", "Registration attempt with email: " + email);
                    }
                });

        // Observe userLiveData for successful registration
        viewModel.getUserLiveData().observe(this, user -> {
            if (user != null) {
                // Registration successful, navigate to the new ACTIVITY
                dialog.dismiss();   
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Observe errorLiveData for registration errors
        viewModel.getErrorLiveData().observe(this, errorMessage -> {
            if (errorMessage != null) {
                // Handle the error, e.g., display a toast with the error message
                dialog.dismiss();
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                // Log the error for debugging purposes
                Log.e("SignUpActivity", "Registration Error: " + errorMessage);
            }
        });
    }
}
