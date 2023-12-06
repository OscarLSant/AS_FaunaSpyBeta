package com.example.as_faunaspyv3;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountEditRegister#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountEditRegister extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_PERMISSIONS = 1;
    private Uri selectedImageUri;
    String img;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextInputEditText edLocation, edDate, edTime;
    private AutoCompleteTextView edSpecie;
    private ImageView imgView;
    private Button btnUpdate, btnDate, btnTime, selectImage, btnlocation;
    private ProgressBar progressBar;
    addGetImage gi;
    private String key;

    private ArrayAdapter<String> adapter;
    private List<String> opciones;

    public AccountEditRegister() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditRegister.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountEditRegister newInstance(String param1, String param2) {
        AccountEditRegister fragment = new AccountEditRegister();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        //args.putInt("position", position);
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
        View view =  inflater.inflate(R.layout.fragment_edit_register, container, false);
        edSpecie = (AutoCompleteTextView) view.findViewById(R.id.edt_specie);

        btnDate = (Button) view.findViewById(R.id.btndate);
        btnTime = (Button) view.findViewById(R.id.btntime);
        btnlocation = (Button)  view.findViewById(R.id.btnlocation);
        selectImage = (Button) view.findViewById(R.id.btnSelectImage);

        edLocation = view.findViewById(R.id.edt_location);
        edDate = view.findViewById(R.id.edt_date);
        edTime = view.findViewById(R.id.edt_time);
        imgView = view.findViewById(R.id.imageView);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        gi = new addGetImage();

        // Obtén los datos que deseas editar desde el Bundle o mediante una interfaz, etc.
        Bundle bundle = getArguments();
        if (bundle != null) {
            String specie = bundle.getString("specie", "");
            String location = bundle.getString("location", "");
            String date = bundle.getString("date", "");
            String time = bundle.getString("time", "");
            img = bundle.getString("img", "");
            gi.setUrlImage(img);
            //position = bundle.getInt("position", -1);
            key = bundle.getString("key", "");


            // Setea los valores obtenidos de la base de datos a los campos
            edSpecie.setText(specie);
            edLocation.setText(location);
            edDate.setText(date);
            edTime.setText(time);

            Glide.with(this)
                    .load(img)
                    .placeholder(R.drawable.svg_errorimage)
                    .into(imgView);
        }

        // MUESTRA LAS OPCIONES DE ESPECIES
        opciones = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        edSpecie.setAdapter(adapter);
        DatabaseReference speciesRef = FirebaseDatabase.getInstance().getReference().child("species");
        speciesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                opciones.clear();
                for(DataSnapshot speciesSnapshot:snapshot.getChildren()){
                    String speciesName = speciesSnapshot.child("name").getValue(String.class);
                    opciones.add(speciesName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error al leer datos", error.toException());
            }
        });

        // BOTONES DE CALENDARIO, HORA, MAPA Y SELECCIÓN DE IMAGEN
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario();
            }
        });
        edDate.setOnTouchListener(new View.OnTouchListener() {
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
                Navigation.findNavController(view).navigate(R.id.action_editRegister_to_addLocationFragment);
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarReloj();
            }
        });
        edTime.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Acciones personalizadas que deseas ejecutar en lugar de abrir el teclado
                mostrarReloj();
                // Devolver true para indicar que el evento táctil ha sido consumido y no debe ser manejado por el sistema
                return true;
            }
        });
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestPermissions();
                //openGallery();
            }
        });


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(edSpecie.getText().toString()) || TextUtils.isEmpty(edTime.getText().toString()) || TextUtils.isEmpty(edDate.getText().toString()) || TextUtils.isEmpty(edLocation.getText().toString())) {
                    Snackbar.make(requireView(), "Asegurese de llenar todos los campos", Snackbar.LENGTH_SHORT).show();

                } else if (selectedImageUri != null) {
                    uploadImage();
                } else updateData();
            }
        });

        return view;
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
                edDate.setText(formattedDate);
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
                edTime.setText(formattedTime);
            }
        }, hourOfDay, minute, true);
        t.show();
    }

    // ACTUALIZAR LA IMAGEN:
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
            openGallery();
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
                Snackbar.make(requireView(), "Permiso denegado para acceder al almacenamiento.", Snackbar.LENGTH_SHORT).show();
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
            imgView.setImageURI(selectedImageUri);
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
                    edDate.setText(displayDateFormat.format(date));
                    edTime.setText(displayTimeFormat.format(date));

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
                        edLocation.setText(lat + ", " + longitude);
                    } else {
                        edLocation.setText("0.0, 0.0");
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

    private void uploadImage() {
        // Obtener la URL de la imagen existente antes de subir la nueva imagen
        String existingImageUrl = gi.getUrlImage();

        // Eliminar la imagen existente en Firebase Storage si hay una URL existente
        if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
            StorageReference existingImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(existingImageUrl);
            existingImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // La imagen existente se eliminó con éxito, ahora puedes subir la nueva imagen
                    uploadNewImage();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Manejar el error de eliminación de la imagen existente (puedes elegir mostrar un mensaje o simplemente continuar con la carga de la nueva imagen)
                    Snackbar.make(requireView(), "Error al eliminar la imagen existente", Snackbar.LENGTH_SHORT).show();
                }
            });
        } else {
            // No hay URL de imagen existente, simplemente sube la nueva imagen
            uploadNewImage();
        }
    }

    // Método para subir la nueva imagen
    private void uploadNewImage() {
        if (selectedImageUri != null) {
            StorageReference sr = FirebaseStorage.getInstance().getReference().child("images/" + System.currentTimeMillis() + ".jpg");

            sr.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 500);

                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Después de hacer la subida a Storage se obtiene la URL de la imagen y se ejecuta el método de inserción de datos
                            String url = uri.toString();
                            gi.setUrlImage(url);
                            updateData();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(requireView(), "Error al cargar la imagen", Snackbar.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });

        } else {
            Snackbar.make(requireView(), "Aún no ha seleccionado una fotografía", Snackbar.LENGTH_SHORT).show();
        }
    }

// Resto de tu código...

    // ACTUALIZAR LOS DATOS
    private void updateData() {
        Map<String, Object> map = new HashMap<>();
        map.put("specie", edSpecie.getText().toString());
        map.put("location", edLocation.getText().toString());
        map.put("date", edDate.getText().toString());
        map.put("time", edTime.getText().toString());
        if (selectedImageUri != null) {
            // Si hay una imagen seleccionada, actualizar la URL de la imagen en el mapa
            map.put("img", gi.getUrlImage());
        } else map.put("profileImageUrl", img);

        FirebaseDatabase.getInstance().getReference().child("avistments")
                .child(key).updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Snackbar.make(requireView(), "Datos actualizados exitosamente.", Snackbar.LENGTH_SHORT).show();

                        AppCompatActivity activity = (AppCompatActivity) getActivity();

                        if (activity != null) {
                            FragmentManager fragmentManager = activity.getSupportFragmentManager();
                            fragmentManager.popBackStack();
                        }
                    }
                });
    }
}