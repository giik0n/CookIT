package com.example.yana.cookit;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yana.cookit.pojo.CookingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
// вікно показу інформаці про рецепт
public class ShowRecipeActivity extends AppCompatActivity {

    TextView description, ingredients;
    ImageView image, saveBtn;
    Button button;
    String steps = "", key;
    DatabaseReference savedRef = FirebaseDatabase.getInstance().getReference().child("Saved");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Boolean isSaved = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        image = findViewById(R.id.imageViewStartCooking);
        saveBtn = findViewById(R.id.saveRecipeStartCooking);
        description = findViewById(R.id.descriptionStartCooking);
        ingredients = findViewById(R.id.ingridientsStartCooking);
        button = findViewById(R.id.buttonStartCooking);

        final Bundle extras = getIntent().getExtras();// заповняє всі поля інформацією про рецепт
        setTitle(extras.getString("Name"));

        Picasso.get().load(Uri.parse(extras.getString("Image"))).resize(0,250).into(image);
        description.setText(extras.getString("Description"));
        ingredients.setText(extras.getString("Ingredients"));
        steps = extras.getString("Steps");
        key = extras.getString("Key").toString();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// кнопка запуску приготування рецепту
                Intent intent = new Intent(getApplicationContext(), CookingActivity.class);
                intent.putExtra("Steps",steps);
                intent.putExtra("Title",getTitle());
                startActivity(intent);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// кнопка збереження рецепту
                isSaved = true;
                savedRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (isSaved) {
                            if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(key)) {
                                savedRef.child(mAuth.getCurrentUser().getUid()).child(key).removeValue();
                                saveBtn.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                                isSaved = false;
                            } else {
                                savedRef.child(mAuth.getCurrentUser().getUid()).child(key).setValue(key);
                                saveBtn.setImageResource(R.drawable.ic_bookmark_red_24dp);
                                isSaved = false;
                            }

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        savedRef.addValueEventListener(new ValueEventListener() {// перевіряє чи збережений все рецепт та встановлює червоний колір
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(extras.get("Key").toString())){
                        saveBtn.setImageResource(R.drawable.ic_bookmark_red_24dp);

                    }else{
                        saveBtn.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);
                    }
                }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
