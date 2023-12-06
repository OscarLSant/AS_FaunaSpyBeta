package com.example.as_faunaspyv3;

import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.util.Log;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeDetailsFragment extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    addGetImage gi;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView tvSpecieName, dt_author, dt_date, dt_time;
    TextView specie_name, specie_gender;
    CircleImageView specie_img;
    CircleImageView author_img;
    ImageView  dt_img;
    private String key;
    GoogleMap mMap;

    public HomeDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeDetailsFragment newInstance(String param1, String param2) {
        HomeDetailsFragment fragment = new HomeDetailsFragment();
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
        View view =  inflater.inflate(R.layout.fragment_home_details, container, false);

        tvSpecieName = (TextView) view.findViewById(R.id.details_nombre);
        dt_img = (ImageView) view.findViewById(R.id.details_img);
        author_img = (CircleImageView) view.findViewById(R.id.details_imgauthor);
        dt_date = (TextView) view.findViewById(R.id.details_date);
        dt_time = (TextView) view.findViewById(R.id.details_time);
        dt_author = (TextView) view.findViewById(R.id.details_nameauthor);
        //card specie
        specie_name = (TextView) view.findViewById(R.id.specie_nametext);
        specie_gender = (TextView) view.findViewById(R.id.specie_gender);
        specie_img = (CircleImageView) view.findViewById(R.id.specie_img1);

        // Obtén los datos que deseas editar desde el Bundle o mediante una interfaz, etc.
        Bundle bundle = getArguments();
        if (bundle != null) {
            String specie = bundle.getString("specie", "");
            String location = bundle.getString("location", "");
            String date = bundle.getString("date", "");
            String time = bundle.getString("time", "");
            String img = bundle.getString("img", "");
            String authorId = bundle.getString("author", "");
            String specieId = bundle.getString("specieId", "");
            //position = bundle.getInt("position", -1);
            key = bundle.getString("key", "");



            DatabaseReference specieRef = FirebaseDatabase.getInstance().getReference().child("species").child("" + specieId);
            specieRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String gender = snapshot.child("gender").getValue(String.class);
                        String img = snapshot.child("img").getValue(String.class);

                        specie_name.setText(name);
                        specie_gender.setText(gender);
                        Glide.with(requireContext()).load(img).placeholder(R.drawable.svg_errorimage).into(specie_img);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child("" + authorId);
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String authorName = snapshot.child("name").getValue(String.class);
                        String authorApe = snapshot.child("apellidos").getValue(String.class);
                        String authorProfile = snapshot.child("profileImageUrl").getValue(String.class);

                        dt_author.setText(authorName + " " + authorApe);
                        Glide.with(requireContext()).load(authorProfile).placeholder(R.drawable.svg_errorimage).into(author_img);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            tvSpecieName.setText(specie);
            dt_date.setText(date);
            dt_time.setText(time);
            Glide.with(requireContext()).load(img).placeholder(R.drawable.svg_errorimage).into(dt_img);

        }

        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        double latitude = 19.882672, longitude = -97.405307;
        LatLng tez = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(tez).title("Punto de localización"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(tez));
    }


}