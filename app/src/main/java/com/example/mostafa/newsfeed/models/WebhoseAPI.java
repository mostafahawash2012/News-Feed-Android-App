package com.example.mostafa.newsfeed.models;

/**
 * Created by mostafa on 3/1/17.
 */

public class WebhoseAPI {
    //URL constans
    public static final String BASE_URL="https://webhose.io/search";
    public static final String TOKEN_KEY = "token";
    public static final String API_KEY = "09efcf63-1c83-47f4-b1ff-62a433a5a999";
    public static final String FORMAT = "format";
    public static final String FORMAT_VALUE = "json";
    public static final String QUERY = "q";
    public static final String QUERY_VALUE_GLOBAL="(site_type:news)";
    public static final String SIZE = "size";
    public static final String SIZE_VALUE = "15";
    public static final String LANGUAGE = "language";
    public static final String LANGUAGE_ENGLISH_VALUE="english";
    public static final String SORT = "sort";
    public static final String SORT_VALUE="relevancy";
    public static final String SITE_TYPE = "site_type";
    public static final String SITE_TYPE_VALUE = "news";

    // Json constants for Response
    public static final String POSTS = "posts";
    public static final String THREAD = "thread";
    public static final String UUID = "uuid";
    public static final String URL = "url";
    public static final String SITE = "site";
    public static final String TITLE = "title";
    public static final String PUBLISHED = "published";
    public static final String MAIN_IMAGE = "main_image";
    public static final String TEXT = "text";
}
