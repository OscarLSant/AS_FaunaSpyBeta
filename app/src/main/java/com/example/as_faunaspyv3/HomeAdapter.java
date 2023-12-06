package com.example.as_faunaspyv3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class HomeAdapter extends FirebaseRecyclerAdapter<HomeModel, HomeAdapter.myViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HomeAdapter(@NonNull FirebaseRecyclerOptions<HomeModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull HomeModel model) {
        holder.nameSpecie.setText(model.getSpecie());

        Glide.with(holder.img.getContext())
                .load(model.img)
                .placeholder(R.drawable.svg_errorimage)
                .into(holder.img);

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                // setear los valores obtenidos de la base de datos a los campos
                bundle.putString("specie", model.getSpecie());
                bundle.putString("location", model.getLocation());
                bundle.putString("date", model.getDate());
                bundle.putString("time", model.getTime());
                bundle.putString("img", model.getImg().toString());
                bundle.putString("author", model.getAuthor());
                bundle.putString("specieId", model.getSpecieId());
                bundle.putString("key", getRef(position).getKey());


                HomeDetailsFragment homeDetailsFragment = new HomeDetailsFragment();
                homeDetailsFragment.setArguments(bundle);

                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_homeDetailsFragment, bundle);
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_cards, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView nameSpecie;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.home_img);
            nameSpecie = (TextView) itemView.findViewById(R.id.details_nametext);
        }
    }
}
