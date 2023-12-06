package com.example.as_faunaspyv3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link speciesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class speciesFragment extends Fragment {

    RecyclerView recyclerView;
    SpeciesAdapter speciesAdapter;
    androidx.appcompat.widget.SearchView searchView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String TOOLTIP_KEY_SPECIES = "tooltipShownSpecies";

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

        searchView = view.findViewById(R.id.search_view1);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean tooltipShown = sharedPreferences.getBoolean(TOOLTIP_KEY_SPECIES, false);

        if (!tooltipShown) {
            // Mostrar el tooltip por primera vez
            showTooltip(searchView, "En este espacio puedes realizar búsquedas específicas de especies, así como examinar los detalles de cada una de ellas.");

            // Actualizar el estado para indicar que el tooltip se ha mostrado
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(TOOLTIP_KEY_SPECIES, true);
            editor.apply();
        }

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        FirebaseRecyclerOptions<SpeciesModel> options =
                new FirebaseRecyclerOptions.Builder<SpeciesModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("species"), SpeciesModel.class)
                        .build();

        speciesAdapter = new SpeciesAdapter(options);
        recyclerView.setAdapter(speciesAdapter);
        //speciesAdapter.startListening();

        // Manejar eventos de búsqueda
        // Configurar el SearchView
        //searchView.setQueryHint("Buscar");
        //searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                txtSearch(query);
                return false;
            }
        });


        return view;
        //return inflater.inflate(R.layout.fragment_species, container, false);
    }

    private void txtSearch(String str) {
        // Tu lógica de búsqueda aquí
        FirebaseRecyclerOptions<SpeciesModel> options =
                new FirebaseRecyclerOptions.Builder<SpeciesModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("species").orderByChild("name").startAt(str).endAt(str+"~"), SpeciesModel.class)
                        .build();

        SpeciesAdapter speciesAdapter1 = new SpeciesAdapter(options);
        speciesAdapter1.startListening();
        recyclerView.setAdapter(speciesAdapter1);
    }

    @Override
    public void onStart() {
        super.onStart();
        speciesAdapter.startListening();
    }

    private void showTooltip(View view, String message) {
        TapTargetView.showFor(requireActivity(),
                TapTarget.forView(view, message)
                        .cancelable(false)
                        .tintTarget(true));
    }
}