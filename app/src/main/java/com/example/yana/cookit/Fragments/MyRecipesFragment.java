package com.example.yana.cookit.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.yana.cookit.Adapters.HomeAdapter;
import com.example.yana.cookit.R;
import com.example.yana.cookit.pojo.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
// фрагмент показує рецепти поточного користувача
public class MyRecipesFragment extends Fragment {

    ListView listView;// вид списку
    HomeAdapter adapter;// адаптер
    ArrayList<Recipe> arrayList;// список рецептів
    private DatabaseReference postsRef;// посилання на бд
    private FirebaseAuth mAuth;// посилання на користувача
    private View view;// вид

    public MyRecipesFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_recipes, container, false);// знаходимо вид фрагменту

        listView = view.findViewById(R.id.myRecipesListView);// знаходимо вид списку
        listView.setDivider(null);// прибираємо розділювач
        listView.setDividerHeight(0);
        postsRef = FirebaseDatabase.getInstance().getReference().child("Recipes");// ссилка на бд з рецептами
        mAuth = FirebaseAuth.getInstance();// посиланя на користувача
        arrayList = new ArrayList<>();// новий список рецептів
        refreshRecipes();// оновлення рецептів

        return view;
    }

    private void refreshRecipes() {
        postsRef.addValueEventListener(new ValueEventListener() {// додавання рецептів до списку
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    if (data.child("uid").getValue().toString().equals(mAuth.getCurrentUser().getUid())){//умова при додавання рецепту до списка: якщо цей рецепт створив поточний користувач, тоді додати до списку
                        arrayList.add(new Recipe(// додавання рецепту
                                data.getKey().toString(),
                                data.child("description").getValue().toString(),
                                data.child("fullname").getValue().toString(),
                                data.child("profileimage").getValue().toString(),
                                data.child("recipecategory").getValue().toString(),
                                data.child("recipeimage").getValue().toString(),
                                data.child("recipeingredients").getValue().toString(),
                                data.child("recipename").getValue().toString(),
                                data.child("recipesteps").getValue().toString(),
                                data.child("uid").getValue().toString()));
                        adapter.notifyDataSetChanged();// оновлення списку
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adapter = new HomeAdapter(getActivity(), arrayList,true);// створення адаптеру
        listView.setAdapter(adapter);// встановлення адаптеру
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshRecipes();
    }
}
