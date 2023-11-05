package com.waybig.activities;

import android.os.Bundle;
import android.util.Patterns;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.Toast;
import com.waybig.databinding.ActivityPasswordResetBinding;

public class PasswordResetActivity extends AppCompatActivity {
    
    private ActivityPasswordResetBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordResetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /*String resetEmail = binding.passResetEmail.getText().toString().trim();
        if (resetEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
            binding.passResetEmail.setError(resetEmail.isEmpty() ? "Cannot be empty" : "Invalid Email");
        } else {
            FirebaseAuth.getInstance()
                    .sendPasswordResetEmail(resetEmail)
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    // Password reset email sent successfully.
                                    Toast.makeText(
                                                    this,
                                                    "Password reset email sent.",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                } else {
                                    // Handle errors if the email is not found or other
                                    // issues.
                                    Toast.makeText(
                                                    this,
                                                    "Password reset failed. Check your email address.",
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
        }

        binding.resetButton.setOnClickListener(
                v -> {
                    
                    String newPass = binding.passResetInput.getText().toString().trim();

                    
                    String resetCode =
                            getIntent()
                                    .getData()
                                    .getQueryParameter(
                                            "oobCode"); // Retrieve the code from the link

                    FirebaseAuth.getInstance()
                            .confirmPasswordReset(resetCode, newPass)
                            .addOnCompleteListener(
                                    task -> {
                                        if (task.isSuccessful()) {
                                            // Password reset successful.
                                            Toast.makeText(
                                                            this,
                                                            "Password reset successful.",
                                                            Toast.LENGTH_SHORT)
                                                    .show();
                                        } else {
                                            // Handle errors if the code is invalid or other issues.
                                            Toast.makeText(
                                                            this,
                                                            "Password reset failed. Please try again.",
                                                            Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    });
                });*/
    }
}
