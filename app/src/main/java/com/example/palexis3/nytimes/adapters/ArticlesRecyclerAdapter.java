package com.example.palexis3.nytimes.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.palexis3.nytimes.R;
import com.example.palexis3.nytimes.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticlesRecyclerAdapter extends RecyclerView.Adapter<ArticlesRecyclerAdapter.ViewHolder>{

    private Context context;
    private List<Article> list;


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

    //involves populating data into the item through the holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Article article = list.get(position);

        //set item views based implemented views and article model
        TextView textView = holder.tvTitle;
        textView.setText(article.getHeadLine());

        ImageView imageView = holder.imageView;
        //clear out recycled image from convertView from last time
        imageView.setImageResource(0);

        //populate the thumbnail image
        //remote download the image in the background
        String thumbNail = article.getThumbNail();
        if(!TextUtils.isEmpty(thumbNail)) {
            Picasso.with(context).load(thumbNail).into(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //passing necessary info into constructor
    public ArticlesRecyclerAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.list = articles;
    }

    //provide a direct reference to each views within a data item
    //used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvTitle;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.ivImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }
}
