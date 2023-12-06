package com.example.as_faunaspyv3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link speciesDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class speciesDetailsFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;
    TextView detailsName, detailsGender, detailsDescription;
    ImageView detailsImg;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public speciesDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment speciesDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static speciesDetailsFragment newInstance(String param1, String param2) {
        speciesDetailsFragment fragment = new speciesDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_species_details, container, false);
        detailsName = view.findViewById(R.id.details_name);
        detailsGender = view.findViewById(R.id.details_gendertext);
        TextView detailsNameCard = view.findViewById(R.id.details_nametext);
        detailsImg = view.findViewById(R.id.details_img1);
        ImageView detailsImgHeader = view.findViewById(R.id.details_imageheader);
        detailsDescription = view.findViewById(R.id.details_desctiption);

        // Obtén los datos que deseas editar desde el Bundle o mediante una interfaz, etc.
        Bundle bundle = getArguments();
        if (bundle != null) {
            String name = bundle.getString("name", "");
            String gender = bundle.getString("gender", "");
            String description = bundle.getString("description", "");
            String img = bundle.getString("img", "");
            //position = bundle.getInt("position", -1);
            String key = bundle.getString("key", "");

            detailsNameCard.setText(name);
            detailsName.setText(name);
            detailsGender.setText(gender);
            detailsDescription.setText(description);
            Glide.with(requireContext()).load(img).placeholder(R.drawable.svg_errorimage).into(detailsImgHeader);
            Glide.with(requireContext()).load(img).placeholder(R.drawable.svg_errorimage).into(detailsImg);
        }
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng tez = new LatLng(19.882672, -97.405307);
        mMap.addMarker(new MarkerOptions().position(tez).title("Punto de localización 1"));

        LatLng tez1 = new LatLng(19.882434, -97.394167);
        mMap.addMarker(new MarkerOptions().position(tez1).title("Punto de localización 2"));

        LatLng tez2 = new LatLng(19.886647, -97.393011);
        mMap.addMarker(new MarkerOptions().position(tez2).title("Punto de localización 3"));

        LatLng tez3 = new LatLng(19.890372, -97.395515);
        mMap.addMarker(new MarkerOptions().position(tez3).title("Punto de localización 3"));

        LatLng tez4 = new LatLng(19.888194, -97.399943);
        mMap.addMarker(new MarkerOptions().position(tez4).title("Punto de localización 3"));

        // Puedes ajustar la cámara para incluir todas las marcas
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(tez);
        builder.include(tez1);
        builder.include(tez2);
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0)); // El valor '15' es el zoom
    }
}