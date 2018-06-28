package com.example.thanhcong.map.CaculatorModules.DirectionModules;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
}
