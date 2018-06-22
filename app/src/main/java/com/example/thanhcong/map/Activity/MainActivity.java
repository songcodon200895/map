package com.example.thanhcong.map.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.thanhcong.map.CaculatorModules.DirectionModules.DirectionFinder;
import com.example.thanhcong.map.CaculatorModules.DirectionModules.DirectionFinderListener;
import com.example.thanhcong.map.CaculatorModules.DirectionModules.Route;
import com.example.thanhcong.map.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener,
        PlaceSelectionListener,DirectionFinderListener{

    private final String database_name="Location.sqlite";
    private static final String TAG = MainActivity.class.getSimpleName();
    private PlaceAutocompleteFragment placeAutocompleteFragment;
    private final int MY_LOCATION_REQUEST_CODE = 100;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleMap mMap;
    SupportMapFragment supportMapFragment,f1,f2;
    Location first_location=null;
    View buttonposition;
    CardView cardView_cotainer;
    TextView txt_adress,txt_time,txt_distance;
    ProgressDialog progressDialog;
    List<Marker> originMarkers = new ArrayList<>();
    List<Marker> destinationMarkers = new ArrayList<>();
    List<Polyline> polylinePaths = new ArrayList<>();
    Button btn_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControlls();
        addEvents();
    }

    private void addEvents() {
     placeAutocompleteFragment.setOnPlaceSelectedListener(this);
     btn_search.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {

         }
     });
    }

    private void addControlls() {
        btn_search=findViewById(R.id.btn_search);
        txt_adress=findViewById(R.id.txt_adress);
        txt_distance=findViewById(R.id.txt_distance);
        txt_time=findViewById(R.id.txt_time);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        placeAutocompleteFragment= (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        buttonposition=supportMapFragment.getView();
        supportMapFragment.getMapAsync(this);
        placeAutocompleteFragment.setHint("Nhập địa chỉ tìm kiếm");
        cardView_cotainer=findViewById(R.id.cardview_container);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return;
    }

        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(this);
        View locationButton = ((View) buttonposition.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        // and next place it, on bottom right (as Google Maps app)
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                locationButton.getLayoutParams();
        // position on right bottom
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 30, 60);
}



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
                Toast.makeText(this, "Error request permission position", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMyLocationChange(Location location) {

        if(first_location==null){
            this.first_location=location;
            CameraUpdate center =CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
            CameraUpdate zoom =CameraUpdateFactory.zoomTo(14);
            mMap.clear();
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        }
    }

    //Place selection
    @Override
    public void onPlaceSelected(Place place) {
        Log.d(TAG, "onPlaceSelected: " + place);
        MarkerOptions markerOptions =new MarkerOptions();
        markerOptions.position(place.getLatLng());
        markerOptions.title(place.getName().toString());
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        //mMap.addMarker(markerOptions);
        txt_adress.setText(place.getAddress());
        Log.e("myposition:",new LatLng(first_location.getLatitude(),first_location.getLongitude()).toString());
        Log.e("placeposition",place.getLatLng().toString());
        sendRequest(new LatLng(first_location.getLatitude(),first_location.getLongitude()),place.getLatLng());
    }

    @Override
    public void onError(Status status) {
       Log.e("error",status.toString());
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
    private void sendRequest(LatLng origin ,LatLng destination) {

        new DirectionFinder(this, origin, destination).execute();

    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }
        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }
        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {

                polyline.remove();
            }
        }
    }
    @Override
    public void onDirectionFinderSuccess(List<Route> route) {
        progressDialog.dismiss();

        polylinePaths = new ArrayList<>();

        originMarkers = new ArrayList<>();

        destinationMarkers = new ArrayList<>();

        for (Route router : route) {

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(router.startLocation, 16));

            ((TextView) findViewById(R.id.txt_time)).setText(router.duration.text);

            ((TextView) findViewById(R.id.txt_distance)).setText(router.distance.text);
            Log.e("distance",router.distance.text);
            originMarkers.add(mMap.addMarker(new MarkerOptions()

                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))

                    .title(router.startAddress)

                    .position(router.startLocation)));

            destinationMarkers.add(mMap.addMarker(new MarkerOptions()

                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))

                    .title(router.endAddress)

                    .position(router.endLocation)));
            PolylineOptions polylineOptions = new PolylineOptions().

                    geodesic(true).

                    color(Color.BLUE).

                    width(10);
            for (int i = 0; i < router.points.size(); i++)

                polylineOptions.add(router.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

        }
    }
}
