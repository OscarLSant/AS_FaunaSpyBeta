package com.example.as_faunaspyv3;

import static android.app.Activity.RESULT_OK;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
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
import androidx.navigation.Navigation;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.Manifest;


public class addAvistament extends Fragment {

    //private addGetLocation getLocation;
    //public addGetLocation getLocationClass = new addGetLocation();
    //private AutoCompleteTextView specie;
    private TextInputEditText etDate, etTime, et_location;
    private AutoCompleteTextView specie;
    private ArrayAdapter<String> adapter;
    private List<String> opciones;
    private Button btnDate, btnTime, selectImage, save, btnlocation;

    // selección de imagen
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_PERMISSIONS = 1;
    private ImageView imageView;

    //para hacer la subida a firebase
    private Uri selectedImageUri;
    private ProgressBar progressBar;
    private String downloadUrl, specieKey;
    private double latitude, longitude;

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

    /*public static addAvistament newInstance(double latitude, double longitude) {
        addAvistament fragmentAdvistament = new addAvistament();
        Bundle args = new Bundle();
        args.putDouble("key_latitude", latitude);
        args.putDouble("key_longitude", longitude);
        fragmentAdvistament.setArguments(args);
        return fragmentAdvistament;
    }*/

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

        specie = (AutoCompleteTextView) view.findViewById(R.id.edt_specie);

        opciones = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        specie.setAdapter(adapter);
        DatabaseReference speciesRef = FirebaseDatabase.getInstance().getReference().child("species");
        speciesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                opciones.clear();
                for(DataSnapshot speciesSnapshot:snapshot.getChildren()){
                    setSpecieKey(speciesSnapshot.getKey());
                    String speciesName = speciesSnapshot.child("name").getValue(String.class);
                    opciones.add(speciesName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al leer datos", error.toException());
            }
        });

        etDate = (TextInputEditText) view.findViewById(R.id.edt_date);
        etTime = (TextInputEditText) view.findViewById(R.id.edt_time);
        et_location = (TextInputEditText)   view.findViewById(R.id.edt_location);

        btnDate = (Button) view.findViewById(R.id.btndate);
        btnTime = (Button) view.findViewById(R.id.btntime);
        btnlocation = (Button)  view.findViewById(R.id.btnlocation);
        save = (Button) view.findViewById(R.id.btnAdd);

        selectImage = (Button) view.findViewById(R.id.btnSelectImage);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        gi = new addGetImage();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
                //openGallery();
            }
        });
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
                //openGallery();
            }
        });

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });
        etDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Acciones personalizadas que deseas ejecutar en lugar de abrir el teclado
                mostrarCalendario();

                // Devolver true para indicar que el evento táctil ha sido consumido y no debe ser manejado por el sistema
                return true;
            }
        });

        btnlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_addFragment_to_addLocationFragment);
            }
        });


        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarReloj();
            }
        });
        etTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Acciones personalizadas que deseas ejecutar en lugar de abrir el teclado
                mostrarReloj();

                // Devolver true para indicar que el evento táctil ha sido consumido y no debe ser manejado por el sistema
                return true;
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

        if(getArguments() != null){
            latitude = getArguments().getDouble("key_latitude", 0.0);
            longitude = getArguments().getDouble("key_longitude", 0.0);

            et_location.setText(latitude + ", " + longitude);
        }

        return view;
    }


    /*private void checkAndRequestPermissions() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};

        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            // Si hay permisos que solicitar
            ActivityCompat.requestPermissions(requireActivity(), permissionsToRequest.toArray(new String[0]), REQUEST_PERMISSIONS);
        } else {
            // Si ya tienes permisos, puedes realizar la acción directamente
            // Por ejemplo, abrir la galería
            openGallery();.0
        }
    }*/
    private void checkAndRequestPermissions() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};

        List<String> permissionsToRequest = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            // Convertir la lista de permisos pendientes a un array de strings
            String[] permissionsArray = permissionsToRequest.toArray(new String[0]);

            // Solicitar permisos al usuario
            ActivityCompat.requestPermissions(requireActivity(), permissionsArray, REQUEST_PERMISSIONS);
            //openGallery();
        } else {
            // Si ya tienes permisos, puedes realizar la acción directamente
            // Por ejemplo, abrir la galería
            openGallery();
        }
    }

    // Manejar resultados de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes continuar con el procesamiento de la imagen
                // Por ejemplo, abrir la galería
                openGallery();
            } else {
                // Permiso denegado, muestra un mensaje o toma alguna acción apropiada
                Snackbar.make(requireView(), "Permiso denegado para acceder al almacenamiento", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void openGallery(){
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
            //android.media.ExifInterface exifInterface = new android.media.ExifInterface(imagePath);
            ExifInterface exifInterface = new ExifInterface(imagePath);

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

                    // Obtener la ubicación de los metadatos de la imagen
                    float[] latLong = new float[2];
                    Metadata metadata = ImageMetadataReader.readMetadata(new File(imagePath));

                    // Obtiene el directorio de GPS
                    GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);

                    if (gpsDirectory != null) {
                        // Verifica si hay información de geolocalización
                            // Obtiene latitud y longitud
                            double latitude = gpsDirectory.getGeoLocation().getLatitude();
                            double longitude = gpsDirectory.getGeoLocation().getLongitude();

                            System.out.println("Latitud: " + latitude);
                            System.out.println("Longitud: " + longitude);
                            String lat = String.valueOf(latitude);
                        Log.d("LOCALIZACION", lat);
                            et_location.setText(lat + ", " + longitude);
                        } else {
                        et_location.setText("0.0, 0.0");
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (ImageProcessingException e) {
                    throw new RuntimeException(e);
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
        //obtener el id del autor
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> map = new HashMap<>();
        map.put("author", userId);
        map.put("img", gi.getUrlImage());
        map.put("specie", specie.getText().toString());
        map.put("date", etDate.getText().toString());
        map.put("time", etTime.getText().toString());
        map.put("location", et_location.getText().toString());
        map.put("description", "description chida");
        map.put("specieId", getSpecieKey());
        map.put("timestamp", ServerValue.TIMESTAMP);

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

    public String getSpecieKey() {
        return specieKey;
    }

    public void setSpecieKey(String specieKey) {
        this.specieKey = specieKey;
    }
/*private void processData() {
        double latitude = getArguments().getDouble("key_latitude", 0.0);
        double longitude = getArguments().getDouble("key_longitude", 0.0);
        // Haz algo con latitude y longitude
    }
*/
}