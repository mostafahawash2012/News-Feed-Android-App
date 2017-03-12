package com.example.mostafa.newsfeed.models;

/**
 * Created by mostafa on 2/28/17.
 */

public class NewsModel {
    public String mId;
    public String mUrl;
    public String mSite;
    public String mTitle;
    public String mDate;
    public String mImagePath;
    public String mText;

    public NewsModel(String id,String url,String site,String title,String date,String image,String text){
        mId = id;
        mUrl=url;
        mDate=date;
        mImagePath=image;
        mSite=site;
        mText=text;
        mTitle=title;
    }
}
