package com.example.hasanzian.newsapp.Adaptor;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hasanzian.newsapp.R;
import com.example.hasanzian.newsapp.Utils.Model;
import com.example.hasanzian.newsapp.Utils.QueryUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adaptor for recycler view
 */

public class RecyclerAdaptor extends RecyclerView.Adapter<RecyclerAdaptor.myViewHolder> {
    //list which is used to populate list item
    private List<Model> mList;
    //context
    private Context mContext;

    /**
     * @param mList is given when creating new adaptor
     */
    public RecyclerAdaptor(List<Model> mList) {
        this.mList = mList;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        mContext = parent.getContext();
        return new myViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        // setting custom font -- Product Sans
        Typeface headingFont, regularFont;
        regularFont = QueryUtils.regularFont(mContext);
        headingFont = QueryUtils.headingFont(mContext);

        holder.mDate.setTypeface(regularFont);
        holder.mHeading.setTypeface(headingFont);
        holder.mSection.setTypeface(regularFont);
        holder.mAuthorName.setTypeface(regularFont);

        holder.mHeading.setText(mList.get(position).getHeading());
        // Format string to format
        String formattedString = QueryUtils.formatDate(mList.get(position).getDate());
        holder.mDate.setText(formattedString);
        holder.mSection.setText(mList.get(position).getSection());
        holder.mAuthorName.setText("by " + mList.get(position).getAuthor());

        Glide.with(mContext).setDefaultRequestOptions(QueryUtils.requestOptions()).load(mList.get(position).getImageUrl()).into(holder.mImageView);
        Glide.with(mContext).setDefaultRequestOptions(QueryUtils.requestOptions()).load(mList.get(position).getAuthorImage()).into(holder.mAuthorImage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * myViewHolder class initializing view
     */
    class myViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.heading)
        TextView mHeading;
        @BindView(R.id.author)
        TextView mAuthorName;
        @BindView(R.id.section)
        TextView mSection;
        @BindView(R.id.date)
        TextView mDate;
        @BindView(R.id.image)
        ImageView mImageView;
        @BindView(R.id.authorImage)
        ImageView mAuthorImage;

        myViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
