package dk.au.mad21spring.animalbank.ListView;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import dk.au.mad21spring.animalbank.DataAccess.AnimalFireStoreModel;
import dk.au.mad21spring.animalbank.InfoView.InfoActivity;
import dk.au.mad21spring.animalbank.R;
import dk.au.mad21spring.animalbank.viewmodels.AnimalCollectionViewModel;
import dk.au.mad21spring.animalbank.viewmodels.AnimalCollectionViewModelFactory;

import static dk.au.mad21spring.animalbank.Constants.ANIMAL_REF_INTENT_EXTRA;


public class ListFragment extends Fragment implements IAnimalListActionListener{

    private RecyclerView recyclerView;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView.LayoutManager layoutManager;
    private AnimalCollectionViewModel viewModel;
    private AnimalAdapter adapter;
    private View view;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        AnimalCollectionViewModelFactory vmFactory = new AnimalCollectionViewModelFactory(getActivity().getApplication());
        this.viewModel = new ViewModelProvider(this, vmFactory).get(AnimalCollectionViewModel.class);
        viewModel.getAnimals().observe(this, this::refreshUI);
        return view;
    }

    private void refreshUI(ArrayList<AnimalFireStoreModel> animals){
        if(animals.size() > 0){
            if(adapter == null){
                adapter= new AnimalAdapter(animals,ListFragment.this);
                recyclerView.setAdapter(adapter);
            } else{
                adapter.UpdateList(animals);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onAnimalPressed(AnimalFireStoreModel animal) {
        Intent intent = new Intent(getActivity(), InfoActivity.class);
        intent.putExtra(ANIMAL_REF_INTENT_EXTRA, animal.documentReference.getPath()); //pass animal path to info activity
        startActivity(intent);
    }
}