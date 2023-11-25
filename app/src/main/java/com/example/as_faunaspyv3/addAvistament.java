package com.example.as_faunaspyv3;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.loader.content.CursorLoader;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link addAvistament#newInstance} factory method to
 * create an instance of this fragment.
 */
public class addAvistament extends Fragment {



    private TextInputEditText etDate, etTime, specie, et_location;
    private Button btnDate, btnTime, selectImage, save;

    // selección de imagen
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;


    //para hacer la subida a firebase
    private Uri selectedImageUri;
    private ProgressBar progressBar;
    private String downloadUrl;

    addGetImage gi;

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

        specie = (TextInputEditText) view.findViewById(R.id.edt_specie);
        etDate = (TextInputEditText) view.findViewById(R.id.edt_date);
        etTime = (TextInputEditText) view.findViewById(R.id.edt_time);
        et_location = (TextInputEditText)   view.findViewById(R.id.edt_location);

        btnDate = (Button) view.findViewById(R.id.btndate);
        btnTime = (Button) view.findViewById(R.id.btntime);
        save = (Button) view.findViewById(R.id.btnAdd);

        selectImage = (Button) view.findViewById(R.id.btnSelectImage);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        gi = new addGetImage();

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGalery();
            }
        });

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
                if (TextUtils.isEmpty(specie.getText().toString()) || TextUtils.isEmpty(etTime.getText().toString()) || TextUtils.isEmpty(etDate.getText().toString())) {
                    Snackbar.make(requireView(), "Asegurese de llenar todos los campos", Snackbar.LENGTH_SHORT).show();

                } else
                    uploadImage();
            }
        });

        return view;
    }

    private void openGalery(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Obtener la ruta de la imagen directamente desde el Uri
            String imagePath = getRealPathFromURI(selectedImageUri);

            // Obtener y mostrar los metadatos de la imagen
            displayImageMetadata(imagePath);
            imageView.setImageURI(selectedImageUri);
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(requireContext(), contentUri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();  // Cerrar el cursor

        return result;
    }

    private void displayImageMetadata(String imagePath) {
        try {
            android.media.ExifInterface exifInterface = new android.media.ExifInterface(imagePath);

            // Obtener la fecha y hora de la imagen
            String dateTime = exifInterface.getAttribute(android.media.ExifInterface.TAG_DATETIME);
            if (dateTime != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault());
                try {
                    Date date = dateFormat.parse(dateTime);

                    // Mostrar la fecha y hora en los EditText correspondientes
                    SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    SimpleDateFormat displayTimeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                    etDate.setText(displayDateFormat.format(date));
                    etTime.setText(displayTimeFormat.format(date));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ImageMetadata", "DateTime is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void uploadImage(){
        if(selectedImageUri != null){
            StorageReference sr = FirebaseStorage.getInstance().getReference().child("images/" + System.currentTimeMillis() + ".jpg");

            sr.putFile(selectedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);

                            Snackbar.make(requireView(), "Imagen cargada exitosamente", Snackbar.LENGTH_SHORT).show();

                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    //después de hacer la subida a Storage se obtiene la url de la imagen y se ejecuta el método de inserción de datos
                                    String url = uri.toString();
                                    gi.setUrlImage(url);
                                    insertData();
                                }
                            });
                        }
                    })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(requireView(), "Error al cargar la imagen", Snackbar.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressBar.setProgress((int)progress);
                }
            });

        } else {
            Snackbar.make(requireView(), "Aún no ha seleccionado una fotografía", Snackbar.LENGTH_SHORT).show();

        }
    }


    private void insertData(){
        Map<String, Object> map = new HashMap<>();
        map.put("img", gi.getUrlImage());
        map.put("specie", specie.getText().toString());
        map.put("date", etDate.getText().toString());
        map.put("time", etTime.getText().toString());
        map.put("location", et_location.getText().toString());
        map.put("description", "description chida");

        FirebaseDatabase.getInstance().getReference().child("avistments").push()
                .setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Snackbar.make(requireView(), "Registro exitoso", Snackbar.LENGTH_SHORT).show();

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
                        Snackbar.make(requireView(), "Error al registrar la información", Snackbar.LENGTH_SHORT).show();

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