package com.example.thanhcong.map.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.thanhcong.map.CaculatorModules.DirectionModules.DirectionFinder;
import com.example.thanhcong.map.CaculatorModules.DirectionModules.DirectionFinderListener;
import com.example.thanhcong.map.CaculatorModules.DirectionModules.Route;
import com.example.thanhcong.map.R;
import com.example.thanhcong.map.Services.LocationChangeLServices;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener,
        DirectionFinderListener{

    PolylineOptions polylineOptions;
    private boolean is_check_show,is_check_update,is_check_click_f1;
    private final String database_name = "Location.sqlite";
    private static final String TAG = MainActivity.class.getSimpleName();
    private PlaceAutocompleteFragment placeAutocompleteFragment, f1, f2;
    private final int MY_LOCATION_REQUEST_CODE = 100;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleMap mMap;
    SupportMapFragment supportMapFragment;
    Location first_location = null;
    View buttonposition;
    ProgressDialog progressDialog;
    List<Marker> originMarkers = new ArrayList<>();
    List<Marker> destinationMarkers = new ArrayList<>();
    List<Polyline> polylinePaths = new ArrayList<>();
    RelativeLayout.LayoutParams layoutParams;
    Button btn_nextmap;
    CardView card_place;
    LinearLayout linear_container;
    String text1 = "", text2 = "";
    LatLng location1;
    LatLng location2;
    MarkerOptions markerOptions;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControlls();
        addEvents();
    }

    private void addEvents() {
        if (mMap != null) {
            mMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {
                    Toast.makeText(MainActivity.this, "Location" + location.toString(), Toast.LENGTH_SHORT).show();
                    Log.e("is_click", "true");
                }
            });
        }
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                sendRequest(new LatLng(first_location.getLatitude(), first_location.getLongitude()), place.getLatLng(), 0,0);
                layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                layoutParams.setMargins(250, 0, 250, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                btn_nextmap.setText("TÌM ĐƯỜNG");
                btn_nextmap.setLayoutParams(layoutParams);
                text1 = "Vị trí của bạn";
                location1 = new LatLng(first_location.getLatitude(), first_location.getLongitude());
                location2 = place.getLatLng();
                text2 = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {

            }
        });
        f1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                location1 = place.getLatLng();
                text1 = place.getAddress().toString();
                btn_nextmap.setText("TÌM ĐƯỜNG");
                is_check_click_f1=true;
            }

            @Override
            public void onError(Status status) {

            }
        });
        f2.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                btn_nextmap.setText("TÌM ĐƯỜNG");
                location2 = place.getLatLng();
                text2 = place.getAddress().toString().substring(0, place.getAddress().toString().indexOf(",", 1));
            }

            @Override
            public void onError(Status status) {

            }
        });
        btn_nextmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutParams = new RelativeLayout.LayoutParams(0, 0);
                layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.maps);
                card_place.setLayoutParams(layoutParams);
                RelativeLayout.LayoutParams pagram_linear_container = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300);
                pagram_linear_container.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                pagram_linear_container.setMargins(100, 100, 100, 0);
                linear_container.setLayoutParams(pagram_linear_container);
                if (btn_nextmap.getText().toString().equalsIgnoreCase("TÌM ĐƯỜNG")) {
                    f1.setText(text1);
                    f2.setText(text2);
                    btn_nextmap.setText("Bắt đầu");
                } else if (btn_nextmap.getText().toString().equalsIgnoreCase("BẮT ĐẦU")) {
                    sendRequest(location1, location2, 1,0);
                    btn_nextmap.setLayoutParams(layoutParams);
                    linear_container.setLayoutParams(layoutParams);
                    is_check_show = true;
                }
            }
        });
    }

    private void addControlls() {
        linear_container = findViewById(R.id.linear_container);
        f1 = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.f1);
        f2 = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.f2);
        f1.setHint("Vị trí bắt đầu");
        f2.setHint("Vị trí kết thúc");
        card_place = findViewById(R.id.cardview);
        btn_nextmap = findViewById(R.id.btn_nextmap);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        buttonposition = supportMapFragment.getView();
        supportMapFragment.getMapAsync(this);
        placeAutocompleteFragment.setHint("Nhập địa chỉ tìm kiếm");
        layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        layoutParams.addRule(RelativeLayout.LEFT_OF, R.id.maps);
        btn_nextmap.setLayoutParams(layoutParams);
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
        markerOptions = new MarkerOptions();
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

        if (first_location == null) {
            this.first_location = location;
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
            mMap.clear();
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        } else {
            if(is_check_show==true && is_check_click_f1==false){
                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(17);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);
                if(!is_check_update){
                    is_check_update=true;
                    sendRequest(new LatLng(location.getLatitude(),location.getLongitude()),location2,1,1);
                    thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                while(true) {
                                    sleep(5000);
                                    is_check_update=false;
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                }
            }
        }
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

    private void sendRequest(LatLng origin, LatLng destination, int request_code1,int request_code2) {

        new DirectionFinder(this, origin, destination, request_code1,request_code2).execute();

    }

    @Override
    public void onDirectionFinderStart(int request_code) {
        if(request_code==0){
            progressDialog = ProgressDialog.show(this, "Please wait.",
                    "Finding direction..!", true);
        }

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
            for (Polyline polyline : polylinePaths) {

                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route, int request_code) {
        progressDialog.dismiss();
        destinationMarkers = new ArrayList<>();
        polylinePaths = new ArrayList<>();
        if (route.size() > 0) {
            for (Route router : route) {
                mMap.clear();
                String adress = router.endAddress.substring(0, router.endAddress.indexOf(",", 2));
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()

                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))

                        .title(adress + "  " + router.distance.text + "  " + router.duration.text)

                        .position(router.endLocation)));
                if (request_code == 1) {
                   // mMap.moveCamera(CameraUpdateFactory.newLatLng(router.startLocation));
                    polylineOptions = new PolylineOptions().
                            geodesic(true).
                            color(Color.BLUE).
                            width(10);
                    mMap.getUiSettings().setRotateGesturesEnabled(true);
                    for (int i = 0; i < router.points.size(); i++)
                        polylineOptions.add(router.points.get(i));
                    polylinePaths.add(mMap.addPolyline(polylineOptions));
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(router.endLocation, 16));
                }
            }
        } else {
            Toast.makeText(this, "Không tìm thấy quãng đường", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (is_check_show) {
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(100, 200, 100, 0);
            card_place.setLayoutParams(layoutParams);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 110);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            params.setMargins(250, 0, 250, 0);
            btn_nextmap.setLayoutParams(params);
            btn_nextmap.setText("Tìm đường");
            is_check_show = false;
            mMap.clear();
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
        }
        else {
            super.onBackPressed();
        }

    }
}
