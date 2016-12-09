package code.cameo.placeautocomplete;

import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import cameo.code.placeautocomplete.PlaceAutoCompleteFragment;
import cameo.code.placeautocomplete.PlaceAutoCompleteSearchBarFragment;
import cameo.code.placeautocomplete.PlaceModel;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        PlaceAutoCompleteFragment.onPlaceSelectedListener {

    private GoogleMap mMap;
    private PlaceAutoCompleteFragment mAutocompleteFragment;
    private String mMapWebApiKey;
    private CameraPosition cameraPosition;
    private PlaceAutoCompleteSearchBarFragment mAutocompleteSearchBarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initView();

        initVariable();

        initListeners();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initVariable() {
        mMapWebApiKey = getString(R.string.google_maps_browser_key);
    }

    private void initListeners() {
        mAutocompleteFragment.setOnPlaceSelectedListener(this);
        mAutocompleteSearchBarFragment.setOnPlaceSelectedListener(this);
    }

    private void initView() {
        mAutocompleteFragment = (PlaceAutoCompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        mAutocompleteSearchBarFragment = (PlaceAutoCompleteSearchBarFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_search_bar);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onPlaceSelected(PlaceModel placeModel) {
        LatLng selectedPlace = new LatLng(placeModel.getLat(), placeModel.getLng());
        setMapData(selectedPlace);

        mMap.addMarker(new MarkerOptions().position(selectedPlace).title("Marker in "+placeModel.getMainPlace()));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(selectedPlace));

    }

    private void setMapData(LatLng selectedPlace) {

        cameraPosition = new CameraPosition.Builder()
                .target(selectedPlace)
                .zoom(17)
                .build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public String getWebApiKey() {
        return mMapWebApiKey;
    }
}
