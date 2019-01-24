package com.babylon.alex.cookit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.transform.Result;


public class AddRecipe extends AppCompatActivity {

    private Spinner spinner;
    private ImageButton imageButton;
    EditText name, ingredients, description;
    private Button button;
    Uri imageUri;

    Bitmap bitmapImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinner = findViewById(R.id.spinnerAdd);
        imageButton = findViewById(R.id.imageButtonAdd);
        name = findViewById(R.id.editTextAddName);
        ingredients = findViewById(R.id.editTextIngredients);
        description = findViewById(R.id.editTextShortDesc);
        button = findViewById(R.id.buttonAddSteps);
        String[] items = new String[]{"Salads", "Soups", "Pizza", "Pasta", "Beef", "Chicken", "Fruit", "Sushi", "Other..."};




        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crop.pickImage(AddRecipe.this);
            }
        });

        final Bundle extras = getIntent().getExtras();
        if (extras != null){
            name.setText(extras.get("Name").toString());
            ingredients.setText(extras.get("Ingredients").toString());
            description.setText(extras.get("Description").toString());
            spinner.setSelection(Arrays.asList(items).indexOf(extras.get("Category").toString()));
            Picasso.get().load(extras.get("Image").toString()).resize(0,350).into(imageButton);
            final StorageReference islandRef = FirebaseStorage.getInstance().getReferenceFromUrl(extras.get("Image").toString());

            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }

            final File finalLocalFile = localFile;
            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    imageUri = Uri.fromFile(finalLocalFile);
                    islandRef.delete();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String recipeName = name.getText().toString();
                String recipeIngredients = ingredients.getText().toString();
                String recipeCategory = spinner.getSelectedItem().toString();
                String recipeDescription = description.getText().toString();
                if (imageUri == null || imageUri.toString().equals("")){
                    Toast.makeText(AddRecipe.this, "Select Image", Toast.LENGTH_SHORT).show();
                }else if(recipeName.equals("") || recipeName == null){
                    Toast.makeText(AddRecipe.this, "Set recipe name", Toast.LENGTH_SHORT).show();
                }else if (recipeIngredients.equals("") || recipeIngredients == null){
                    Toast.makeText(AddRecipe.this, "Add ingredients", Toast.LENGTH_SHORT).show();
                }else if (recipeDescription.equals("") || recipeDescription == null){
                    Toast.makeText(AddRecipe.this, "Write short description", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(AddRecipe.this,AddStepsActivity.class);
                    intent.putExtra("Name",recipeName);
                    intent.putExtra("Ingredients",recipeIngredients);
                    intent.putExtra("Category",recipeCategory);
                    intent.putExtra("Description",recipeDescription);
                    intent.putExtra("Image",imageUri.toString());
                    if (getIntent().hasExtra("Key")){
                        intent.putExtra("Key",extras.get("Key").toString());
                    }
                    if (getIntent().hasExtra("Steps")){
                        intent.putExtra("Steps",extras.get("Steps").toString());
                    }
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK){
            Crop.of(data.getData(),Uri.fromFile(new File(getCacheDir(),"cropped "))).start(this);
            imageButton.setImageURI(Crop.getOutput(data));
        }else if(requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK){
            Uri uri = Crop.getOutput(data);
            imageButton.setImageURI(uri);
            imageUri = uri;
        }else{
            Toast.makeText(this, "Error: try again", Toast.LENGTH_SHORT).show();
        }
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
