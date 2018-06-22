package com.example.thanhcong.map.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;

public class LocationService extends Service implements LocationListener {
    Context context;
    LatLng _start_position;
    LatLng _end_position;
    public LocationService(Context context,LatLng _start_position,LatLng _end_position) {
        this.context=context;
        this._start_position=_start_position;
        this._end_position=_end_position;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
     _start_position=new LatLng(location.getLatitude(),location.getLongitude());
     if(_start_position==_end_position){
         //End service
         return;
     }
     _start_position=new LatLng(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }
}
