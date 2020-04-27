package pl.proenix.android.us2pum.lab5pointofinterest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMap extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private View view;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PLACE_LONGITUDE = "place_longitude";
    private static final String PLACE_LATITUDE = "place_latitude";
    private static final String PLACE_NAME = "place_name";

    private Double placeLongitude;
    private Double placeLatitude;
    private String placeName;

    public FragmentMap() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param placeLongitude Longitude
     * @param placeLatitude Latitude
     * @return A new instance of fragment FragmentMap.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMap newInstance(Double placeLongitude, Double placeLatitude, String placeName) {
        FragmentMap fragment = new FragmentMap();
        Bundle args = new Bundle();
        args.putDouble(PLACE_LONGITUDE, placeLongitude);
        args.putDouble(PLACE_LATITUDE, placeLatitude);
        args.putString(PLACE_NAME, placeName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placeLongitude = getArguments().getDouble(PLACE_LONGITUDE);
            placeLatitude = getArguments().getDouble(PLACE_LATITUDE);
            placeName = getArguments().getString(PLACE_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(getContext(), "Map could not be loaded.", Toast.LENGTH_LONG).show();
        }

        return view;
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
        LatLng place = new LatLng(placeLongitude, placeLatitude);
        mMap.addMarker(new MarkerOptions().position(place).title(placeName));
        // Move Camera to place coordinates and set zoom to 15.0f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 15.0f));
    }
}
