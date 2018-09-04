package com.udacity.bakingtime.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.udacity.bakingtime.R;
import com.udacity.bakingtime.data.adapter.RecipeAdapter;
import com.udacity.bakingtime.data.listener.CustomItemClickListener;
import com.udacity.bakingtime.data.model.Recipe;
import com.udacity.bakingtime.data.viewmodel.RecipeViewModel;
import com.udacity.bakingtime.ui.utils.RecyclerViewItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// https://stackoverflow.com/questions/35237549/change-layoutmanager-depending-on-device-format/35238038#35238038
public class RecipeListFragment extends ViewLifecycleFragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String RECIPE_INGREDIENT_STEP_FRAGMENT = "recipe_ingredient_step_fragment";
    private static final String RECIPE_LIST_STATE = "recipe_list_state";

    private int mColumnCount = 3;
    private List<Recipe> mRecipeList = new ArrayList<>();
    private RecipeViewModel mRecipeViewModel;
    private RecipeAdapter mRecipeAdapter;


    public static RecipeListFragment newInstance(int columnCount){
        RecipeListFragment fragment = new RecipeListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeListFragment(){
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null){
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recipe_recyclerview);

        mRecipeAdapter = new RecipeAdapter(mRecipeList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Reference: https://developer.android.com/topic/libraries/architecture/viewmodel#sharing
                mRecipeViewModel.setSelectedRecipe(mRecipeList.get(position));
                launchRecipeIngredientsSteps();
            }
        });

        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.list_layout_margin);
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(1, spacingInPixels, true, 0));
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), mColumnCount));
        recyclerView.setAdapter(mRecipeAdapter);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadRecipes();
    }


    private void loadRecipes(){
        mRecipeViewModel = ViewModelProviders.of(
                Objects.requireNonNull(getActivity())).get(RecipeViewModel.class);

        final Observer<List<Recipe>> recipeListObserver = new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                mRecipeList = recipes;
                mRecipeAdapter.setRecipeList(recipes);
            }
        };
        mRecipeViewModel.getAllRecipes()
                .observe(Objects.requireNonNull(getViewLifecycleOwner()), recipeListObserver);
    }


    private void launchRecipeIngredientsSteps(){

        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment taggedFragment = fragmentManager.findFragmentByTag(RECIPE_INGREDIENT_STEP_FRAGMENT);

        if (taggedFragment == null){
            taggedFragment = RecipeIngredientStepFragment.newInstance();
            fragmentTransaction
                    .add(R.id.activity_fragment_container, taggedFragment, RECIPE_INGREDIENT_STEP_FRAGMENT)
                    .addToBackStack(RECIPE_LIST_STATE);
        } else {
            fragmentTransaction.show(taggedFragment);
        }

        List<Fragment> fragmentList = fragmentManager.getFragments();

        for (Fragment fragment : fragmentList){
            if (!fragment.equals(taggedFragment) && fragment.isVisible()){
                fragmentTransaction.hide(fragment);
            }
        }

        fragmentTransaction.commit();
    }
}
