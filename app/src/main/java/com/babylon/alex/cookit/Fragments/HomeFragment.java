package com.babylon.alex.cookit.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.babylon.alex.cookit.Adapters.HomeAdapter;
import com.babylon.alex.cookit.R;
import com.babylon.alex.cookit.pojo.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private ListView listView;

    private DatabaseReference postsRef;
    private ArrayList<Recipe> arrayList;
    private View view;
    private HomeAdapter adapter;
    Spinner spinner;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        listView = view.findViewById(R.id.listViewHome);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        
        postsRef = FirebaseDatabase.getInstance().getReference().child("Recipes");
        arrayList = new ArrayList<>();

        refreshRecipes();

        return view;
    }

    private void refreshRecipes() {


            postsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    arrayList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()){
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

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        adapter = new HomeAdapter(getActivity(),arrayList);
        listView.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshRecipes();
    }
}
