package com.example.thanhcong.map.CaculatorModules.DirectionModules;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HttpConnector {
  public static String reponse(String url){
      try {
          URL ul =new URL(url);
          HttpURLConnection connection = (HttpURLConnection) ul.openConnection();
          InputStream stream =connection.getInputStream();
          BufferedReader reader =new BufferedReader(new InputStreamReader(stream));
          int a;
          String line="";
          StringBuffer respone =new StringBuffer();
          while ((line=reader.readLine())!=null){
              respone.append(line);
          }
          if(respone.length()>0){
              return respone.toString();
          }

      } catch (IOException e) {
          e.printStackTrace();
      }
      return "Error!";
  }

    public static LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }
}
