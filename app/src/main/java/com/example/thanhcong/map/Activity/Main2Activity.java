package com.example.thanhcong.map.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.thanhcong.map.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class Main2Activity extends AppCompatActivity implements PlaceSelectionListener{

    int PLACE_AUTOCOMPLETE_REQUEST_CODE=100;

    PlaceAutocompleteFragment f1,f2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        addControlls();
    }

    private void addControlls() {
        f1= (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.fpl1);
        f2= (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.fpl2);
        f1.setHint("Nhập vị trí bắt đầu");
        f2.setHint("Nhập vị trí muốn đi");
    }

    @Override
    public void onPlaceSelected(Place place) {
      @SuppressLint("ResourceType") String text1= f1.getString(R.id.fpl1);
      @SuppressLint("ResourceType") String text2=f2.getString(R.id.fpl2);
      if(text1==null && text2==null){
          return;
      }
      if(text1!=null && text2 !=null){
          Log.e("get_adress","đã có đủ dữ liệu");
      }

      if(text1==null || text2 ==null){
          Log.e("get_adress","Địa chỉ 1 hoặc 2 trống");
      }
    }

    @Override
    public void onError(Status status) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.e("placeres", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("placeres", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
}
