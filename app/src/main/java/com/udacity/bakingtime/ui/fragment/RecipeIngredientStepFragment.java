package com.udacity.bakingtime.ui.fragment;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.bakingtime.R;
import com.udacity.bakingtime.data.adapter.RecipeStepAdapter;
import com.udacity.bakingtime.data.listener.CustomItemClickListener;
import com.udacity.bakingtime.data.model.Step;
import com.udacity.bakingtime.data.viewmodel.RecipeViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecipeIngredientStepFragment extends ViewLifecycleFragment {

    private static final String RECIPE_DETAIL_FRAGMENT = "recipe_detail_fragment";
    private static final String RECIPE_INGREDIENT_STEP_STATE = "recipe_ingredient_step_step";

    private RecipeViewModel mRecipeViewModel;
    private RecipeStepAdapter mRecipeStepAdapter;
    private List<Step> mRecipeStepList = new ArrayList<>();
    private TextView mIngredientsTextView;
    private boolean mIsLargeScreen = false;
    private Toolbar mToolbar;


    public static RecipeIngredientStepFragment newInstance(){
        RecipeIngredientStepFragment fragment = new RecipeIngredientStepFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeIngredientStepFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_ingredient_step, container, false);

        // Reference: https://stackoverflow.com/questions/35237549/change-layoutmanager-depending-on-device-format
        mIsLargeScreen = Objects.requireNonNull(getActivity()).getResources().getBoolean(R.bool.isLargeScreen);
        mIngredientsTextView = view.findViewById(R.id.recipe_ingredients_textview);

        mToolbar = getActivity().findViewById(R.id.recipe_activity_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        Context context = view.getContext();
        RecyclerView recyclerView = view.findViewById(R.id.recipe_step_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerView.ItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(mDividerItemDecoration);

        setUpRecipeStepAdapter();
        recyclerView.setAdapter(mRecipeStepAdapter);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewModel();
        loadRecipeSteps();
        loadIngredients();
        setToolbarTitle();
        //initRecipeStep();

        Log.d("INGREDIENTSTEPFRAGMENT", "ONACTIVITYCREATED FIRED");
    }


    private void setUpRecipeStepAdapter() {
        mRecipeStepAdapter = new RecipeStepAdapter(mRecipeStepList, new CustomItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // Reference: https://developer.android.com/topic/libraries/architecture/viewmodel#sharing
                mRecipeViewModel.setSelectedRecipeStep(mRecipeStepList.get(position));
                launchRecipeStepContent();
            }
        });
    }


    private void setUpViewModel(){
        mRecipeViewModel = ViewModelProviders.of(
                Objects.requireNonNull(getActivity())).get(RecipeViewModel.class);
    }


    private void loadRecipeSteps(){
        mRecipeStepList = Objects.requireNonNull(mRecipeViewModel.getSelectedRecipe().getValue()).getSteps();
        mRecipeStepAdapter.setRecipeStepList(Objects.requireNonNull(mRecipeStepList));
    }


    private void loadIngredients(){
        mIngredientsTextView.setText(Objects.requireNonNull(
                mRecipeViewModel.getSelectedRecipe()
                        .getValue()).getIngredientsString());
    }


    private void setToolbarTitle() {
        mToolbar.setTitle(Objects.requireNonNull(mRecipeViewModel.getSelectedRecipe().getValue()).getName());
    }


    private void launchRecipeStepContent(){

        FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment taggedFragment = fragmentManager.findFragmentByTag(RECIPE_DETAIL_FRAGMENT);

        if (mIsLargeScreen){

            taggedFragment = RecipeDetailFragment.newInstance();
            fragmentTransaction.replace(R.id.item_detail_container, taggedFragment, RECIPE_DETAIL_FRAGMENT);
        } else {

            if (taggedFragment == null) {

                taggedFragment = RecipeDetailFragment.newInstance();
                fragmentTransaction.add(R.id.activity_fragment_container, taggedFragment, RECIPE_DETAIL_FRAGMENT)
                        .addToBackStack(RECIPE_INGREDIENT_STEP_STATE);
            } else {

                fragmentTransaction.show(taggedFragment);
            }

            List<Fragment> fragmentList = fragmentManager.getFragments();

            for (Fragment fragment : fragmentList) {
                if (!fragment.equals(taggedFragment) && fragment.isVisible()) {
                    fragmentTransaction.hide(fragment);
                }
            }
        }

        fragmentTransaction.commit();
    }


/*    private void initRecipeStep(){

        if (mIsLargeScreen) {
            mRecipeViewModel.setSelectedRecipeStep(mRecipeStepList.get(0));
            launchRecipeStepContent();
        }
    }*/
}
