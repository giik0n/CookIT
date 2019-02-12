package com.example.yana.cookit;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.yana.cookit.Fragments.HomeFragment;
import com.example.yana.cookit.Fragments.RecipesFragment;
import com.example.yana.cookit.Fragments.SearchFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
// головне меню с переключенням фрагментів
    private FloatingActionButton fab;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private android.support.v4.app.FragmentTransaction ft;

    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private RecipesFragment RecipesFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        RecipesFragment = new RecipesFragment();
        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ft = getSupportFragmentManager().beginTransaction();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);// вибор фрагмент
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                ft = getSupportFragmentManager().beginTransaction();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        setTitle(R.string.title_home);
                        ft.replace(R.id.container,homeFragment).commit();
                        return true;
                    case R.id.navigation_search:
                        setTitle(R.string.title_search);
                        ft.replace(R.id.container, searchFragment).commit();
                        return true;
                    case R.id.navigation_recipes:
                        setTitle(R.string.title_recipes);
                        ft.replace(R.id.container, RecipesFragment).commit();
                        return true;
                }
                ft.commit();
                return true;
            }
        });
        navigation.setSelectedItemId(R.id.navigation_home);
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,AddRecipe.class));
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }else{
            checkUserExistence();
            
        }

    }

    private void checkUserExistence() {
        final String currentUserId = mAuth.getCurrentUser().getUid();// если новий пользователь то переключает на заполнения данних имени фото и тд
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(currentUserId)){
                    startActivity(new Intent(MainActivity.this,ProfileActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// пункти меню профиля и вихода
        int id = item.getItemId();

        switch (id){
            case R.id.navigation_logout:
                mAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;

            case R.id.navigation_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}
