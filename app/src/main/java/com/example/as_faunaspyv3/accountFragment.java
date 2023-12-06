package com.example.as_faunaspyv3;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link accountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class accountFragment extends Fragment {

    // PARA TOOLTIP
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String TOOLTIP_DISPLAYED_KEY = "tooltipDisplayed";

    Button configuracion;
    private Handler handler = new Handler();

    TextView nombreUsuario;
    CircleImageView fotoPerfilUsuario;
    RecyclerView recyclerView;
    AccountAdapter accountAdapter;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public accountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment accountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static accountFragment newInstance(String param1, String param2) {
        accountFragment fragment = new accountFragment();
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
        View view =  inflater.inflate(R.layout.fragment_account, container, false);

        configuracion = (Button) view.findViewById(R.id.user_logout);
        // Cargar la animación
        startVibrantAnimation();
        if (!isTooltipAlreadyDisplayed()) {
            // Muestra el mensaje flotante al abrir el fragment
            showTooltip();

            // Marca el indicador para que no se muestre de nuevo
            markTooltipAsDisplayed();
        }
        
        nombreUsuario = (TextView) view.findViewById(R.id.user_nombre);
        fotoPerfilUsuario = (CircleImageView) view.findViewById(R.id.user_profileimage);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_user);



        recyclerView = (RecyclerView)   view.findViewById(R.id.rv_user);
        int snapCount = 2;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), snapCount);
        recyclerView.setLayoutManager(gridLayoutManager);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("profile_images/" + userId);

        FirebaseRecyclerOptions<AccountModel> options =
                new FirebaseRecyclerOptions.Builder<AccountModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("avistments").orderByChild("author").equalTo(userId), AccountModel.class)
                        .build();
        accountAdapter = new AccountAdapter(options);
        recyclerView.setAdapter(accountAdapter);



        //obtener la información del usuario logueado (nombre, apellidos y tal vez correo)
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    String userApellidos = dataSnapshot.child("apellidos").getValue(String.class);
                    String userEmail = dataSnapshot.child("email").getValue(String.class);
                    String img = dataSnapshot.child("profileImageUrl").getValue(String.class);

                    nombreUsuario.setText(userName);
                    if (isAdded() && getActivity() != null) {
                        Glide.with(getActivity())
                                .load(img)
                                .placeholder(R.drawable.svg_errorimage)
                                .into(fotoPerfilUsuario);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // obtener la url de la imagen e insertarla:
        /*storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUri) {
                // Aquí puedes usar la URL de descarga para cargar la imagen, ya sea con una biblioteca de imágenes o como desees
                String imageUrl = downloadUri.toString();
                //Log.d("UserProfile", "Profile Image URL: " + imageUrl);

                // Puedes usar la URL de descarga para cargar la imagen con Glide, Picasso u otras bibliotecas de imágenes
                // Por ejemplo, con Glide:
                // Glide.with(YourActivity.this).load(imageUrl).into(yourImageView);
                if (isAdded() && getActivity() != null) {
                    Glide.with(getActivity())
                            .load(imageUrl)
                            .placeholder(R.drawable.svg_errorimage)
                            .into(fotoPerfilUsuario);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar errores si la imagen no puede ser descargada
                //Log.e("UserProfile", "Error downloading profile image: " + e.getMessage());
            }
        });*/

        configuracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un AlertDialog para confirmar el cierre de sesión
                /*AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
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
            }*/

                Navigation.findNavController(view).navigate(R.id.action_accountFragment_to_accountConfiguration);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        accountAdapter.startListening();
    }

    private void startVibrantAnimation() {
        // Cargar la animación de rotación hacia la derecha y retorno
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(configuracion, "rotation", 0f, 90f);
        rotationAnimator.setDuration(1000); // 1 segundo de rotación
        rotationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        rotationAnimator.addListener(new ObjectAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Establecer un pequeño lapso de tiempo antes de iniciar la próxima animación
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Iniciar la próxima animación de rotación en sentido contrario
                        startReverseAnimation();
                    }
                }, 1000); // Descanso de 0.5 segundos antes de iniciar la próxima animación
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        // Iniciar la animación de rotación hacia la derecha
        rotationAnimator.start();
    }


    private void startReverseAnimation() {
        // Cargar la animación de rotación en sentido contrario
        ObjectAnimator reverseAnimator = ObjectAnimator.ofFloat(configuracion, "rotation", 90f, 0f);
        reverseAnimator.setDuration(1000); // 1 segundo de rotación en sentido contrario
        reverseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        reverseAnimator.addListener(new ObjectAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Establecer un pequeño lapso de tiempo antes de iniciar la próxima animación
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Iniciar la próxima animación de rotación hacia la derecha
                        startVibrantAnimation();
                    }
                }, 1000); // Descanso de 0.5 segundos antes de iniciar la próxima animación
            }

            @Override
            public void onAnimationCancel(Animator animator) {
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        // Iniciar la animación de rotación en sentido contrario
        reverseAnimator.start();
    }

    private void showTooltip() {
        // Puedes personalizar el mensaje y la posición según tus necesidades
        String tooltipText = "Este espacio te permite editar tu información personal y cerrar sesión si lo deseas.";

        // Configura el objetivo del tooltip para tu botón
        TapTargetView.showFor(requireActivity(),
                TapTarget.forView(configuracion, tooltipText)
                        .cancelable(true)
                        .tintTarget(false)
                        .outerCircleColor(R.color.seed) // Personaliza los colores según tus necesidades
        );
    }

    private boolean isTooltipAlreadyDisplayed() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(TOOLTIP_DISPLAYED_KEY, false);
    }

    private void markTooltipAsDisplayed() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(TOOLTIP_DISPLAYED_KEY, true);
        editor.apply();
    }

}