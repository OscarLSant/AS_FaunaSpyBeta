package com.example.as_faunaspyv3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class addLocationFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener {

    GoogleMap nMap;
    private FloatingActionButton btnAceptar;
    double lon = 0.0;
    double lat = 0.0 ;

    /*public static addLocationFragment newInstance(double latitude, double longitude) {
        addLocationFragment fragmentLocation = new addLocationFragment();
        Bundle args = new Bundle();
        args.putDouble("key_latitude", latitude);
        args.putDouble("key_longitude", longitude);
        fragmentLocation.setArguments(args);
        return fragmentLocation;
    }*/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        /*savedInstanceState = getArguments();
        if(savedInstanceState != null){
            getLocationClass = (addGetLocation) savedInstanceState.getSerializable("localizacion");
            // Verificar si el casting fue exitoso antes de usar el objeto
            if (getLocationClass != null) {
                //
                Log.d("VALOR NULO", "EL VALOR DEL OBJETO ES NULO");
            } else {
                Log.d("VALOR CORRECTO", "EL OBJETO TIENE VALOR");
            }
        }*/

        View view =  inflater.inflate(R.layout.fragment_add_location, container, false);

        btnAceptar = (FloatingActionButton) view.findViewById(R.id.aceptar);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Realizar la transacción de fragmentos para volver al fragment anterior
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null) {
                    FragmentManager fragmentManager = activity.getSupportFragmentManager();
                    fragmentManager.popBackStack(); // Esto eliminará el fragment actual de la pila
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        nMap = googleMap;
        this.nMap.setOnMapClickListener(this);
        this.nMap.setOnMapLongClickListener(this);
        double latitude = 19.882672, longitude = -97.405307;

        this.setLat(latitude);
        this.setLon(longitude);

        //sendDataToSecondFragment(latitude, longitude);

        LatLng tez = new LatLng(latitude, longitude);
        nMap.addMarker(new MarkerOptions().position(tez).title("Punto de localización"));
        nMap.moveCamera(CameraUpdateFactory.newLatLng(tez));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        nMap.clear();
        LatLng tez = new LatLng(latLng.latitude, latLng.longitude);
        this.setLat(latLng.latitude);
        this.setLon(latLng.longitude);
        //sendDataToSecondFragment(latLng.latitude, latLng.longitude);
        nMap.addMarker(new MarkerOptions().position(tez).title("Punto de localización"));
        nMap.moveCamera(CameraUpdateFactory.newLatLng(tez));

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {

        nMap.clear();
        LatLng tez = new LatLng(latLng.latitude, latLng.longitude);
        this.setLat(latLng.latitude);
        this.setLon(latLng.longitude);
        //sendDataToSecondFragment(latLng.latitude, latLng.longitude);
        nMap.addMarker(new MarkerOptions().position(tez).title("Punto de localización"));
        nMap.moveCamera(CameraUpdateFactory.newLatLng(tez));
    }


    public double getLon() {
        return lon;
    }

    public void setLon(double longitud) {
        this.lon = longitud;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    // Cuando necesitas pasar datos al segundo fragmento
    /*private void sendDataToSecondFragment(double latitude, double longitude) {
        addAvistament secondFragment = addAvistament.newInstance(latitude, longitude);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container, secondFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }*/
}