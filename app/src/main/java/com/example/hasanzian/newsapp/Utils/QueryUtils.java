package com.example.hasanzian.newsapp.Utils;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

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
     * @param jsonString to parse
     */
    public static ArrayList<Model> extractNews(String jsonString) {

        // Create an empty ArrayList that we can start adding iNews to
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
            //for loop for extracting each objects
            for(int i= 0 ; i < resultsArray.length() ;i++){
                String authorName = "";
                String authorImage = "";
                JSONObject currentObject = resultsArray.getJSONObject(i);
                String webTitle = currentObject.getString("webTitle");
                String sectionName = currentObject.getString("sectionName");
                String date = currentObject.getString("webPublicationDate");
                String webUrl  = currentObject.getString("webUrl");
                JSONObject fields = currentObject.getJSONObject("fields");
                String thumbnail = fields.getString("thumbnail");
                JSONArray tagsArray = currentObject.getJSONArray("tags");
                   for(int j = 0 ;j < tagsArray.length() ; j++ ){
                        JSONObject tagObject = tagsArray.getJSONObject(j);
                        authorName = tagObject.getString("webTitle");
                        if(tagObject.has("bylineImageUrl")){
                            authorImage = tagObject.getString("bylineImageUrl");}
                   }

                Model Model = new Model(webTitle,webUrl,thumbnail ,sectionName,date,authorName,authorImage);
                iNews.add(Model);
                Log.d("List",iNews.toString());


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

}
