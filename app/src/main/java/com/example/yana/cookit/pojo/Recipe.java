package com.example.yana.cookit.pojo;

public class Recipe {// клас рецепту для передачі параметрів

    private String key, profiledescription, profilefullname, profileImage, recipeCategory, recipeImage, recipeIngredients, recipeName, recipeSteps, uId;

    public Recipe(String key, String profiledescription, String profilefullname, String profileImage, String recipeCategory, String recipeImage, String recipeIngredients, String recipeName, String recipeSteps, String uId) {
        this.key = key;
        this.profiledescription = profiledescription;
        this.profilefullname = profilefullname;
        this.profileImage = profileImage;
        this.recipeCategory = recipeCategory;
        this.recipeImage = recipeImage;
        this.recipeIngredients = recipeIngredients;
        this.recipeName = recipeName;
        this.recipeSteps = recipeSteps;
        this.uId = uId;
    }

    public String getKey() {
        return key;
    }

    public String getProfiledescription() {
        return profiledescription;
    }

    public String getProfilefullname() {
        return profilefullname;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getRecipeCategory() {
        return recipeCategory;
    }

    public String getRecipeImage() {
        return recipeImage;
    }

    public String getRecipeIngredients() {
        return recipeIngredients;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getRecipeSteps() {
        return recipeSteps;
    }

    public String getuId() {
        return uId;
    }

    @Override
    public boolean equals(Object obj) {// для порівняння рецептів
        // TODO Auto-generated method stub
        if(obj instanceof Recipe)
        {
            Recipe temp = (Recipe) obj;
            if(this.key == temp.key)
                return true;
        }
        return false;

    }
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub

        return (this.key.hashCode());
    }
}
