package com.example.palexis3.nytimes.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.palexis3.nytimes.R;
import com.example.palexis3.nytimes.models.Article;
import com.example.palexis3.nytimes.views.DynamicHeightImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticlesRecyclerAdapter extends RecyclerView.Adapter<ArticlesRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Article> list;
    private DynamicHeightImageView imageView;


    //passing necessary info into constructor
    public ArticlesRecyclerAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.list = articles;
    }

    //in charge of inflating a layout from XML and returning the holder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        //inflate the custom layout
        View view = inflater.inflate(R.layout.item_article_result, parent, false);

        //return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    //provide a direct reference to each views within a data item
    //used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        DynamicHeightImageView imageView;
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (DynamicHeightImageView) itemView.findViewById(R.id.ivImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }

    }

    //Target interface
    public interface Target {

        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from);
        public void onBitmapFailed(Drawable errorDrawable);
        public void onPrepareLoad(Drawable placeHolderDrawable);

    }


    //implement target interface
   Target target = new Target() {
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
    };


    //involves populating data into the item through the holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = list.get(position);

        //set item views based implemented views and article model
        TextView textView = holder.tvTitle;
        textView.setText(article.getHeadLine());

        imageView = holder.imageView;

        //populate the thumbnail image
        //remote download the image in the background
        String thumbNail = article.getThumbNail();
        if(!TextUtils.isEmpty(thumbNail)) {
            imageView.setTag(target);
            Picasso.with(context)
                    .load(thumbNail)
                    .placeholder(R.drawable.nytimes_logo)
                    .into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
