package com.example.hasanzian.newsapp.adaptor;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hasanzian.newsapp.R;
import com.example.hasanzian.newsapp.databinding.ListItemBinding;
import com.example.hasanzian.newsapp.databinding.ListItemDarkBinding;
import com.example.hasanzian.newsapp.notification.AppNotificationChannel;
import com.example.hasanzian.newsapp.utils.Model;
import com.example.hasanzian.newsapp.utils.QueryUtils;

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

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // night mode
        if (AppNotificationChannel.nightModeSettings(parent.getContext())) {
            ListItemDarkBinding binding = ListItemDarkBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            mContext = parent.getContext();
            return new myViewHolder(binding);
        } else {
            ListItemBinding binding = ListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            mContext = parent.getContext();
            return new myViewHolder(binding);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.bind(mList.get(position));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * myViewHolder class initializing view
     */
    class myViewHolder extends RecyclerView.ViewHolder {
        private ListItemDarkBinding darkBinding;
        private ListItemBinding binding;

        myViewHolder(ListItemDarkBinding binding) {
            super(binding.getRoot());
            this.darkBinding = binding;
        }

        myViewHolder(ListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }


        public void bind(Model model) {
            if (AppNotificationChannel.nightModeSettings(mContext)) {
                setDarkBinding(darkBinding,model);
            } else {
               setLightBinding(binding,model);
            }

        }

        private void setDarkBinding(ListItemDarkBinding darkBinding, Model model) {
            // setting custom font -- Product Sans
            Typeface headingFont, regularFont;
            regularFont = QueryUtils.regularFont(mContext);
            headingFont = QueryUtils.headingFont(mContext);
            darkBinding.date.setTypeface(regularFont);
            darkBinding.heading.setTypeface(headingFont);
            darkBinding.section.setTypeface(regularFont);
            darkBinding.author.setTypeface(regularFont);

            darkBinding.heading.setText(model.getHeading());
            // Format string to format
            String formattedString = QueryUtils.formatDate(model.getDate());
            darkBinding.date.setText(formattedString);
            darkBinding.section.setText(model.getSection());
            darkBinding.author.setText(String.format("by %s", model.getAuthor()));

            Glide.with(mContext).setDefaultRequestOptions(QueryUtils.requestOptions()).load(model.getImageUrl()).into(darkBinding.image);
            Glide.with(mContext).setDefaultRequestOptions(QueryUtils.requestOptions()).load(model.getAuthorImage()).into(darkBinding.authorImage);

            if (QueryUtils.imageOptions(mContext)) {
                darkBinding.cardImage.setVisibility(View.VISIBLE);
            } else {
                darkBinding.cardImage.setVisibility(View.GONE);
            }
            if (QueryUtils.authorImage(mContext)) {
                darkBinding.authorImage.setVisibility(View.VISIBLE);
            } else {
                darkBinding.authorImage.setVisibility(View.GONE);
            }
        }

        private void setLightBinding(ListItemBinding binding, Model model) {
            // setting custom font -- Product Sans
            Typeface headingFont, regularFont;
            regularFont = QueryUtils.regularFont(mContext);
            headingFont = QueryUtils.headingFont(mContext);
            binding.date.setTypeface(regularFont);
            binding.heading.setTypeface(headingFont);
            binding.section.setTypeface(regularFont);
            binding.author.setTypeface(regularFont);

            binding.heading.setText(model.getHeading());
            // Format string to format
            String formattedString = QueryUtils.formatDate(model.getDate());
            binding.date.setText(formattedString);
            binding.section.setText(model.getSection());
            binding.author.setText(String.format("by %s", model.getAuthor()));

            Glide.with(mContext).setDefaultRequestOptions(QueryUtils.requestOptions()).load(model.getImageUrl()).into(binding.image);
            Glide.with(mContext).setDefaultRequestOptions(QueryUtils.requestOptions()).load(model.getAuthorImage()).into(binding.authorImage);

            if (QueryUtils.imageOptions(mContext)) {
                binding.cardImage.setVisibility(View.VISIBLE);
            } else {
                binding.cardImage.setVisibility(View.GONE);
            }
            if (QueryUtils.authorImage(mContext)) {
                binding.authorImage.setVisibility(View.VISIBLE);
            } else {
                binding.authorImage.setVisibility(View.GONE);
            }
        }
    }

}
