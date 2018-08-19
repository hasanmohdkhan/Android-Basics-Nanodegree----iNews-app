package com.example.hasanzian.newsapp.Adaptor;

import android.content.Context;
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

import java.util.List;

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


    /**
     * myViewHolder class initializing view
     */
    class myViewHolder extends RecyclerView.ViewHolder {
        TextView mHeading, mSubHeading , mAuthorName,mSection,mDate;//,mAuthorName;
        ImageView mImageView,mAuthorImage;


        myViewHolder(View itemView) {
            super(itemView);
            mHeading = itemView.findViewById(R.id.heading);
            mSubHeading = itemView.findViewById(R.id.sub_heading_text); //author image
            mImageView = itemView.findViewById(R.id.image);
            mAuthorName = itemView.findViewById(R.id.author);
            mSection = itemView.findViewById(R.id.section);
            mDate = itemView.findViewById(R.id.date);
            mAuthorImage = itemView.findViewById(R.id.authorImage);

        }
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
        holder.mHeading.setText(mList.get(position).getHeading());
        holder.mSubHeading.setText(mList.get(position).getSubheading());
        holder.mDate.setText(mList.get(position).getDate());
        holder.mSection.setText(mList.get(position).getSection());
        holder.mAuthorName.setText(mList.get(position).getAuthor());
        // holder.mImageView.setImageResource(R.color.colorAccent);
        Glide.with(mContext)
                .load(mList.get(position).getImageUrl()).into(holder.mImageView);
        Glide.with(mContext)
                .load(mList.get(position).getAuthorImage()).into(holder.mAuthorImage);


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


}
