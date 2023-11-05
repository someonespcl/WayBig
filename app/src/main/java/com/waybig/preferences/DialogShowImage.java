package com.waybig.preferences;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.ViewGroup;
import android.view.Window;
import android.graphics.drawable.ColorDrawable;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.waybig.R;
import com.waybig.models.User;
import com.waybig.viewModel.UserViewModel;

public class DialogShowImage extends Dialog {

    private Context context;
    private UserViewModel viewModel;
    private ImageView showImage;
    private ImageCacheManager imageManager;
    private User myUser;

    public DialogShowImage(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.layout_dialog_show_image);

        showImage = findViewById(R.id.showImageDialog);
        imageManager = new ImageCacheManager((AppCompatActivity) context);
        

        // Initialize ViewModel
        viewModel = new ViewModelProvider((AppCompatActivity) context).get(UserViewModel.class);

        viewModel
                .getUserLiveData()
                .observe(
                        (LifecycleOwner) context,
                        user -> {
                            if (user != null) {
                                // Load the image from the user object. Make sure 'user.getImage()'
                                // contains the image resource.
                                Bitmap cacheImage =
                                        imageManager.getCacheProfileImage(user.getUserId());
                                if (cacheImage != null) {
                                    showImage.setImageBitmap(cacheImage);
                                } else {
                                    Glide.with(context)
                                            .load(myUser.getImage())
                                            .transition(
                                                    DrawableTransitionOptions.withCrossFade(
                                                            new DrawableCrossFadeFactory.Builder()
                                                                    .setCrossFadeEnabled(true)
                                                                    .build()))
                                            .into(showImage);
                                }
                            }
                        });

        // Set the dialog size based on the screen dimensions (adjust as needed).
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
    
}
