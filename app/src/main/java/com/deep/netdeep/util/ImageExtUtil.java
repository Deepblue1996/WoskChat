package com.deep.netdeep.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

public class ImageExtUtil {
    public static void show(Context context, File file, ImageView imageView) {
        Glide.with(context)
                .load(file)
                .apply((new RequestOptions())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .skipMemoryCache(false)
                        .dontAnimate()
                        .centerCrop()
                        .placeholder(0))
                .into(imageView);
    }
}
