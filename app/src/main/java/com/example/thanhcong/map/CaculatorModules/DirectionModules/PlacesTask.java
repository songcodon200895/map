package com.example.thanhcong.map.CaculatorModules.DirectionModules;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.SimpleAdapter;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class PlacesTask extends AsyncTask<String, Integer, List<HashMap<String,String>>> {

    JSONObject jObject;

    @Override
    protected List<HashMap<String, String>> doInBackground(String... jsonData) {

        List<HashMap<String, String>> places = null;

        PlaceJSONParser placeJsonParser = new PlaceJSONParser();

        try {
            jObject = new JSONObject(jsonData[0]);

            // Getting the parsed data as a List construct
            places = placeJsonParser.parse(jObject);

        } catch (Exception e) {
            Log.e("e", e.toString());
        }
        return places;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> result) {

        String[] from = new String[]{"description"};
        int[] to = new int[]{android.R.id.text1};
    }
}
