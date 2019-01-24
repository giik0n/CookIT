package com.babylon.alex.cookit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddStepsActivity extends AppCompatActivity {
    private Button addStep, addTimer, makeRecipe;
    private EditText editTextStep, editTextTimer;
    private ListView listViewRecipe;
    private ProgressDialog loading;

    ArrayList<String> items;
    ArrayAdapter<String> adapter;
    String recipe, recipeName, recipeIngredients, recipeCategory, recipeDesc, downloadUrl, dateTime, key;
    Uri imageUri;

    private StorageReference imagesRef;
    private DatabaseReference usersRef, postsRef;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_steps);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagesRef = FirebaseStorage.getInstance().getReference().child("Recipe Images");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Recipes");
        mAuth = FirebaseAuth.getInstance();

        recipeName = getIntent().getStringExtra("Name");
        recipeIngredients = getIntent().getStringExtra("Ingredients");
        recipeCategory = getIntent().getStringExtra("Category");
        recipeDesc = getIntent().getStringExtra("Description");
        imageUri = Uri.parse(getIntent().getStringExtra("Image"));



        addStep = findViewById(R.id.buttonAddStep);
        addTimer = findViewById(R.id.buttonAddTimer);
        makeRecipe = findViewById(R.id.buttonMakeRecipe);
        loading = new ProgressDialog(this);

        listViewRecipe = findViewById(R.id.listViewRecipe);
        editTextTimer = findViewById(R.id.editTextTime);
        editTextStep = findViewById(R.id.editTextStep);
        items = new ArrayList<>();

        if (getIntent().getExtras().containsKey("Steps")){
            String[] steps = getIntent().getExtras().getString("Steps").split(";");
            for (int i = 0; i< steps.length; i++){
                items.add(steps[i]);
            }
        }

        if (getIntent().getExtras().containsKey("Key")){
            key = getIntent().getExtras().getString("Key");
        }else{
            key="";
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        listViewRecipe.setAdapter(adapter);
        listViewRecipe.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                items.remove(i);
                listViewRecipe.setAdapter(adapter);
                return true;
            }
        });

        addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.add(editTextStep.getText().toString());
                listViewRecipe.setAdapter(new ArrayAdapter<String>(AddStepsActivity.this, android.R.layout.simple_list_item_1, items));
            }
        });

        addTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.add("timer: " + editTextTimer.getText().toString() + " minutes");
                listViewRecipe.setAdapter(new ArrayAdapter<String>(AddStepsActivity.this, android.R.layout.simple_list_item_1, items));
            }
        });

        makeRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (items.size()>0){
                    loading.setTitle("Saving new recipe");
                    loading.setMessage("Please wait...");
                    loading.show();
                    recipe = "";
                    for (int i = 0; i < items.size(); i++) {
                        recipe = recipe + items.get(i).toString() + ";";
                    }
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy");
                    String date = sdf.format(calendar.getTime());

                    SimpleDateFormat sdft = new SimpleDateFormat("HH:mm:ss");
                    String time = sdft.format(calendar.getTime());
                    dateTime = date.concat(time);
                    StorageReference filePath = imagesRef.child(imageUri.getLastPathSegment()+dateTime+".png");

                    filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                downloadUrl = task.getResult().getDownloadUrl().toString();

                                usersRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            String userFullname = dataSnapshot.child("fullname").getValue().toString();
                                            String userImage= dataSnapshot.child("profileimage").getValue().toString();

                                            HashMap postMap = new HashMap();
                                            postMap.put("uid", mAuth.getCurrentUser().getUid());
                                            postMap.put("profileimage", userImage);
                                            postMap.put("fullname", userFullname);
                                            postMap.put("recipename", recipeName);
                                            postMap.put("recipecategory", recipeCategory);
                                            postMap.put("recipeingredients", recipeIngredients);
                                            postMap.put("recipeimage", downloadUrl);
                                            postMap.put("recipesteps", recipe);
                                            postMap.put("description", recipeDesc);
                                            if (key.equals("")){
                                                key = mAuth.getCurrentUser().getUid() + dateTime;
                                            }
                                            postsRef.child(key).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(AddStepsActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }else{
                                                        Toast.makeText(AddStepsActivity.this, "Error: "+ task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                    loading.dismiss();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });



                            }else{
                                Toast.makeText(AddStepsActivity.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(AddStepsActivity.this, "Add some steps to recipe...", Toast.LENGTH_SHORT).show();
                }
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
