package com.babylon.alex.cookit.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.babylon.alex.cookit.Adapters.HomeAdapter;
import com.babylon.alex.cookit.R;
import com.babylon.alex.cookit.pojo.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecipesFragment extends Fragment {


    public RecipesFragment() {
        // Required empty public constructor
    }

    private Button myRecipesButton, savedRecipesButton;

    private android.support.v4.app.FragmentTransaction ft;

    private SavedRecipesFragment savedRecipesFragment;
    private MyRecipesFragment myRecipesFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);

        myRecipesButton = view.findViewById(R.id.buttonMyRecipes);
        savedRecipesButton = view.findViewById(R.id.buttonSaved);

        ft = getActivity().getSupportFragmentManager().beginTransaction();

        myRecipesFragment = new MyRecipesFragment();
        savedRecipesFragment = new SavedRecipesFragment();
        ft.replace(R.id.myRecipeContainer, myRecipesFragment).commit();



        myRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedRecipesButton.setTextColor(getResources().getColor(R.color.colorSemiWhite));
                myRecipesButton.setTextColor(getResources().getColor(R.color.colorWhite));
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.myRecipeContainer, myRecipesFragment).commit();

            }
        });

        savedRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedRecipesButton.setTextColor(getResources().getColor(R.color.colorWhite));
                myRecipesButton.setTextColor(getResources().getColor(R.color.colorSemiWhite));
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.myRecipeContainer,savedRecipesFragment).commit();

            }
        });

        return view;
    }

}
