package com.example.as_faunaspyv3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String TOOLTIP_KEY_HOME = "tooltipShownHome";
    RecyclerView recyclerView;
    HomeAdapter homeAdapter;

    FloatingActionButton floatingActionButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public homeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
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
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        // Verificar si el tooltip ya se ha mostrado antes en este fragmento
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean tooltipShown = sharedPreferences.getBoolean(TOOLTIP_KEY_HOME, false);

        if (!tooltipShown) {
            // Mostrar el tooltip por primera vez
            showTooltip(floatingActionButton, "Hola, bienvenido a FaunaSpy. \n\nEn este sitio puedes registrar avistamientos de especies en tu región, así como explorar los registros realizados por otros usuarios.");

            // Actualizar el estado para indicar que el tooltip se ha mostrado
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(TOOLTIP_KEY_HOME, true);
            editor.apply();
        }

        recyclerView = (RecyclerView)   view.findViewById(R.id.rv);

        int snapCount = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), snapCount);
        recyclerView.setLayoutManager(gridLayoutManager);

        FirebaseRecyclerOptions<HomeModel> options =
                new FirebaseRecyclerOptions.Builder<HomeModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("avistments").orderByChild("timestamp"), HomeModel.class)
                        .build();
        homeAdapter = new HomeAdapter(options);
        recyclerView.setAdapter(homeAdapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_addFragment);
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        homeAdapter.startListening();
    }

    private void showTooltip(View view, String message) {
        TapTargetView.showFor(requireActivity(),
                TapTarget.forView(view, message)
                        .cancelable(false)
                        .tintTarget(true));
    }
}