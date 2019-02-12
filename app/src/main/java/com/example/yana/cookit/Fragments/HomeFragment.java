package com.example.yana.cookit.Fragments;

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

import com.example.yana.cookit.Adapters.HomeAdapter;
import com.example.yana.cookit.R;
import com.example.yana.cookit.pojo.Recipe;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//фрагмент з новими рецептами (лента)


public class HomeFragment extends Fragment {

    private ListView listView;// вид списку

    private DatabaseReference postsRef;// посилання на бд
    private ArrayList<Recipe> arrayList;// список з рецептів
    private View view;// вид
    private HomeAdapter adapter;// адаптер для списку

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);// знаходимо вид фрагменту
        listView = view.findViewById(R.id.listViewHome);// знаходими вид списку
        listView.setDivider(null);// прибираємо відокремлення між елементами списку
        listView.setDividerHeight(0);//тоже)
        
        postsRef = FirebaseDatabase.getInstance().getReference().child("Recipes");// отримуємо посилання на бд с рецептами
        arrayList = new ArrayList<>();// створюжмо новий список з рецептами

        refreshRecipes();// оновлення списку

        return view;
    }

    private void refreshRecipes() {


            postsRef.addValueEventListener(new ValueEventListener() {// заповняє список через адаптер, надаючи йому всі параметри кожного рецепту
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    arrayList.clear();// очищення списку
                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        arrayList.add(new Recipe(//додавання елементу
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
                        adapter.notifyDataSetChanged();//оновлення зписку з доданим елементом
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        adapter = new HomeAdapter(getActivity(),arrayList,true);//створення нового адаптеру з заповненим списком
        listView.setAdapter(adapter);// надаємо виду списку адаптер
    }


    @Override
    public void onResume() {
        super.onResume();
        refreshRecipes();
    }
}
