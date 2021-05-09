package dk.au.mad21spring.animalbank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import dk.au.mad21spring.animalbank.AnimalFireStoreModel;
import dk.au.mad21spring.animalbank.IAnimalListActionListener;


public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder>{


    private ArrayList<AnimalFireStoreModel> animals;
    private IAnimalListActionListener listener;

    public AnimalAdapter(ArrayList<AnimalFireStoreModel> animalList, IAnimalListActionListener listener){
            animals=animalList;
            this.listener=listener;
    }

    public void UpdateList(ArrayList<AnimalFireStoreModel> updateAnimals){
        animals=updateAnimals;
    }

    @Override
    public AnimalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.animal_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.animalNameText.setText(animals.get(position).getName());
        holder.spottedDateText.setText(animals.get(position).getDate().toDate().toString());
        Picasso.get()
                .load(animals.get(position).getImageURI())
                .into(holder.animalImage);
    }

    @Override
    public int getItemCount() {
        if(animals==null){
            return 0;
        }
        return animals.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView animalNameText;
        public TextView spottedDateText;
        public ImageView animalImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            animalNameText = itemView.findViewById(R.id.animalNameText);
            spottedDateText = itemView.findViewById(R.id.spottedDateText);
            animalImage = itemView.findViewById(R.id.animalImage);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int position = getBindingAdapterPosition();
            if(position!= RecyclerView.NO_POSITION && listener!=null){
                AnimalFireStoreModel animalClicked = animals.get(position);
                listener.onAnimalPressed(animalClicked);
            }
        }
    }

}
