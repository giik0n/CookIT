package com.example.yana.cookit.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.yana.cookit.Adapters.HomeAdapter;
import com.example.yana.cookit.R;
import com.example.yana.cookit.pojo.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SearchFragment extends Fragment {


    public SearchFragment() {}

    ListView listView;
    Switch aSwitch;
    EditText editText;
    ArrayList<Recipe> arrayList;
    DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("Recipes");
    Boolean byIngredients = false;
    Spinner spinner;
    int currentCategory = 0;
    String[] categories;
    HomeAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        listView = view.findViewById(R.id.listViewSearch);
        aSwitch = view.findViewById(R.id.switchSearch);
        editText = view.findViewById(R.id.editTextSearch);
        spinner = view.findViewById(R.id.spinnerSearch);
        arrayList = new ArrayList<>();
        categories = new String[]{getString(R.string.all), getString(R.string.salads), getString(R.string.soups), getString(R.string.pizza), getString(R.string.pasta), getString(R.string.beef), getString(R.string.chicken), getString(R.string.fruit), getString(R.string.sushi), getString(R.string.other)};
        spinner.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, categories));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {// вибираемо категорію
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentCategory = i;
                    setAdapter(editText.getText().toString());// оновлюемо список з новою категорією
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {// переключаємо пощук між інгріжіентами та назвою
                byIngredients = b;
                setAdapter(editText.getText().toString());// оновлюємо
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setAdapter(editable.toString());
            }// при зміні пошукового тексту шукаємо підходящі рецепти та оновлюємо опять всееееее
        });

        return view;
    }

    public void setAdapter(final String s) {// оновлення списку рецептів
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for (DataSnapshot data: dataSnapshot.getChildren()){
                    String ingredients = data.child("recipeingredients").getValue().toString();
                    String name = data.child("recipename").getValue().toString();
                    String category = data.child("recipecategory").getValue().toString();

                    if (currentCategory==0){// перевірка на категорію, якщо all тоді просто виводимо всі рецепти
                        if (byIngredients){// перевіряжмо чи ми шукаємо по назві чи інгрідіентах
                            String[] strings = s.split(" ");
                            for (int i =0;i<strings.length;i++) {
                                if (ingredients.toLowerCase().contains(strings[i].toLowerCase())) {// перевіряємо чи співпадаюсь інгрідіенти
                                    arrayList.add(new Recipe(
                                            data.getKey(),
                                            data.child("description").getValue().toString(),
                                            data.child("fullname").getValue().toString(),
                                            data.child("profileimage").getValue().toString(),
                                            data.child("recipecategory").getValue().toString(),
                                            data.child("recipeimage").getValue().toString(),
                                            data.child("recipeingredients").getValue().toString(),
                                            data.child("recipename").getValue().toString(),
                                            data.child("recipesteps").getValue().toString(),
                                            data.child("uid").getValue().toString()));
                                }
                            }
                        }else {
                            if (name.toLowerCase().contains(s.toLowerCase())) {// перевіряємо чи співпадає імя
                                arrayList.add(new Recipe(
                                        data.getKey(),
                                        data.child("description").getValue().toString(),
                                        data.child("fullname").getValue().toString(),
                                        data.child("profileimage").getValue().toString(),
                                        data.child("recipecategory").getValue().toString(),
                                        data.child("recipeimage").getValue().toString(),
                                        data.child("recipeingredients").getValue().toString(),
                                        data.child("recipename").getValue().toString(),
                                        data.child("recipesteps").getValue().toString(),
                                        data.child("uid").getValue().toString()));

                            }
                        }
                    }else{
                        if (byIngredients){
                            String[] strings = s.split(" ");
                            for (int i =0;i<strings.length;i++) {

                                if (ingredients.toLowerCase().contains(strings[i].toLowerCase()) && category.equals(categories[currentCategory])) {
                                    arrayList.add(new Recipe(
                                            data.getKey(),
                                            data.child("description").getValue().toString(),
                                            data.child("fullname").getValue().toString(),
                                            data.child("profileimage").getValue().toString(),
                                            data.child("recipecategory").getValue().toString(),
                                            data.child("recipeimage").getValue().toString(),
                                            data.child("recipeingredients").getValue().toString(),
                                            data.child("recipename").getValue().toString(),
                                            data.child("recipesteps").getValue().toString(),
                                            data.child("uid").getValue().toString()));

                                }
                            }
                        }else {
                            if (name.toLowerCase().contains(s.toLowerCase()) && category.equals(categories[currentCategory])) {
                                arrayList.add(new Recipe(
                                        data.getKey(),
                                        data.child("description").getValue().toString(),
                                        data.child("fullname").getValue().toString(),
                                        data.child("profileimage").getValue().toString(),
                                        data.child("recipecategory").getValue().toString(),
                                        data.child("recipeimage").getValue().toString(),
                                        data.child("recipeingredients").getValue().toString(),
                                        data.child("recipename").getValue().toString(),
                                        data.child("recipesteps").getValue().toString(),
                                        data.child("uid").getValue().toString()));

                            }
                        }
                    }
                }

                Set<Recipe> st= new HashSet<>();
                st.addAll(arrayList);
                arrayList.clear();
                arrayList.addAll(st);
                Collections.reverse(arrayList);
                adapter = new HomeAdapter(getActivity(), arrayList,false);
                listView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onPause() {
        adapter.notifyDataSetChanged();
        super.onPause();
    }
}
