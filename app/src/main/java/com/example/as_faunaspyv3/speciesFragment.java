package com.example.as_faunaspyv3;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link speciesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class speciesFragment extends Fragment {

    RecyclerView recyclerView;
    SpeciesAdapter speciesAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public speciesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment speciesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static speciesFragment newInstance(String param1, String param2) {
        speciesFragment fragment = new speciesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        View view =  inflater.inflate(R.layout.fragment_species, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        FirebaseRecyclerOptions<SpeciesModel> options =
                new FirebaseRecyclerOptions.Builder<SpeciesModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("species"), SpeciesModel.class)
                        .build();

        speciesAdapter = new SpeciesAdapter(options);
        recyclerView.setAdapter(speciesAdapter);
        //speciesAdapter.startListening();
        return view;

        //return inflater.inflate(R.layout.fragment_species, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        speciesAdapter.startListening();
    }
}