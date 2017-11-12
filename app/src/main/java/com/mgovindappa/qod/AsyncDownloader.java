package com.mgovindappa.qod;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mgovindappa.qod.constant.Extras;
import com.mgovindappa.qod.model.QOD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Manjesh on 11/8/2017.
 */

// Defines Async task for fetching the JSON feed
public class AsyncDownloader<T> extends AsyncTask<String, Void, T> {
    private Context context;
    private String state = "";
    private AsyncCallbacks callback;

    public AsyncDownloader (Context context, AsyncCallbacks asyncCallback){
        this.context = context;
        this.callback = asyncCallback;
    }

    @Override
    protected T doInBackground(String... params) {

        String readData = "";
        InputStream inputStream;
        HttpURLConnection urlConnection = null;
        String result = "";
        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                int data = reader.read();
                while (data != -1)
                {
                    result += (char)data;
                    data = reader.read();
                }

                state = params[1];

                if (params[1].equals(Extras.CATEGORIES)) {
                    return parseCategories(result);
                }
                else if (params[1].equals(Extras.QOD)){
                    return parseQuote(result);
                }
            }
            else {
                state = "error";
                InputStream is = urlConnection.getErrorStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();
                while(data != -1)
                {
                    result += (char)data;
                    data = reader.read();
                }
                return parseError(result);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // we don't want to return null
        return null;
    }

    @Override
    protected void onPostExecute(T result) {

        super.onPostExecute(result);

        if (state.equals("error")) {

            callback.onPostExecuteCategories((HashMap<String, String>) result);

        }else if (state.equals(Extras.CATEGORIES)) {

            callback.onPostExecuteCategories((HashMap<String, String>) result);

        } else if (state.equals(Extras.QOD)) {

            callback.onPostExecuteQOD((QOD)result);
        }
    }

    // parse the error message that we got
    private T parseError(String error) {

        HashMap<String, String> errMessage = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(error);

            JSONObject jObject = jsonObject.getJSONObject("error");
            String message  = jObject.getString("message");
            errMessage.put("error", message);

        }  catch (Exception e)
        {
            e.printStackTrace();
        }
        return (T)errMessage;
    }


    // parse the QOD from the results
    private T parseQuote(String result) {

        String title = "";
        String quote = "";
        String author = "";

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject jObject = jsonObject.getJSONObject("contents");
            JSONArray jsonArray = jObject.getJSONArray("quotes");

            for (int i = 0 ; i < jsonArray.length() ; i++) {

                JSONObject jsonQuote = jsonArray.getJSONObject(i);
                Iterator<?> keys = jsonQuote.keys();

                while (keys.hasNext()) {

                    String key = keys.next().toString();
                    if (key.equals("title")) {

                        title = jsonQuote.get(key).toString();

                    } else if (key.equals("quote")) {

                        quote = jsonQuote.get(key).toString();

                    } else if (key.equals("author")) {

                        author = jsonQuote.get(key).toString();
                    }
                }
            }
           Log.i(">>>", " " + quote +  " "  + author +  " " + title);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (T)new QOD(title,quote,author);
    }

    // Parse the categories from the result
    private T parseCategories(String result) {

        HashMap<String, String> categories = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(result);

            JSONObject jObject = jsonObject.getJSONObject("contents").getJSONObject("categories");

            Iterator<String> keys = jObject.keys();

            while ( keys.hasNext() ){
                String key = (String)keys.next(); // First key in your json object
                String value = jObject.getString(key);
                categories.put(key, value);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return (T)categories;
    }
}
