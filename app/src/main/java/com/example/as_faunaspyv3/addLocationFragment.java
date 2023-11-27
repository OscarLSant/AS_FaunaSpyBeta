package com.example.as_faunaspyv3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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




    // Método de configuración para el objeto getLocationClass


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
                // Puedes usar getLocationClass aquí según tus necesidades
                // Por ejemplo, mostrar información en la interfaz de usuario
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

                // esto lo hace local
                //addGetLocation obtenerLocalización = new addGetLocation();

                //obtenerLocalización.setLatitude(String.valueOf(aqui.getLat()));
                //obtenerLocalización.setLongitude(String.valueOf(aqui.getLon()));

                AppCompatActivity activity = (AppCompatActivity) getActivity();
                // Realizar la transacción de fragmentos para volver al fragment anterior
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

        LatLng tez = new LatLng(19.882672, -97.405307);
        nMap.addMarker(new MarkerOptions().position(tez).title("Punto de localización"));
        nMap.moveCamera(CameraUpdateFactory.newLatLng(tez));
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {

        nMap.clear();
        LatLng tez = new LatLng(latLng.latitude, latLng.longitude);
        this.setLat(latLng.latitude);
        this.setLon(latLng.longitude);
        nMap.addMarker(new MarkerOptions().position(tez).title("Punto de localización"));
        nMap.moveCamera(CameraUpdateFactory.newLatLng(tez));

        /*
        if (setLocation != null) {
            setLocation.setLatitude("" + latLng.latitude);
            setLocation.setLongitude("" + latLng.longitude);

            if (nMap != null) {
                nMap.clear();
                LatLng tez = new LatLng(latLng.latitude, latLng.longitude);
                nMap.addMarker(new MarkerOptions().position(tez).title("Punto de localización"));
                nMap.moveCamera(CameraUpdateFactory.newLatLng(tez));
            } else {
                Log.d("VALOR NULO", "EL VALOR DE nMap ES NULO");
            }
        } else {
            Log.d("VALOR NULO", "EL VALOR DE getLocationClass ES NULO");
        }*/
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        //setLocation.setLatitude("" + latLng.latitude);
        //setLocation.setLongitude("" + latLng.longitude);

            //me quedé intentando pasar el objeto, me da como resultado nulo, este objeto lo estoy pasando con valor pero aquí ya no me lo acepta
            //primero lo pasé con el bundle pero ahora lo estoy intentando pasar sin ser serializable pero me está sucediendo exactamente lo mismo y no entiendo por qué
            // queda pendiente revisar por qué está sucediendo esto...
        nMap.clear();
        LatLng tez = new LatLng(latLng.latitude, latLng.longitude);
        this.setLat(latLng.latitude);
        this.setLon(latLng.longitude);
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
}