package com.example.hasanzian.newsapp.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.request.RequestOptions;
import com.example.hasanzian.newsapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Helper methods related to requesting and receiving News data from Guardian
 */
public final class QueryUtils {
    public static String CODE = "";
    public static String NO_DATA = "";
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link Model} objects that has been built up from
     * parsing a JSON response.
     *
     * @param jsonString to parse
     */
    public static ArrayList<Model> extractNews(String jsonString) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding iNews to list
        ArrayList<Model> iNews = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // root object
            JSONObject root = new JSONObject(jsonString);
            // Extracting the response object
            JSONObject responseObject = root.getJSONObject("response");
            // Extracting result object from response object
            JSONArray resultsArray = responseObject.getJSONArray("results");
            if (resultsArray.length() == 0) {
                NO_DATA = "NO data available";
            }
            //for loop for extracting each objects
            for (int i = 0; i < resultsArray.length(); i++) {
                String authorName = "";
                String authorImage = "";
                JSONObject currentObject = resultsArray.getJSONObject(i);
                String webTitle = currentObject.getString("webTitle");
                String sectionName = currentObject.getString("sectionName");
                String date = currentObject.getString("webPublicationDate");
                String webUrl = currentObject.getString("webUrl");
                JSONObject fields = currentObject.getJSONObject("fields");
                String thumbnail = fields.getString("thumbnail");
                JSONArray tagsArray = currentObject.getJSONArray("tags");
                // check if tag array has any entry or not
                if (tagsArray.length() == 0) {
                    authorName = "Guardian";
                } else {
                    for (int j = 0; j < tagsArray.length(); j++) {
                        JSONObject tagObject = tagsArray.getJSONObject(j);
                        authorName = tagObject.getString("webTitle");
                        if (tagObject.has("bylineImageUrl")) {
                            authorImage = tagObject.getString("bylineImageUrl");
                        }
                    }
                }

                Model model = new Model(webTitle, webUrl, thumbnail, date, sectionName, authorName, authorImage);
                iNews.add(model);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }

        // Return the list of iNews
        return iNews;
    }

    /**
     * formats the date in the required format
     *
     * @param publishDate date to be formatted
     * @return formatted date in the form of a string
     */
    public static String formatDate(String publishDate) {
        String formattedDate = "";
        //define input date format
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        //define output date format
        SimpleDateFormat output = new SimpleDateFormat("dd MMM yyyy, HH:mm ", Locale.getDefault());
        try {
            //parse and format the input date
            Date date = input.parse(publishDate);
            formattedDate = output.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }


    /**
     * check the status of network
     *
     * @param context reference to calling Activity
     * @return active network information true/false
     */
    public static boolean isConnectedToNetwork(Context context) {
        // get reference to ConnectivityManager
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            // get the current status of network
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * To get the custom font Product Sans Regular
     *
     * @param mContext context of app
     * @return custom font location of Product Sans Regular
     */
    public static Typeface regularFont(Context mContext) {
        AssetManager assetManager = mContext.getApplicationContext().getAssets();
        return Typeface.createFromAsset(assetManager, "fonts/Product Sans Regular.ttf");
    }

    /**
     * To get the custom font PProduct Sans Bold
     *
     * @param mContext context of app
     * @return custom font location of Product Sans Bold
     */
    public static Typeface headingFont(Context mContext) {
        AssetManager assetManager = mContext.getApplicationContext().getAssets();
        return Typeface.createFromAsset(assetManager, "fonts/Product Sans Bold.ttf");
    }

    /**
     * Glide place holder setup
     *
     * @return RequestOptions
     */
    public static RequestOptions requestOptions() {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_launcher_foreground);
        requestOptions.error(R.drawable.gurdian);
        return requestOptions;
    }
}
