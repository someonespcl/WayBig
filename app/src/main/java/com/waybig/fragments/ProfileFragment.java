package com.waybig.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.waybig.R;
import com.waybig.activities.ProfileSettingsActivity;
import com.waybig.preferences.CircularImageView;
import com.waybig.preferences.DialogShowImage;
import com.waybig.preferences.ImageCacheManager;
import com.waybig.viewModel.UserViewModel;

public class ProfileFragment extends Fragment {

    private DatabaseReference dataRefer;
    private FirebaseAuth auth;
    private FirebaseUser fUser;
    private UserViewModel viewModel;

    private TextView emailTv;
    private TextView nameTv;
    private TextView phone;
    private ImageView settings;
    private CircularImageView profilePic;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        dataRefer = FirebaseDatabase.getInstance().getReference("Users");
        auth = FirebaseAuth.getInstance();
        fUser = auth.getCurrentUser();

        emailTv = view.findViewById(R.id.userProfileEmail);
        nameTv = view.findViewById(R.id.userProfileName);
        //phone = view.findViewById(R.id.userProfilePhoneN);
        profilePic = view.findViewById(R.id.profilePic);
        //settings = view.findViewById(R.id.settings);

        profilePic.setOnClickListener(
                v -> {
                    DialogShowImage dialog = new DialogShowImage(requireActivity());
                    dialog.show();
                });

        /*settings.setOnClickListener(
                v -> {
                    startActivity(new Intent(requireActivity(), ProfileSettingsActivity.class));
                });*/

        viewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        
        ImageCacheManager imageCacheManager = new ImageCacheManager(requireContext());
        
        viewModel.fetchUserData(fUser.getUid());
        viewModel
                .getUserLiveData()
                .observe(
                        getViewLifecycleOwner(),
                        user -> {
                            if (user != null) {
                                emailTv.setText(user.getEmail());
                                nameTv.setText(user.getName());
                                phone.setText(user.getPhoneNumber());
                    
                                Bitmap imageCache = imageCacheManager.getCacheProfileImage(fUser.getUid());
                    
                                if (imageCache != null) {
                                    profilePic.setImageBitmap(imageCache);
                                    Log.d("SHOWING LOCAL IMAGE", "DONE");
                                } else {
                                    try {
                                        Glide.with(requireActivity())
                                                .load(user.getImage())
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .priority(Priority.HIGH)
                                                .into(profilePic);
                                        Log.d("SHOWING ONLINE IMAGE", "DONE");
                                    } catch (Exception err) {
                                        Toast.makeText(
                                                        requireActivity(),
                                                        err.getMessage(),
                                                        Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            } else {
                                // Handle the case where the user data was not found.
                                Toast.makeText(
                                                requireActivity(),
                                                fUser.getEmail() + "Data Not Found.",
                                                Toast.LENGTH_LONG)
                                        .show();
                            }
                        });

        viewModel
                .getErrorLiveData()
                .observe(
                        getViewLifecycleOwner(),
                        errorMessage -> {
                            Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_LONG)
                                    .show();
                        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.getUserLiveData().removeObservers(getViewLifecycleOwner());
        viewModel.getErrorLiveData().removeObservers(getViewLifecycleOwner());
        viewModel.stopListening();
    }
}
