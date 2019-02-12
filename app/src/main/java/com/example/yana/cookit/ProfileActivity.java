package com.example.yana.cookit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
// вікно налаштування профілю
public class ProfileActivity extends AppCompatActivity {

    CircleImageView profilePhoto;
    EditText profileUsername, profileFullname;
    Button saveButton;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference userProfileImageRef;
    String currentUserID;
    ProgressDialog loadingBar;

    private Uri profilePhotoUri;

    final static int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profilePhoto = findViewById(R.id.profile_photo);
        profileUsername = findViewById(R.id.profile_username);
        profileFullname = findViewById(R.id.profile_fullname);
        saveButton = findViewById(R.id.profile_save);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        loadingBar = new ProgressDialog(this);

        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Crop.pickImage(ProfileActivity.this);
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {// бере введені данні та зберігає в бд

                String username = profileUsername.getText().toString();
                String fullname = profileFullname.getText().toString();

                if (username.isEmpty() || fullname.isEmpty()){
                    Toast.makeText(ProfileActivity.this, "Fill all the fields", Toast.LENGTH_SHORT).show();
                }else{
                    loadingBar.setTitle("Account saving");
                    loadingBar.setMessage("Please wait");
                    loadingBar.show();
                    HashMap userMap = new HashMap();
                    userMap.put("username",username);
                    userMap.put("fullname",fullname);
                    usersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ProfileActivity.this, "Your data is saved successfully", Toast.LENGTH_SHORT).show();
                            }else{
                                String message = task.getException().getMessage();
                                Toast.makeText(ProfileActivity.this, "Error: "+message, Toast.LENGTH_SHORT).show();
                            }
                            loadingBar.dismiss();
                        }
                    });
                }
            }
        });

        usersRef.addValueEventListener(new ValueEventListener() {// якщо користувач є в бд заповняє поля його данними
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    if (dataSnapshot.hasChild("profileimage")){
                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).into(profilePhoto);
                    }
                    if (dataSnapshot.hasChild("username")){
                        profileUsername.setText(dataSnapshot.child("username").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("fullname")){
                        profileFullname.setText(dataSnapshot.child("fullname").getValue().toString());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {// вікно вибору фото та збереження в хранилище
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK){
            Crop.of(data.getData(),Uri.fromFile(new File(getCacheDir(),"cropped "))).asSquare().start(this);
            profilePhoto.setImageURI(Crop.getOutput(data));

        }else if(requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK){
            Uri uri = Crop.getOutput(data);
            profilePhoto.setImageURI(uri);

            profilePhotoUri = uri;

            StorageReference filePath = userProfileImageRef.child(currentUserID + ".png");
            loadingBar.setMessage(getString(R.string.savingPhoto));
            loadingBar.show();
            filePath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {// пересилання файлу в firebase
                    if (task.isSuccessful()){
                        Toast.makeText(ProfileActivity.this, R.string.photoSavedSuccessfully, Toast.LENGTH_SHORT).show();
                        final String downloadUrl = task.getResult().getDownloadUrl().toString();
                        usersRef.child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(ProfileActivity.this, R.string.imageSaved, Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(ProfileActivity.this, getString(R.string.error)+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                loadingBar.dismiss();
                            }
                        });
                    }
                }
            });
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
