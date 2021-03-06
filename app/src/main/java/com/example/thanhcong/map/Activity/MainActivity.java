package com.example.thanhcong.map.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.thanhcong.map.CaculatorModules.DirectionModules.DirectionFinder;
import com.example.thanhcong.map.CaculatorModules.DirectionModules.DirectionFinderListener;
import com.example.thanhcong.map.CaculatorModules.DirectionModules.HttpConnector;
import com.example.thanhcong.map.CaculatorModules.DirectionModules.PlacesTask;
import com.example.thanhcong.map.CaculatorModules.DirectionModules.Route;
import com.example.thanhcong.map.R;
import com.example.thanhcong.map.database.DatabaseHelper;
import com.example.thanhcong.map.database.QueryData;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
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
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener,
        DirectionFinderListener,GoogleMap.OnMapClickListener
        ,GoogleMap.OnMyLocationButtonClickListener{

    private final String _URL="https://maps.googleapis.com/maps/api/place/autocomplete/json?input=";
    private final String _KEYPLACE="AIzaSyAT47CaIFnGlbdQOrsqHr8cDVKvd34wQ3I";
    private PolylineOptions polylineOptions;
    private boolean is_check_show,is_check_update,is_check_kill,is_default_postion=true;
    private static final String TAG = MainActivity.class.getSimpleName();
    private PlaceAutocompleteFragment placeAutocompleteFragment,f2;
    private AutoCompleteTextView f1;
    private final int MY_LOCATION_REQUEST_CODE = 100;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private SupportMapFragment supportMapFragment;
    Location first_location = null;
    View buttonposition;
    List<Marker> originMarkers = new ArrayList<>();
    List<Marker> destinationMarkers = new ArrayList<>();
    List<Polyline> polylinePaths = new ArrayList<>();
    List<LatLng>latLngList =new ArrayList<>();
    RelativeLayout.LayoutParams layoutParams;
    Button btn_nextmap;
    CardView card_place;
    LinearLayout linear_container;
    String text1 = "", text2 = "";
    LatLng location1,location2;
    MarkerOptions markerOptions=new MarkerOptions();
    Thread thread;
    DatabaseHelper database;
    PlacesTask placesTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database =new DatabaseHelper(getApplicationContext(),QueryData.DATABASE_NAME,null,1);
        database.query_excute(QueryData.CREATETABLE);
        Cursor cursor =database.query_data(QueryData.LOCATION_NEW_QUERY,null);
        if(cursor.moveToFirst()){
            double lat =Double.parseDouble(cursor.getString(1));
            double longt=Double.parseDouble(cursor.getString(2));
        }
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
                if(first_location==null){
                    mMap.clear();
                    MarkerOptions options =new MarkerOptions();
                    options.title(place.getAddress().toString());
                    options.position(place.getLatLng());
                    mMap.addMarker(options);
                    CameraUpdate move =CameraUpdateFactory.newLatLng(place.getLatLng());
                    mMap.moveCamera(move);
                    mMap.animateCamera(CameraUpdateFactory.zoomBy(17));
                    return;
                }
                sendRequest(new LatLng(first_location.getLatitude(), first_location.getLongitude()), place.getLatLng(), 0,0,0);
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
      /*  f1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                location1 = place.getLatLng();
                text1 = place.getAddress().toString();
                btn_nextmap.setText("TÌM ĐƯỜNG");
            }

            @Override
            public void onError(Status status) {

            }
        });*/

        f1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
             placesTask=new PlacesTask(getApplicationContext(),f1);
             placesTask.execute(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        f1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                HashMap<String,String>value= (HashMap<String, String>) adapterView.getItemAtPosition(position);
                LatLng latLng=HttpConnector.getLocationFromAddress(getApplicationContext(),value.get("description"));
                Toast.makeText(MainActivity.this, "values:"+latLng.toString(), Toast.LENGTH_SHORT).show();
                location1 =latLng;
                text1 =value.get("description");
                btn_nextmap.setText("TÌM ĐƯỜNG");
            }
        });

        f1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                    if(location1.toString().equalsIgnoreCase((new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude())).toString())){
                        is_default_postion=true;
                    }
                    else {
                        is_default_postion=false;
                    }
                    sendRequest(location1, location2, 1,0,0);
                    btn_nextmap.setLayoutParams(layoutParams);
                    linear_container.setLayoutParams(layoutParams);
                    is_check_show = true;
                    first_location=new Location(LocationManager.GPS_PROVIDER);
                    first_location.setLatitude(location1.latitude);
                    first_location.setLongitude(location1.longitude);
                }
            }
        });
    }

    private void addControlls() {
        linear_container = findViewById(R.id.linear_container);
        f1 =findViewById(R.id.f1);
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
        checkpermission();
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
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(this);
        mMap.setOnMapClickListener(this);
    }
    private void checkpermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_LOCATION_REQUEST_CODE);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_LOCATION_REQUEST_CODE);
            }
            return;
        } else {
            return;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_LOCATION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                      mMap.setMyLocationEnabled(true);
                      mMap.setOnMyLocationChangeListener(this);
                    }

                } else {
                    checkpermission();
                }
                return;
            }

        }
    }

    @Override
    public void onMyLocationChange(Location location) {

        if (first_location == null) {
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            View locationButton = ((View) buttonposition.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 60);
            locationButton.setLayoutParams(layoutParams);
            this.first_location = location;
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
            mMap.clear();
            mMap.moveCamera(center);
            mMap.animateCamera(zoom);
        } else {
            if(is_check_show==true||is_check_kill==true){
                if (is_default_postion) {
                    location1=new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude());
                }
                if(polylinePaths==null||polylinePaths.size()<=0||!PolyUtil.isLocationOnPath(new LatLng(location.getLatitude(),location.getLongitude())
                        ,latLngList,true,20.0f)){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location1,17));
                    sendRequest(location1,location2,1,1,0);
                }
                else {
                    if(!is_check_update){
                        is_check_update=true;
                        sendRequest(new LatLng(mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude()),location2,1,1,1);
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

    private void sendRequest(LatLng origin, LatLng destination, int request_code1,int request_code2,int request_code3) {
        new DirectionFinder(this, origin, destination, request_code1,request_code2,request_code3).execute();

    }

    @Override
    public void onDirectionFinderStart(int request_code,int request_code3) {
        if (originMarkers != null && request_code3!=1) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }
        if (destinationMarkers != null && request_code3!=1) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }
        if (polylinePaths != null && request_code3!=1) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route, int request_code1,int request_code2) {
        destinationMarkers = new ArrayList<>();
        polylinePaths = new ArrayList<>();
        @SuppressLint("RestrictedApi") PatternItem item1 =new PatternItem(1, 0.4f);
        List<PatternItem>items =new ArrayList<>();
        items.add(item1);
        if (route.size() > 0) {
            for (Route router : route) {
                String adressend = router.endAddress.substring(0, router.endAddress.indexOf(",", 2));
                String adressstart=router.startAddress.substring(0,router.startAddress.indexOf(",",2));
                latLngList=router.points;
                mMap.clear();
                if(is_default_postion){
                    markerOptions =new MarkerOptions();
                    markerOptions.title(adressstart+"  "+router.distance.text+"  "+router.duration.text);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
                    markerOptions.position(location1);
                    mMap.addMarker(markerOptions);
                }
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                        .title(adressend + "  " + router.distance.text + "  " + router.duration.text)
                        .position(router.endLocation)));
                if (request_code1 == 1) {
                    polylineOptions = new PolylineOptions().
                            geodesic(false).clickable(true).
                            color(Color.BLUE).pattern(items).
                            width(13);
                    mMap.getUiSettings().setRotateGesturesEnabled(true);
                    for (int i = 0; i < router.points.size(); i++)
                        polylineOptions.add(router.points.get(i));
                       if(request_code2==1){
                           mMap.moveCamera(CameraUpdateFactory.newLatLng(router.startLocation));
                       }
                       else{
                           mMap.moveCamera(CameraUpdateFactory.newLatLng(router.endLocation));
                       }
                       polylinePaths.add(mMap.addPolyline(polylineOptions));
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(router.endLocation, 16));
                    text2=router.endAddress;
                    f2.setText(router.endAddress);
                    placeAutocompleteFragment.setText(router.endAddress);
                }
            }
        } else {
            Toast.makeText(this, "Không tìm thấy quãng đường", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDistanceDurationSuccess(List<Route> routes, int code) {
       if(routes.size()>0){
           for(Marker marker : destinationMarkers){
               marker.remove();
           }
           for (Route route : routes){
               String adress = route.endAddress.substring(0, route.endAddress.indexOf(",", 2));
               destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                       .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                       .title(adress + "  " + route.distance.text + "  " + route.duration.text)
                       .position(route.endLocation)));
               Log.e("success_location","duration:"+route.duration.text+",distance:"+route.distance.text);
           }
       }
    }

    @Override
    public void onBackPressed() {
        if (is_check_show) {
            is_check_kill=false;
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

    @Override
    public void onMapClick(LatLng latLng) {
        if(!is_check_show){
           // Toast.makeText(this, "Dang o chon duong", Toast.LENGTH_SHORT).show();
            if(mMap==null){

            }
            else {
                sendRequest(new LatLng(first_location.getLatitude(), first_location.getLongitude()), latLng, 0,0,0);
                layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                layoutParams.setMargins(250, 0, 250, 0);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                btn_nextmap.setText("TÌM ĐƯỜNG");
                btn_nextmap.setLayoutParams(layoutParams);
                text1 = "Vị trí của bạn";
                location1 = new LatLng(first_location.getLatitude(), first_location.getLongitude());
                location2 = latLng;
            }
        }
        else
        {
           // Toast.makeText(this, "Dang o dan duong", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if(!is_check_show){
            if(mMap==null){

            }
            else {
                location1 = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                text1="Vị trí của tôi";
                f1.setText("Vị trí của tôi");
            }
        }
        return false;
    }

    @Override
    protected void onStop() {
        database.query_excute(QueryData.DELETE_LOCATION);
        SharedPreferences.Editor editor=getSharedPreferences("location",MODE_PRIVATE).edit();
        editor.clear();
        if(is_check_show){
            is_default_postion=true;
            database.query_excute(QueryData.SAVELOCATION(location2.latitude,location2.longitude,1));
            editor.putString("latitute",location2.latitude+"");
            editor.putString("longitute",location2.longitude+"");
            editor.commit();
            is_check_kill=true;
        }
        super.onStop();
    }
}
