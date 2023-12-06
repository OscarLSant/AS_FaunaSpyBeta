package com.example.as_faunaspyv3;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountConfiguration#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountConfiguration extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    addGetImage gi;
    ProgressBar progressBar;
    private String key, img;





    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button btn_update, btn_logout, btn_changeProfile;
    TextView userNameHeader, userName, userApellidos, userEmail;
    CircleImageView userProfile;

    public AccountConfiguration() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountConfiguration.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountConfiguration newInstance(String param1, String param2) {
        AccountConfiguration fragment = new AccountConfiguration();
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
        View view = inflater.inflate(R.layout.fragment_account_configuration, container, false);
        gi = new addGetImage();

        progressBar = view.findViewById(R.id.progressBar);
        btn_logout = view.findViewById(R.id.btnuser_cerrarsesion);
        btn_update = view.findViewById(R.id.btnuser_update);
        btn_changeProfile = view.findViewById(R.id.btnSelectImage);
        userNameHeader = view.findViewById(R.id.user_nameheader);
        userName = view.findViewById(R.id.user_name);
        userApellidos = view.findViewById(R.id.user_apellidos);
        userEmail = view.findViewById(R.id.user_email);
        userProfile = view.findViewById(R.id.user_profile);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un AlertDialog para confirmar el cierre de sesión
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Cerrar Sesión");
                builder.setMessage("¿Estás seguro de que deseas cerrar sesión?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // cerrar sesión
                        FirebaseAuth.getInstance().signOut();

                        // Redirigir a la actividad de inicio de sesión
                        Intent intent = new Intent(requireContext(), LogInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Limpiar la pila de actividades
                        startActivity(intent);
                        Activity activity = getActivity();
                        activity.finish();
                        // Finalizar la actividad actual para evitar que el usuario vuelva atrás con el botón de retroceso
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancelar el cierre de sesión
                        dialog.dismiss();
                    }
                });

                // Mostrar el AlertDialog
                builder.show();
            }
        });


        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Referencia a la base de datos de Firebase
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

            // Realizar la consulta utilizando el userId
            databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Verificar si el nodo con el userId existe en la base de datos
                    if (dataSnapshot.exists()) {
                        // El nodo existe, puedes obtener los datos del usuario
                        // Ejemplo: Obtener el nombre del usuario
                        String nombreUsuario = dataSnapshot.child("name").getValue(String.class);
                        String apellidosUsuario = dataSnapshot.child("apellidos").getValue(String.class);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        img = dataSnapshot.child("profileImageUrl").getValue(String.class);

                        userNameHeader.setText(nombreUsuario + " " + apellidosUsuario);
                        userName.setText(nombreUsuario);
                        userApellidos.setText(apellidosUsuario);
                        userEmail.setText(email);
                        Glide.with(requireContext()).load(img).placeholder(R.drawable.svg_errorimage).into(userProfile);

                    } else {
                        // El nodo no existe, maneja la situación según tus necesidades
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Manejar errores en la consulta si es necesario
                }
            });
        }

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        btn_changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(userName.getText().toString()) || TextUtils.isEmpty(userApellidos.getText().toString())) {
                    Snackbar.make(requireView(), "Asegurese de llenar todos los campos", Snackbar.LENGTH_SHORT).show();

                } else if (selectedImageUri != null) {
                    uploadImage();
                } else updateData();
            }
        });

        return view;
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
            userProfile.setImageURI(selectedImageUri);
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


    // ACTUALIZAR DATOS
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
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = currentUser.getUid();
        if (selectedImageUri != null) {
            StorageReference sr = FirebaseStorage.getInstance().getReference().child("profile_images/" + userId + ".jpg");

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
        map.put("name", userName.getText().toString());
        map.put("apellidos", userApellidos.getText().toString());
        map.put("profileImageUrl", gi.getUrlImage());
        // Verificar si hay una nueva URL de imagen
        if (selectedImageUri != null) {
            // Si hay una imagen seleccionada, actualizar la URL de la imagen en el mapa
            map.put("profileImageUrl", gi.getUrlImage());
        } else map.put("profileImageUrl", img);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId = currentUser.getUid();

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(userId).updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Snackbar.make(requireView(), "Datos actualizados exitosamente.", Snackbar.LENGTH_SHORT).show();

                        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                        ft.detach(AccountConfiguration.this).attach(AccountConfiguration.this).commit();
                    }
                });
    }
}