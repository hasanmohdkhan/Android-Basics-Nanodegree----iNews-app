package com.example.hasanzian.newsapp.utils;

/**
 * Models
 */

public class Model {
    private String mHeading;
    private String mArticleUrl;
    private String mImageUrl;
    private String mDate;
    private String mSection;
    private String mAuthor;
    private String mAuthorImage;

    public Model(String mHeading, String mArticleUrl, String mImageUrl, String mDate, String mSection, String mAuthor, String mAuthorImage) {
        this.mHeading = mHeading;
        this.mArticleUrl = mArticleUrl;
        this.mImageUrl = mImageUrl;
        this.mDate = mDate;
        this.mSection = mSection;
        this.mAuthor = mAuthor;
        this.mAuthorImage = mAuthorImage;
    }

    public String getHeading() {
        return mHeading;
    }

    public String getArticleUrl() {
        return mArticleUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getDate() {
        return mDate;
    }

    public String getSection() {
        return mSection;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getAuthorImage() {
        return mAuthorImage;
    }
}
