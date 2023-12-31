package com.waybig.preferences;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageCacheManager {
    private Context context;

    public ImageCacheManager(Context context) {
        this.context = context;
    }

    public void saveProfileImageForCache(Bitmap image, String userId) {
        // Get the private internal storage directory
        File directory = new File(context.getFilesDir(), "profile_images");

        // Create the directory if it doesn't exist
        if (!directory.exists() && !directory.mkdirs()) {
            // Handle the error if directory creation failed
            return;
        }

        // Create a file in the internal storage
        File imageFile = new File(directory, userId + ".jpeg");

        // Save the image file using a FileOutputStream
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            image.compress(Bitmap.CompressFormat.JPEG, 75, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getCacheProfileImage(String userId) {
        // Get the private internal storage directory
        File directory = new File(context.getFilesDir(), "profile_images");

        // Check if the image file exists
        File imageFile = new File(directory, userId + ".jpeg");
        if (imageFile.exists()) {
            // Return the Bitmap from the file path
            return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        }

        // Return null if the image doesn't exist
        return null;
    }
}

/*package com.waybig.preferences;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Build;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.crypto.Cipher;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class ImageCacheManager {
    private Context context;

    public ImageCacheManager(Context context) {
        this.context = context;
    }

    public void saveProfileImageForCache(Bitmap image, String userId) {
        File directory;

        // Use the appropriate method to get the storage directory based on Android version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            directory = new File(context.getExternalFilesDir(null), ".profile_images");
        } else {
            directory = new File(context.getExternalFilesDir(null), "waybig/profile_images");
        }

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File imageFile = new File(directory, userId + ".jpeg");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            image.compress(Bitmap.CompressFormat.JPEG, 75, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getCacheProfileImage(String userId) {
        File directory;

        // Use the appropriate method to get the storage directory based on Android version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            directory = new File(context.getExternalFilesDir(null), ".profile_images");
        } else {
            directory = new File(context.getExternalFilesDir(null), "waybig/profile_images");
        }

        if (directory.exists()) {
            File imageFile = new File(directory, userId + ".jpeg");
            if (imageFile.exists()) {
                return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            }
        }
        return null; // Return null if the image doesn't exist
    }
}*/