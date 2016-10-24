package com.example.palexis3.nytimes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.palexis3.nytimes.adapters.ArticlesRecyclerAdapter;
import com.example.palexis3.nytimes.views.DynamicHeightImageView;
import com.squareup.picasso.Picasso;

public class StaggeredGridTarget implements ArticlesRecyclerAdapter.Target {

    private DynamicHeightImageView imageView;
    private Context context;

    public StaggeredGridTarget(Context context, DynamicHeightImageView imageView) {
        this.context = context;
        this.imageView = imageView;

    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

        // Calculate the image ratio of the loaded bitmap
        float ratio = (float) bitmap.getHeight() / (float) bitmap.getWidth();

        // Set the ratio for the image
        imageView.setHeightRatio(ratio);

        //load the image into the view
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Log.d("Bitmap DEBUG", errorDrawable.toString());
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        Drawable d =  ContextCompat.getDrawable(context, R.drawable.nytimes_logo);
        imageView.setImageDrawable(d);
    }
}
