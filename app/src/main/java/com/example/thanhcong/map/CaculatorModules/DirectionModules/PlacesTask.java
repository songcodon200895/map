package com.example.thanhcong.map.CaculatorModules.DirectionModules;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class PlacesTask extends AsyncTask<String, Void, String> {
    AutoCompleteTextView completeTextView;
    ParserTask parserTask;
    Context context;
    public PlacesTask(Context context, AutoCompleteTextView completeTextView){
        this.completeTextView=completeTextView;
        this.context=context;
    }

    @Override
    protected String doInBackground(String... place) {
        String data = "";
        String key = "key=AIzaSyAT47CaIFnGlbdQOrsqHr8cDVKvd34wQ3I";
        String input="";
        try {
            input = "input=" + URLEncoder.encode(place[0], "utf-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String type="types=geocode";
        String senser="sensor=false";
        String parameters = input+"&"+type+"&"+senser+"&"+key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;
        try{
            data = downloadUrl(url);
        }catch(Exception e){
        }
        Log.e("dowloaded",data.toString());
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        parserTask = new ParserTask(context,completeTextView);
        parserTask.execute(result);
    }
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }catch(Exception e){
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
