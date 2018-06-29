package com.example.thanhcong.map.CaculatorModules.DirectionModules;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

import com.example.thanhcong.map.Adapter.SimpleAdapterCustom;
import com.example.thanhcong.map.R;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>> {

    AutoCompleteTextView autoCompleteTextView;
    Context context;
    public  ParserTask(Context context,AutoCompleteTextView autoCompleteTextView){
        this.autoCompleteTextView=autoCompleteTextView;
        this.context=context;
    }

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
        List<HashMap<String,String>>list =result;
        String[] from = new String[]{"description"};
        int[] to = new int[]{android.R.id.text1};
        // Creating a SimpleAdapter for the AutoCompleteTextView
        SimpleAdapter adapter = new SimpleAdapter(context, list,android.R.layout.simple_list_item_1, from, to);
        // Setting the adapter
        autoCompleteTextView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
