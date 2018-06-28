package com.example.thanhcong.map.database;

public class QueryData {
    public static String DATABASE_NAME="LocationSave.sqlite";
     //Create table Location
    public static String CREATETABLE="CREATE TABLE IF NOT EXISTS Location(Id INTEGER PRIMARY KEY AUTOINCREMENT" +
            ",Latitude VARCHAR(30) NOT NULL,Longitude VARCHAR(30) NOT NULL,FromOrTo INTEGER NOT NULL)";
    //Save location
    public static String SAVELOCATION(double Latitute,double Longidete,int FromOrTo){
        return "INSERT INTO Location(Id,Latitude,Longitude,FromOrTo) VALUES " +
                "(null,'"+Latitute+"','"+Longidete+"',"+FromOrTo+")";
    }
    //Query get locatted
    public static String LOCATION_NEW_QUERY="SELECT * FROM Location";
    //Delete location
    public static String DELETE_LOCATION ="DELETE FROM Location";
}
