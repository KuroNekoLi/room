package com.example.homework2rewrite;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MediaUtil {
    public static long getImageId(Activity activity) {
        long id =-1;

        Intent intent = activity.getIntent();
        if (intent != null) {
            if (intent.hasExtra("image_id")) {
                id = intent.getLongExtra("image_id" ,-1);
            }
        }
        return id;
    }

    public static void loadImage(Activity activity, String imagePath, ImageView imageView) {
        if (imagePath == null) {
            // 如果 imagePath 為 null，則使用 drawable 中的 image_placeholder.png
            Glide.with(activity)
                    .load( R.drawable.image_placeholder)
                    .into(imageView);
        } else {
            Glide.with(activity)
                    .load(imagePath)
                    .centerCrop()
                    .into(imageView);

        }
    }
}