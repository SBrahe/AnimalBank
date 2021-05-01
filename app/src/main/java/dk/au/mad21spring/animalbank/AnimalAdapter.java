package dk.au.mad21spring.animalbank;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;
import java.util.Calendar;

public class AnimalAdapter extends RecyclerView.Adapter<AnimalAdapter.ViewHolder>{


    private ArrayList<AnimalFireStoreModel> animals;
    private IAnimalListActionListener listener;

    public AnimalAdapter(ArrayList<AnimalFireStoreModel> animalList,IAnimalListActionListener listener){
            animals=animalList;
            this.listener=listener;
    }

    public void UpdateList(ArrayList<AnimalFireStoreModel> updateAnimals){
        animals=updateAnimals;
    }

    @Override
    public AnimalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.animalNameText.setText(animals.get(position).getName());
        holder.spottedDateText.setText(animals.get(position).getDate().toString());
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            animalNameText = itemView.findViewById(R.id.animalNameText);
            spottedDateText = itemView.findViewById(R.id.spottedDateText);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            int position = getBindingAdapterPosition();
            if(position!= RecyclerView.NO_POSITION){
                AnimalFireStoreModel animalClicked = animals.get(position);
                listener.onAnimalPressed(animalClicked);
            }
        }
    }

}
