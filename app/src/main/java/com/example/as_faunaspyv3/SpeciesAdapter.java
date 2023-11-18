package com.example.as_faunaspyv3;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class SpeciesAdapter extends FirebaseRecyclerAdapter<SpeciesModel, SpeciesAdapter.myViewHolder>{

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public SpeciesAdapter(@NonNull FirebaseRecyclerOptions<SpeciesModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull myViewHolder holder, int position, @NonNull SpeciesModel model) {
        holder.name.setText(model.getName());
        holder.gender.setText(model.getGender());

        Glide.with(holder.img.getContext())
                .load(model.getImg())
                .placeholder(R.drawable.svg_erorrimage)
                .circleCrop()
                .error(R.drawable.svg_erorrimage)
                .into(holder.img);

        holder.btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*TextView name = v.findViewById(R.id.details_name);
                TextView nameCard = v.findViewById(R.id.details_nametext);
                TextView gender = v.findViewById(R.id.details_gendertext);
                TextView description = v.findViewById(R.id.details_desctiption);
                ImageView imgHeader = v.findViewById(R.id.details_imageheader);
                ImageView imageCard = v.findViewById(R.id.details_img1);*/

                LayoutInflater inflater = (LayoutInflater) holder.img.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View detailsView = inflater.inflate(R.layout.species_details, null);

                TextView name = detailsView.findViewById(R.id.details_name);
                TextView nameCard = detailsView.findViewById(R.id.details_nametext);
                TextView gender = detailsView.findViewById(R.id.details_gendertext);
                TextView description = detailsView.findViewById(R.id.details_desctiption);
                ImageView imgHeader = detailsView.findViewById(R.id.details_imageheader);
                ImageView imageCard = detailsView.findViewById(R.id.details_img1);


                name.setText(model.getName());
                nameCard.setText(model.getName());
                gender.setText(model.getName());
                description.setText(model.getDescription());

                Glide.with(imgHeader.getContext())
                        .load(model.getImg())
                        .placeholder(R.drawable.svg_erorrimage)
                        .circleCrop()
                        .error(R.drawable.svg_erorrimage)
                        .into(imgHeader);

                Glide.with(imageCard.getContext())
                        .load(model.getImg())
                        .placeholder(R.drawable.svg_erorrimage)
                        .circleCrop()
                        .error(R.drawable.svg_erorrimage)
                        .into(imageCard);

                ViewGroup container = (ViewGroup) holder.btnDetails.getParent();
                container.removeAllViews();
                container.addView(detailsView);
            }
        });
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.species_cards2,parent,false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder{
        CircleImageView img;
        TextView name, gender;
        Button btnDetails;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            img = (CircleImageView) itemView.findViewById(R.id.details_img1);
            name = (TextView) itemView.findViewById(R.id.details_nametext);
            gender = (TextView) itemView.findViewById(R.id.details_gendertext);
            btnDetails = (Button) itemView.findViewById(R.id.btndetails);
        }
    }

}
