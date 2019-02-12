package com.example.yana.cookit.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.yana.cookit.Adapters.HomeAdapter;
import com.example.yana.cookit.R;
import com.example.yana.cookit.pojo.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecipesFragment extends Fragment {

// фрагмент вибору між своїми та збереженими рецептами


    public RecipesFragment() {
    }

    private Button myRecipesButton, savedRecipesButton;// кнопки вибору між списками рецептів

    private android.support.v4.app.FragmentTransaction ft;// змінює фрагмент всередині в залежності від натиснутої кнопки

    private SavedRecipesFragment savedRecipesFragment;// фрагмент збережених рецептів
    private MyRecipesFragment myRecipesFragment;// фрагмент створених рецептів

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);// знаходимо вид

        myRecipesButton = view.findViewById(R.id.buttonMyRecipes);// знаходимо кнопки на виді
        savedRecipesButton = view.findViewById(R.id.buttonSaved);

        ft = getActivity().getSupportFragmentManager().beginTransaction();// ініціалізуємо класс який переключає фрагменти в контейнері

        myRecipesFragment = new MyRecipesFragment();//іеіціалізація потрібниз фрагментів
        savedRecipesFragment = new SavedRecipesFragment();
        ft.replace(R.id.myRecipeContainer, myRecipesFragment).commit();// сразу включаємо фрагмент з створеними рецептами



        myRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//включає фрагмент зі своїми рецептами
                savedRecipesButton.setTextColor(getResources().getColor(R.color.colorSemiWhite));
                myRecipesButton.setTextColor(getResources().getColor(R.color.colorWhite));
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.myRecipeContainer, myRecipesFragment).commit();

            }
        });

        savedRecipesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//включає фрагмент зі збереженими рецептами
                savedRecipesButton.setTextColor(getResources().getColor(R.color.colorWhite));
                myRecipesButton.setTextColor(getResources().getColor(R.color.colorSemiWhite));
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.myRecipeContainer,savedRecipesFragment).commit();

            }
        });

        return view;
    }

}
