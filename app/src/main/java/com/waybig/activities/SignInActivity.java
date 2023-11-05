package com.waybig.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.waybig.R;
import com.waybig.databinding.ActivitySignInBinding;
import com.waybig.viewModel.SignInViewModel;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private ActivitySignInBinding binding;
    private SignInViewModel viewModel;
    private Dialog dialog;
    private SharedPreferences sharedPreferences;
    private static final String LOCAL_CREDENTIALS = "localCredentials";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER_ME = "rememberMe";
    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new Dialog(SignInActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        
        sharedPreferences = getSharedPreferences("localCredentials", Context.MODE_PRIVATE);
        boolean rememberMe = sharedPreferences.getBoolean(KEY_REMEMBER_ME, false);
        binding.rememberMe.setChecked(rememberMe);
        
        if(rememberMe) {
        	String savedEmail = sharedPreferences.getString(KEY_EMAIL, "");
            String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
            binding.eInput.setText(savedEmail);
            binding.passInput.setText(savedPassword);
        }

        // Initialize GoogleSignInClient and FirebaseAuth
        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        viewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        binding.loginButton.setOnClickListener(
                v -> {
                    
                    String email = binding.eInput.getText().toString().trim();
                    String password = binding.passInput.getText().toString().trim();
                
                    if (binding.rememberMe.isChecked()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(KEY_EMAIL, email);
                        editor.putString(KEY_PASSWORD, password);
                        editor.putBoolean(KEY_REMEMBER_ME, true);
                        editor.apply();
                    } else {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(KEY_EMAIL);
                        editor.remove(KEY_PASSWORD);
                        editor.putBoolean(KEY_REMEMBER_ME, false);
                        editor.apply();
                    }

                    if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        binding.eInput.setError(
                                email.isEmpty() ? "Cannot be empty" : "Invalid Email");
                    } else if (password.isEmpty() || password.length() < 6) {
                        binding.passInput.setError(
                                password.isEmpty() ? "Cannot be empty" : "Too Short");
                    } else {
                        binding.eInput.setError(null);
                        binding.passInput.setError(null);
                        dialog.show();
                        viewModel.signInWithEmailAndPassword(email, password);
                        Log.d("SignIpActivity", "SignIn attempt with email: " + email);
                    }
                });

        binding.signInWithGoogle.setOnClickListener(
                v -> {
                    resultIntent.launch(new Intent(googleSignInClient.getSignInIntent()));
                });

        binding.forgetPass.setOnClickListener(
                v -> {
                    startActivity(new Intent(SignInActivity.this, PasswordResetActivity.class));
                    finish();
                });

        // Observe userLiveData for successful signin
        viewModel
                .getUserLiveData()
                .observe(
                        this,
                        user -> {
                            if (user != null) {
                                // signin successful, navigate to the new ACTIVITY
                                dialog.dismiss();
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

        // Observe errorLiveData for signin errors
        viewModel
                .getErrorLiveData()
                .observe(
                        this,
                        errorMessage -> {
                            if (errorMessage != null) {
                                // Handle the error, e.g., display a toast with the error message
                                dialog.dismiss();
                                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                                // Log the error for debugging purposes
                                Log.e("SignInActivity", "SignIn Error: " + errorMessage);
                            }
                        });
    }

    ActivityResultLauncher<Intent> resultIntent =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Intent resultData = result.getData();
                                Task<GoogleSignInAccount> task =
                                        GoogleSignIn.getSignedInAccountFromIntent(resultData);
                                try {
                                    GoogleSignInAccount account =
                                            task.getResult(ApiException.class);
                                    assert account != null;
                                    authWithGoogle(account.getIdToken());
                                } catch (ApiException e) {
                                    // Sign-in failed, display an error message
                                    Log.e("SignInActivity", "GoogleSignInError : " + e.getMessage());
                                }
                            }
                        }
                    });

    private void authWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth
                .signInWithCredential(credential)
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    viewModel.storeDetails(user.getUid(), user.getDisplayName(), user.getEmail(), null, null);
                                	startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                                    finish();
                                }else {
                                    Toast.makeText(SignInActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
    }
    
    private void passwordRecover(String email) {
        dialog.show();
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignInActivity.this, "Forget password email link sent Successfully.", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(
                                                    SignInActivity.this,
                                                    task.getException().getMessage(),
                                                    Toast.LENGTH_LONG)
                                            .show();
                                    dialog.dismiss();
                                }
                            }
                        });
    }
    
    public void goToRegister(View v) {
        startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
    }
}
