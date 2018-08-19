package com.example.hasanzian.newsapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.hasanzian.newsapp.Adaptor.RecyclerAdaptor;
import com.example.hasanzian.newsapp.Utils.Model;
import com.example.hasanzian.newsapp.Utils.QueryUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    RecyclerAdaptor adaptor;
    List<Model> mList = new ArrayList<>();
    private static final String URL_API = "https://content.guardianapis.com/search?q=&format=json&show-tags=contributor&show-fields=starRating,headline,thumbnail,short-url&order-by=newest&api-key=441da542-bd64-4060-b81c-eff647cb6f27";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        MyTask myTask = new MyTask();
        myTask.execute();


    }

    public class MyTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler httpHandler = new HttpHandler();
            String jsonString ="";
            try {
                //getting Json String
                jsonString = httpHandler.makeHttpRequest(createUrl(URL_API));
                mList = QueryUtils.extractNews(jsonString);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adaptor = new RecyclerAdaptor(mList);
            mRecyclerView.setAdapter(adaptor);
        }

         /* Returns new URL object from the given string URL.
           */
        private  URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                Log.e("LOG_TAG", "Problem building the URL ", e);
            }
            return url;
        }




    }


}
