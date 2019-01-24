package com.babylon.alex.cookit;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.babylon.alex.cookit.pojo.CookingActivity;
import com.squareup.picasso.Picasso;

public class ShowRecipeActivity extends AppCompatActivity {

    TextView description, ingredients;
    ImageView image;
    Button button;
    String steps = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        image = findViewById(R.id.imageViewStartCooking);
        description = findViewById(R.id.descriptionStartCooking);
        ingredients = findViewById(R.id.ingridientsStartCooking);
        button = findViewById(R.id.buttonStartCooking);

        Bundle extras = getIntent().getExtras();
        setTitle(extras.getString("Name"));

        Picasso.get().load(Uri.parse(extras.getString("Image"))).resize(0,250).into(image);
        description.setText(extras.getString("Description"));
        ingredients.setText(extras.getString("Ingredients"));
        steps = extras.getString("Steps");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CookingActivity.class);
                intent.putExtra("Steps",steps);
                intent.putExtra("Title",getTitle());
                startActivity(intent);
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
