package com.example.as_faunaspyv3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextInputEditText signupNombre, signupApellidos, signupEmail, signupPassword;
    private Button signupButton;
    private TextView loginRedirectText;
    private Button btnSelectImage;
    private CircleImageView imageView;
    private static final int PICK_IMAGE_REQUEST = 1;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        signupNombre = findViewById(R.id.signup_name);
        signupApellidos = findViewById(R.id.signup_apellidos);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.sigup_btnsignup);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        imageView = findViewById(R.id.imageView);



        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String name = signupNombre.getText().toString().trim();
                String ape = signupApellidos.getText().toString().trim();

                if(name.isEmpty()){
                    signupNombre.setError("Por favor proporciona tu nombre");
                    signupNombre.requestFocus();
                }
                if(ape.isEmpty()){
                    signupApellidos.setError("Por favor proporciona tus apellidos");
                    signupApellidos.requestFocus();
                }
                if(user.isEmpty()){
                    signupEmail.setError("Por favor proporciona un correo electrónico");
                    signupEmail.requestFocus();
                }
                if(pass.isEmpty()){
                    signupPassword.setError("Por favor escribe una contraseña");
                    signupPassword.requestFocus();
                } else {

                    //registrarUsuario(name, ape, user, pass);
                    auth.createUserWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                // Obtener el ID único del usuario creado
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                // Almacenar la información adicional en la base de datos
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);

                                Map<String, Object> userData = new HashMap<>();
                                userData.put("email", user);
                                userData.put("name", name);
                                userData.put("apellidos", ape);

                                databaseReference.setValue(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // método para subir la imagen
                                        uploadProfileImage(userId);
                                        
                                        Snackbar.make(SignUpActivity.this.getCurrentFocus(), "Registro exitoso", Snackbar.LENGTH_SHORT).show();
                                        //startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(SignUpActivity.this.getCurrentFocus(), "Error, " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    }
                                });

                            } else {
                                Snackbar.make(SignUpActivity.this.getCurrentFocus(), "Error, " + task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
            }
        });

    }

    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfileImage(String userId) {
        // Obtener la referencia del almacenamiento en Firebase
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_images/" + userId);

        // Obtener la URI de la imagen de perfil (suponiendo que tienes una variable 'profileImageUri' que contiene la URI)
        // Si estás utilizando la cámara o la galería, deberás manejar la obtención de la URI según tu implementación.

        // Subir la imagen al almacenamiento en Firebase
        storageReference.putFile(uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Obtener la URL de descarga de la imagen
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Almacenar la URL de la imagen de perfil en Firebase Realtime Database
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
                                databaseReference.child("profileImageUrl").setValue(downloadUri.toString());

                                // Informar al usuario sobre el registro exitoso
                                Snackbar.make(SignUpActivity.this.getCurrentFocus(), "Registro exitoso", Snackbar.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(SignUpActivity.this.getCurrentFocus(), "Error al subir la imagen, " + e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }
}

