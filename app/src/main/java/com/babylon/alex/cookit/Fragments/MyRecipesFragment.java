package com.babylon.alex.cookit.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.babylon.alex.cookit.Adapters.HomeAdapter;
import com.babylon.alex.cookit.R;
import com.babylon.alex.cookit.pojo.Recipe;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyRecipesFragment extends Fragment {

    ListView listView;
    HomeAdapter adapter;
    ArrayList<Recipe> arrayList;
    private DatabaseReference postsRef;
    private FirebaseAuth mAuth;
    private View view;

    public MyRecipesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_recipes, container, false);

        listView = view.findViewById(R.id.myRecipesListView);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        postsRef = FirebaseDatabase.getInstance().getReference().child("Recipes");
        mAuth = FirebaseAuth.getInstance();
        arrayList = new ArrayList<>();
        refreshRecipes();

        return view;
    }

    private void refreshRecipes() {
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    if (data.child("uid").getValue().toString().equals(mAuth.getCurrentUser().getUid())){
                        arrayList.add(new Recipe(
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
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adapter = new HomeAdapter(getActivity(), arrayList);
        listView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshRecipes();
    }
}