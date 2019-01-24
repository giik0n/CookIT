package com.babylon.alex.cookit.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.babylon.alex.cookit.AddRecipe;
import com.babylon.alex.cookit.R;
import com.babylon.alex.cookit.ShowRecipeActivity;
import com.babylon.alex.cookit.pojo.Recipe;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends BaseAdapter {

    Activity activity;
    ArrayList<Recipe> arrayList;
    LayoutInflater inflater;

    FirebaseAuth mAuth;
    DatabaseReference currentPostRef, savedRef;

    Boolean isSaved = false;

    public HomeAdapter(Activity activity, ArrayList<Recipe> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAuth = FirebaseAuth.getInstance();
        currentPostRef = FirebaseDatabase.getInstance().getReference().child("Recipes");
        savedRef = FirebaseDatabase.getInstance().getReference().child("Saved");
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.item_recipe,viewGroup, false);
        CircleImageView circleImageView = view.findViewById(R.id.circleImageViewRecipeUserImage);
        TextView username = view.findViewById(R.id.textViewRecipeUserFullname);
        ImageView image = view.findViewById(R.id.imageViewRecipeImage);
        TextView description = view.findViewById(R.id.textViewRecipeDesc);
        TextView name = view.findViewById(R.id.textViewRecipeName);
        ImageView editButton = view.findViewById(R.id.imageViewPostEdit);
        ImageView deleteButton = view.findViewById(R.id.imageViewPostDelete);
        final ImageView saveButton = view.findViewById(R.id.imageViewPostSave);

        savedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!arrayList.isEmpty()){
                    if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(arrayList.get(i).getKey())){
                        saveButton.setImageResource(R.drawable.ic_bookmark_red_24dp);

                    }else{
                        saveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        Picasso.get().load(arrayList.get(i).getProfileImage()).resize(75,75).into(circleImageView);
        username.setText(arrayList.get(i).getProfilefullname());
        Picasso.get().load(arrayList.get(i).getRecipeImage()).resize(0,350).into(image);
        name.setText(arrayList.get(i).getRecipeName());
        description.setText(arrayList.get(i).getProfiledescription());

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ShowRecipeActivity.class);
                intent.putExtra("Name",arrayList.get(i).getRecipeName());
                intent.putExtra("Ingredients",arrayList.get(i).getRecipeIngredients());
                intent.putExtra("Description",arrayList.get(i).getProfiledescription());
                intent.putExtra("Category",arrayList.get(i).getRecipeCategory());
                intent.putExtra("Image",arrayList.get(i).getRecipeImage());
                intent.putExtra("Steps",arrayList.get(i).getRecipeSteps());
                intent.putExtra("Key",arrayList.get(i).getKey());
                activity.startActivity(intent);
            }
        };

        name.setOnClickListener(onClickListener);
        image.setOnClickListener(onClickListener);
        description.setOnClickListener(onClickListener);

        if (mAuth.getCurrentUser().getUid().equals(arrayList.get(i).getuId())){
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSaved = true;
                    savedRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (isSaved) {
                                if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(arrayList.get(i).getKey())) {
                                    savedRef.child(mAuth.getCurrentUser().getUid()).child(arrayList.get(i).getKey()).removeValue();
                                    isSaved = false;
                                } else {
                                    savedRef.child(mAuth.getCurrentUser().getUid()).child(arrayList.get(i).getKey()).setValue(arrayList.get(i).getKey());
                                    isSaved = false;
                                }

                            }
                            notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));
                builder
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                currentPostRef.child(arrayList.get(i).getKey()).removeValue();
                                StorageReference photoRef = FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(arrayList.get(i).getRecipeImage());
                                photoRef.delete();
                                arrayList.remove(i);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, AddRecipe.class);
                intent.putExtra("Name",arrayList.get(i).getRecipeName());
                intent.putExtra("Ingredients",arrayList.get(i).getRecipeIngredients());
                intent.putExtra("Description",arrayList.get(i).getProfiledescription());
                intent.putExtra("Category",arrayList.get(i).getRecipeCategory());
                intent.putExtra("Image",arrayList.get(i).getRecipeImage());
                intent.putExtra("Steps",arrayList.get(i).getRecipeSteps());
                intent.putExtra("Key",arrayList.get(i).getKey());
                activity.startActivity(intent);
            }
        });




        return view;
    }
}
