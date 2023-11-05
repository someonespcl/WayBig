package com.waybig.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import com.waybig.R;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.common.images.ImageManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.waybig.databinding.ActivityProfileSettingsBinding;
import com.waybig.models.User;
import com.waybig.preferences.CircularImageView;
import com.waybig.preferences.ImageCacheManager;
import com.waybig.viewModel.UserViewModel;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ProfileSettingsActivity extends AppCompatActivity {
    
    private ActivityProfileSettingsBinding binding;
    private FirebaseUser fUser;
    private FirebaseAuth auth;
    private User myUser;
    private CircularImageView editProfileImage;
    private UserViewModel viewModel;
    private static int REQUEST_CODE = 111;
    ImageCacheManager imageManager;
    private Dialog dialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        dialog = new Dialog(ProfileSettingsActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        
        auth = FirebaseAuth.getInstance();
        fUser = auth.getCurrentUser();
        imageManager = new ImageCacheManager(ProfileSettingsActivity.this);
        
        viewModel = new ViewModelProvider(ProfileSettingsActivity.this).get(UserViewModel.class);
        viewModel.fetchUserData(fUser.getUid());
        viewModel
                .getUserLiveData()
                .observe(
                        ProfileSettingsActivity.this,
                        user -> {
                            if (user != null) {
                                Bitmap cacheImage =
                                        imageManager.getCacheProfileImage(fUser.getUid());
                                if (cacheImage != null) {
                                    dialog.dismiss();
                                    binding.editImage.setImageBitmap(cacheImage);
                                } else {
                                    dialog.dismiss();
                                    try {
                                        dialog.show();
                                        Glide.with(ProfileSettingsActivity.this)
                                                .load(myUser.getImage())
                                                .into(binding.editImage);
                                    } catch (Exception err) {
                                        dialog.dismiss();
                                        Toast.makeText(
                                                        ProfileSettingsActivity.this,
                                                        err.getMessage().toString(),
                                                        Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            } else {
                                Toast.makeText(
                                                ProfileSettingsActivity.this,
                                                "User Data Not Found.",
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

        viewModel
                .getErrorLiveData()
                .observe(
                        this,
                        errorMessage -> {
                            if (errorMessage != null) {
                                Toast.makeText(ProfileSettingsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        });

        binding.editImageBtn.setOnClickListener(
                v -> {
                    openGallery();
                });
        
    }
    
    private void openGallery() {
        if (Build.VERSION.PREVIEW_SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent =
                    new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            openGalleryLauncher.launch(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            openGalleryLauncher.launch(intent);
        }
    }
    
    ActivityResultLauncher<Intent> openGalleryLauncher =
    registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri imageUri = result.getData().getData();
                    UCrop.Options options = new UCrop.Options();
                    options.setCompressionQuality(100); // Max quality (no compression)
                    options.setToolbarTitle("Crop Image");
                    options.withAspectRatio(1, 1);
                    options.setToolbarWidgetColor(Color.BLACK);
                    options.setToolbarColor(Color.BLACK);
                    options.setStatusBarColor(Color.TRANSPARENT);
                    options.setActiveControlsWidgetColor(Color.parseColor("#FFC100"));

                    // Set the destination URI for the cropped image
                    Uri destinationUri = Uri.fromFile(new File(getCacheDir(), "cropped_image.jpg"));

                    // Start uCrop
                    UCrop uCrop = UCrop.of(imageUri, destinationUri).withOptions(options);

                    uCrop.start(ProfileSettingsActivity.this);
                } else {
                    dialog.dismiss();
                    Toast.makeText(
                        ProfileSettingsActivity.this,
                        "Please select an image",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            Uri croppedImageUri = UCrop.getOutput(data);
            Bitmap imageBitmap = uriToBitmap(croppedImageUri);
            
            if (imageBitmap != null) {
                dialog.show();
                viewModel.uploadImage(imageBitmap, fUser.getUid());
                imageManager.saveProfileImageForCache(imageBitmap, fUser.getUid());
                Log.d("FILE SAVED TO LOCAL", "DONE");
            } else {
                dialog.dismiss();
                Toast.makeText(
                                ProfileSettingsActivity.this,
                                "Failed to convert Uri to Bitmap",
                                Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private Bitmap uriToBitmap(Uri uri) {
        try {
            InputStream imageStream = getApplicationContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            if (imageStream != null) {
                imageStream.close();
            }
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: Implement this method
        viewModel.getUserLiveData().removeObservers(ProfileSettingsActivity.this);
        viewModel.getErrorLiveData().removeObservers(ProfileSettingsActivity.this);
    }
    
}