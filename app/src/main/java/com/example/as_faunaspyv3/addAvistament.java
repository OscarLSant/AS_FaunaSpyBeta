package com.example.as_faunaspyv3;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link addAvistament#newInstance} factory method to
 * create an instance of this fragment.
 */
public class addAvistament extends Fragment {

    EditText etDate, etTime, img, specie;
    Button btnDate, btnTime, save;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public addAvistament() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment addFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static addAvistament newInstance(String param1, String param2) {
        addAvistament fragment = new addAvistament();
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
        View view =  inflater.inflate(R.layout.fragment_add, container, false);

        img = (EditText) view.findViewById(R.id.edt_img);
        specie = (EditText) view.findViewById(R.id.edt_specie);
        etDate = (EditText) view.findViewById(R.id.edt_date);
        etTime = (EditText) view.findViewById(R.id.edt_time);

        btnDate = (Button) view.findViewById(R.id.btndate);
        btnTime = (Button) view.findViewById(R.id.btntime);
        save = (Button) view.findViewById(R.id.btnAdd);


        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarReloj();
            }
        });

        // tiene que guardar la información en la base de datos y después regresar al homeFragment.
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(img.getText().toString()) || TextUtils.isEmpty(specie.getText().toString()) || TextUtils.isEmpty(etTime.getText().toString()) || TextUtils.isEmpty(etDate.getText().toString())) {
                    Toast.makeText(requireContext(), "Debe llenar todos los campos", Toast.LENGTH_SHORT).show();
                } else insertData();
            }
        });

        return view;
    }

    private void insertData(){
        Map<String, Object> map = new HashMap<>();
        map.put("img", img.getText().toString());
        map.put("specie", specie.getText().toString());
        map.put("date", etDate.getText().toString());
        map.put("time", etTime.getText().toString());
        map.put("locatoin", "location");
        map.put("description", "description chida");

        FirebaseDatabase.getInstance().getReference().child("avistments").push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(requireContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                        // aquí quiero que se cierre el fragment actual y se retorne al anterior

                        // Obtener la actividad que contiene los fragments
                        AppCompatActivity activity = (AppCompatActivity) getActivity();

                        // Realizar la transacción de fragmentos para volver al fragment anterior
                        if (activity != null) {
                            FragmentManager fragmentManager = activity.getSupportFragmentManager();
                            fragmentManager.popBackStack(); // Esto eliminará el fragment actual de la pila
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(requireContext(), "Error al registrar", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarCalendario() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog d = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                etDate.setText(formattedDate);
            }
        }, year, month, dayOfMonth);
        d.show();
    }

    private void mostrarReloj(){

        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog t = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                etTime.setText(formattedTime);
            }
        }, hourOfDay, minute, true);
        t.show();
    }
}