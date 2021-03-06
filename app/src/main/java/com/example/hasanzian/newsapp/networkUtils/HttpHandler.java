package com.example.hasanzian.newsapp.networkUtils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Http request handler class
 */

public class HttpHandler {


    public HttpHandler() {
    }

    public String makeHttpRequest(URL url) throws IOException {
        //returning a json String from this method
        String jsonResponse = "";
        //url connection
        HttpURLConnection urlConnection = null;
        //input Stream from network
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* read time*/);
            urlConnection.setConnectTimeout(15000 /*connection time out*/);
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = convertInputStreamToJsonString(inputStream);
                Log.e("responseCode ", "response code: " + urlConnection.getResponseCode());
            } else {
                Log.e("responseCode ", "Error response code: " + urlConnection.getResponseCode());
                jsonResponse = "Code: " + urlConnection.getResponseCode();
                Log.e("Json", jsonResponse);
            }



        } catch (IOException e) {
            //handle exception
        } finally {
            //closing url connection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            //closing input stream
            if (inputStream != null) {
                inputStream.close();
            }
        }
        //returning json data
        return jsonResponse;
    }

    private String convertInputStreamToJsonString(InputStream inputStream) {





        /* Java.io.BufferedReader class reads text from a character-input stream,
         * buffering characters so as to provide for the efficient
         * reading of sequence of characters
         * */
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream , Charset.forName("UTF-8")));
        //string Builder is used to build json string
        StringBuilder stringBuilder = new StringBuilder();
        // string
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {   //closing input stream after conversion is done
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // returning string
        return stringBuilder.toString();
    }
}
