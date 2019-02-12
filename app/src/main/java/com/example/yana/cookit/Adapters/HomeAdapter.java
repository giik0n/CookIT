package com.example.yana.cookit.Adapters;

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

import com.example.yana.cookit.AddRecipe;
import com.example.yana.cookit.R;
import com.example.yana.cookit.ShowRecipeActivity;
import com.example.yana.cookit.pojo.Recipe;
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


// Це клас - адаптер, потрібен для того щоб в списку рецептів елементи були не лише текст, ай фото, кнопки...
public class HomeAdapter extends BaseAdapter {


    private Activity activity;//змінна батьківського вікна
    private ArrayList<Recipe> arrayList;//список рецептів
    private LayoutInflater inflater;// клас який шукає вид вікна, та підключає його

    private FirebaseAuth mAuth;// авторизація користувача firebase
    private DatabaseReference currentPostRef, savedRef;// посилання на поточний рецепт, та збережені рецепти

    private Boolean isSaved = false;// перевірка на зберігання
    private Boolean isSaveShow;// змінна перевіряє чи поточний рецепт збережений у користувача

    public HomeAdapter(Activity activity, ArrayList<Recipe> arrayList, Boolean isSaveShow) { //конструктор класу
        this.activity = activity;
        this.arrayList = arrayList;
        this.isSaveShow = isSaveShow;
        this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);// знаходимо вид елемент списку
        mAuth = FirebaseAuth.getInstance();//зотримуємо посилання на користувача
        currentPostRef = FirebaseDatabase.getInstance().getReference().child("Recipes");//посилання на всі рецепти
        savedRef = FirebaseDatabase.getInstance().getReference().child("Saved");// на збережені
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }// віддає кількість елементів в списку

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }//повертає потрібний елемент

    @Override
    public long getItemId(int i) {
        return i;
    }//повертає id елементу

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {//повертає вид елементу списку
        view = inflater.inflate(R.layout.item_recipe,viewGroup, false);//знаходимо вид

        final int position = i;//позиція в списку
        CircleImageView circleImageView = view.findViewById(R.id.circleImageViewRecipeUserImage);//картинка користувача //знаходження посиланнь на всі елементи виду
        TextView username = view.findViewById(R.id.textViewRecipeUserFullname);//імя користувача
        ImageView image = view.findViewById(R.id.imageViewRecipeImage);// картинка рецепту
        TextView description = view.findViewById(R.id.textViewRecipeDesc);// опис рецепту
        TextView name = view.findViewById(R.id.textViewRecipeName);//назва
        ImageView editButton = view.findViewById(R.id.imageViewPostEdit);//кнопка редагування
        ImageView deleteButton = view.findViewById(R.id.imageViewPostDelete);//кнопка видалення
        final ImageView saveButton = view.findViewById(R.id.imageViewPostSave);//кнопка збереження

        savedRef.addValueEventListener(new ValueEventListener() {//розставляє значки збережених рецептів червоним
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!arrayList.isEmpty()){
                    if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(arrayList.get(position).getKey())){//якщо рецепт збережений
                        saveButton.setImageResource(R.drawable.ic_bookmark_red_24dp);
                    }else{
                        saveButton.setImageResource(R.drawable.ic_bookmark_border_grey_24dp);//якщо ні
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        Picasso.get().load(arrayList.get(i).getProfileImage()).resize(75,75).into(circleImageView);//загрузка картинки користувача в кругову рамку
        username.setText(arrayList.get(i).getProfilefullname());//встановлення імя користувача
        Picasso.get().load(arrayList.get(i).getRecipeImage()).resize(0,350).into(image);//встановлення картинки рецепту
        name.setText(arrayList.get(i).getRecipeName());//встановлення назви рецепту
        description.setText(arrayList.get(i).getProfiledescription());//встановлення опису

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {//слухає чи натиснув користувач на рецепт та відкриває його
                Intent intent = new Intent(activity, ShowRecipeActivity.class);//створюється посилання
                intent.putExtra("Name",arrayList.get(position).getRecipeName());//надаються параметри рецепту
                intent.putExtra("Ingredients",arrayList.get(position).getRecipeIngredients());
                intent.putExtra("Description",arrayList.get(position).getProfiledescription());
                intent.putExtra("Category",arrayList.get(position).getRecipeCategory());
                intent.putExtra("Image",arrayList.get(position).getRecipeImage());
                intent.putExtra("Steps",arrayList.get(position).getRecipeSteps());
                intent.putExtra("Key",arrayList.get(position).getKey());
                activity.startActivity(intent);//запускається вікно
            }
        };

        name.setOnClickListener(onClickListener);//встановлює цей слухач на імя рецепту, картинку, та опис
        image.setOnClickListener(onClickListener);
        description.setOnClickListener(onClickListener);

        if (mAuth.getCurrentUser().getUid().equals(arrayList.get(i).getuId())){//якщо цей рецепт іншого користувача то можне тільки зберегти, а якщо ващ, тоді відрегадувати та видалити
            editButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.INVISIBLE);
        }
        if (!isSaveShow){
            saveButton.setVisibility(View.INVISIBLE);
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//збередення рецепту
                isSaved = true;
                    savedRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (isSaved) {
                                if (dataSnapshot.child(mAuth.getCurrentUser().getUid()).hasChild(arrayList.get(position).getKey())) {//якщо цей пост збережений тожі при повторному натисканні видаляє його
                                    savedRef.child(mAuth.getCurrentUser().getUid()).child(arrayList.get(position).getKey()).removeValue();
                                    isSaved = false;
                                } else {
                                    savedRef.child(mAuth.getCurrentUser().getUid()).child(arrayList.get(position).getKey()).setValue(arrayList.get(position).getKey());//зберігає рецепт
                                    isSaved = false;
                                }
                                notifyDataSetChanged();//оновлює список
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//слухач видалення
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));//діалог підтвердження
                builder
                        .setMessage(R.string.areYouSure)
                        .setPositiveButton(R.string.yes,  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                currentPostRef.child(arrayList.get(position).getKey()).removeValue();
                                StorageReference photoRef = FirebaseStorage.getInstance().getReference().getStorage().getReferenceFromUrl(arrayList.get(position).getRecipeImage());
                                photoRef.delete();
                                arrayList.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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
            public void onClick(View view) {//слухач редагування
                Intent intent = new Intent(activity, AddRecipe.class);//створює посилання
                intent.putExtra("Name",arrayList.get(position).getRecipeName());//передає параметри
                intent.putExtra("Ingredients",arrayList.get(position).getRecipeIngredients());
                intent.putExtra("Description",arrayList.get(position).getProfiledescription());
                intent.putExtra("Category",arrayList.get(position).getRecipeCategory());
                intent.putExtra("Image",arrayList.get(position).getRecipeImage());
                intent.putExtra("Steps",arrayList.get(position).getRecipeSteps());
                intent.putExtra("Key",arrayList.get(position).getKey());
                activity.startActivity(intent);//запускає нове вікно редагування
            }
        });
        return view;
    }
}
