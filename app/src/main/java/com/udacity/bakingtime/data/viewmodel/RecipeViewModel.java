package com.udacity.bakingtime.data.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.udacity.bakingtime.data.model.Step;
import com.udacity.bakingtime.data.remote.DataRepository;
import com.udacity.bakingtime.data.model.Recipe;

import java.util.List;

// Reference: https://developer.android.com/topic/libraries/architecture/viewmodel#sharing
public class RecipeViewModel extends AndroidViewModel {

    private final LiveData<List<Recipe>> mAllRecipes;
    private final MutableLiveData<Recipe> mSelectedRecipe = new MutableLiveData<>();
    private final MutableLiveData<Step> mSelectedRecipeStep = new MutableLiveData<>();


    public RecipeViewModel(@NonNull Application application) {
        super(application);
        DataRepository mRepository = new DataRepository(application);
        mAllRecipes = mRepository.getAllRecipes();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return mAllRecipes;
    }

    public void setSelectedRecipe(Recipe recipe){
        mSelectedRecipe.setValue(recipe);
    }

    public LiveData<Recipe> getSelectedRecipe(){
        return mSelectedRecipe;
    }

    public void setSelectedRecipeStep(Step step){
        mSelectedRecipeStep.setValue(step);
    }

    public LiveData<Step> getSelectedRecipeStep(){
        return mSelectedRecipeStep;
    }
}
