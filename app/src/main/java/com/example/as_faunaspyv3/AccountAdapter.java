package com.example.as_faunaspyv3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AccountAdapter extends FirebaseRecyclerAdapter<AccountModel, AccountAdapter.myViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AccountAdapter(@NonNull FirebaseRecyclerOptions<AccountModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AccountAdapter.myViewHolder holder, final int position, @NonNull AccountModel model) {
        holder.nameSpecie.setText(model.getSpecie());

        Glide.with(holder.img.getContext())
                .load(model.img)
                .placeholder(R.drawable.svg_errorimage)
                .into(holder.img);

        holder.btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                // setear los valores obtenidos de la base de datos a los campos
                bundle.putString("specie", model.getSpecie());
                bundle.putString("location", model.getLocation());
                bundle.putString("date", model.getDate());
                bundle.putString("time", model.getTime());
                bundle.putString("img", model.getImg().toString());
                //bundle.putInt("position", position);
                bundle.putString("key", getRef(position).getKey());

                AccountEditRegister editFragment = new AccountEditRegister();
                editFragment.setArguments(bundle);

                Navigation.findNavController(v).navigate(R.id.action_accountFragment_to_editRegister, bundle);

            }
        });
        holder.btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.nameSpecie.getContext());
                builder.setTitle("¿Está seguro que desea eliminar?");
                builder.setMessage("Los datos eliminados son irreversibles");

                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Oftener la referencia al nodo que se va a eliminar
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("avistments")
                                .child(getRef(position).getKey());

                        // Obtener la URL de la imagen desde el nodo "img"
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    // Obtener la URL de la imagen desde el dataSnapshot
                                    String imageUrl = dataSnapshot.child("img").getValue(String.class);

                                    // Eliminar la entrada de la base de datos
                                    ref.removeValue();

                                    // Eliminar la imagen de Firebase Storage
                                    if (imageUrl != null) {
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                                        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // La imagen se eliminó exitosamente
                                                Snackbar.make(holder.nameSpecie, "Datos e imagen eliminados con éxito", Snackbar.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Manejar el fallo al eliminar la imagen
                                                Snackbar.make(holder.nameSpecie, "Error al eliminar la imagen", Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Manejar errores en la lectura de datos de Firebase Realtime Database
                                //Toast.makeText(holder.nameSpecie.getContext(), "Error al leer datos de Firebase", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(holder.nameSpecie, "Cancelled", Snackbar.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_cards, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView nameSpecie;
        Button btn_editar, btn_eliminar;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.details_img1);
            nameSpecie = (TextView) itemView.findViewById(R.id.details_nametext);

            btn_editar = (Button) itemView.findViewById(R.id.btn_editar);
            btn_eliminar = (Button) itemView.findViewById(R.id.btn_delete);
        }
    }
}
