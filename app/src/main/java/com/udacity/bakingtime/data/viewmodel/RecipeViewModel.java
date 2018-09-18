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
import java.util.Objects;

// Reference: https://developer.android.com/topic/libraries/architecture/viewmodel#sharing
public class RecipeViewModel extends AndroidViewModel {

    private final LiveData<List<Recipe>> mAllRecipes;
    private final MutableLiveData<Recipe> mSelectedRecipe = new MutableLiveData<>();
    private final MutableLiveData<Step> mSelectedRecipeStep = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mStepsEndReached = new MutableLiveData<>();
    private final MutableLiveData<Boolean> mStepsBeginReached = new MutableLiveData<>();


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


    private List<Step> getAllRecipeSteps(LiveData<Recipe> recipe){
        return Objects.requireNonNull(recipe.getValue()).getSteps();
    }


    public void setSelectedRecipeStep(Step step){
        mSelectedRecipeStep.setValue(step);
    }



    public LiveData<Step> getSelectedRecipeStep(){

        if (Objects.requireNonNull(mSelectedRecipeStep.getValue()).equals(null)){
            setSelectedRecipeStep(Objects.requireNonNull(mSelectedRecipe.getValue()).getSteps().get(0));
        }

        return mSelectedRecipeStep;
    }


    public void getPreviousRecipeStep() {
        final List<Step> steps = getAllRecipeSteps(mSelectedRecipe);

        for (Step step : steps){
            if (step.getId() == Objects.requireNonNull(mSelectedRecipeStep.getValue()).getId()){
                final int stepIndex = steps.indexOf(step);
                mSelectedRecipeStep.setValue(steps.get(stepIndex - 1));
                setButtonsStatus();
                break;
            }
        }
    }


    public void getNextRecipeStep(){
        final List<Step> steps = getAllRecipeSteps(mSelectedRecipe);

        for (Step step : steps){
            if (step.getId() == Objects.requireNonNull(mSelectedRecipeStep.getValue()).getId()){
                final int stepIndex = steps.indexOf(step);
                mSelectedRecipeStep.setValue(steps.get(stepIndex + 1));
                setButtonsStatus();
                break;
            }
        }
    }


    public void setButtonsStatus(){

        final List<Step> steps = getAllRecipeSteps(mSelectedRecipe);
        final int listSize = steps.size();

        for (Step step : steps) {
            if (step.getId() == Objects.requireNonNull(mSelectedRecipeStep.getValue()).getId()) {
                final int stepIndex = steps.indexOf(step);

                if (stepIndex != 0 && stepIndex != listSize-1){
                    mStepsBeginReached.setValue(Boolean.FALSE);
                    mStepsEndReached.setValue(Boolean.FALSE);
                }

                if (stepIndex == 0){
                    mStepsBeginReached.setValue(Boolean.TRUE);
                    mStepsEndReached.setValue(Boolean.FALSE);
                }

                if (stepIndex == listSize-1){
                    mStepsBeginReached.setValue(Boolean.FALSE);
                    mStepsEndReached.setValue(Boolean.TRUE);
                }
            }
        }
    }


    public LiveData<Boolean> getStepsEndReached(){
        return mStepsEndReached;
    }


    public LiveData<Boolean> getStepsBeginReached(){
        return mStepsBeginReached;
    }
}
