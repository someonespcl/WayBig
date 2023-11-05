package com.waybig.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class SignUpViewModel extends ViewModel {
    private final FirebaseAuth auth;
    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public SignUpViewModel() {
        auth = FirebaseAuth.getInstance();
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void registerWithEmailAndPassword(
            String email, String password, String name, String phoneNumber) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = auth.getCurrentUser();
                                userLiveData.postValue(user);
                                storeDetails(user.getUid(), name, email, password, phoneNumber);

                            } else {
                                String errorMessage = task.getException().getMessage();
                                errorLiveData.postValue(errorMessage);
                            }
                        });
    }

    public void storeDetails(
            String userId, String name, String email, String password, String phoneNumber) {
        DatabaseReference databaseRefer = FirebaseDatabase.getInstance().getReference("Users");
        HashMap<String, Object> data = new HashMap<>();
        data.put("userId", userId);
        data.put("name", name);
        data.put("email", email);
        data.put("password", password);
        data.put("phoneNumber", phoneNumber);
        databaseRefer.child(userId).setValue(data);
    }
}
