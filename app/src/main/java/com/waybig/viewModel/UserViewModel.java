package com.waybig.viewModel;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.waybig.models.User;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class UserViewModel extends ViewModel {
    
    private DatabaseReference databaseReference;
    private final MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private ValueEventListener valueEventListener;
    private StorageReference storageReference;
    private User user;

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }
    
    public LiveData<List<User>> getAllUsersLiveData() {
        MutableLiveData<List<User>> allUsersLiveData = new MutableLiveData<>();
        DatabaseReference allUsersReference = FirebaseDatabase.getInstance().getReference("Users");

        allUsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String name = userSnapshot.child("name").getValue(String.class);
                    String email = userSnapshot.child("email").getValue(String.class);
                    String image = userSnapshot.child("images").getValue(String.class);
                    String userId = userSnapshot.child("userId").getValue(String.class);
                    userList.add(new User(name, email, null, image, userId));
                }
                allUsersLiveData.postValue(userList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database errors
                errorLiveData.postValue(databaseError.getMessage());
            }
        });

        return allUsersLiveData;
    }

    public void fetchUserData(String userId) {
        // Initialize the DatabaseReference and add the ValueEventListener
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        valueEventListener = createValueEventListener();
        databaseReference.addValueEventListener(valueEventListener);
    }

    // Create the ValueEventListener
    private ValueEventListener createValueEventListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                    String image = dataSnapshot.child("images").getValue(String.class);
                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    
                    
                    user = new User(name, email, phoneNumber, image, userId);
                    userLiveData.postValue(user);
                } else {
                    errorLiveData.postValue("User Not Found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                errorLiveData.postValue(databaseError.getMessage());
            }
        };
    }

    public void uploadImage(Bitmap originalImage, String userId) {
        try {
            Bitmap resizedBitmap =
                    Bitmap.createScaledBitmap(
                            originalImage,
                            originalImage.getWidth(),
                            originalImage.getHeight(),
                            true);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
            byte[] compressedImageData = outputStream.toByteArray();
            storageReference =
                    FirebaseStorage.getInstance().getReference("user_images/" + userId + ".jpeg");
            UploadTask uploadTask = storageReference.putBytes(compressedImageData);

            uploadTask
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                storageReference
                                        .getDownloadUrl()
                                        .addOnSuccessListener(
                                                uri -> {
                                                    updateImageUrlInDatabase(
                                                            userId, uri.toString());
                                                });
                            })
                    .addOnFailureListener(
                            e -> {
                                errorLiveData.postValue("Image upload failed: " + e.getMessage());
                            });

        } catch(Exception err) {
        	err.printStackTrace();
        }
    }

    private void updateImageUrlInDatabase(String userId, String downloadUrl) {
        DatabaseReference userReference =
                FirebaseDatabase.getInstance().getReference("Users").child(userId);
        DatabaseReference imageUrlReference = userReference.child("images");

        imageUrlReference
                .setValue(downloadUrl)
                .addOnSuccessListener(
                        aVoid -> {
                            // Image URL updated successfully in the database
                        })
                .addOnFailureListener(
                        e -> {
                            // Handle the database update failure
                            errorLiveData.postValue(
                                    "Failed to update image URL in the database: "
                                            + e.getMessage());
                        });
    }

    public void updateUserProfile(String newName, String newPhnNum) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("phoneNumber", newPhnNum);

        databaseReference
                .updateChildren(updates)
                .addOnSuccessListener(
                        new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Profile updated successfully
                                user.setName(newName);
                                user.setPhoneNumber(newPhnNum);
                                userLiveData.postValue(user);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle error
                                errorLiveData.postValue(e.getMessage());
                            }
                        });
    }

    // Method to stop listening for updates
    public void stopListening() {
        if (valueEventListener != null) {
            databaseReference.removeEventListener(valueEventListener);
        }
    }
}
