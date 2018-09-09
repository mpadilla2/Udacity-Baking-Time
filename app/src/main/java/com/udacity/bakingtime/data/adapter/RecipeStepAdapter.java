package com.udacity.bakingtime.data.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.bakingtime.R;
import com.udacity.bakingtime.data.listener.CustomItemClickListener;
import com.udacity.bakingtime.data.model.Recipe;
import com.udacity.bakingtime.data.model.Step;

import java.util.ArrayList;
import java.util.List;

// Reference: click listener code: https://gist.github.com/riyazMuhammad/1c7b1f9fa3065aa5a46f
public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.ViewHolder> {

    private List<Step> mRecipeStepList = new ArrayList<>();
    private CustomItemClickListener customItemClickListener;


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView mRecipeStepText;

        ViewHolder(final View view){
            super(view);
            mRecipeStepText = (TextView) view.findViewById(R.id.recipe_step_item_textview);
        }

    }


    public RecipeStepAdapter(List<Step> dataset, CustomItemClickListener itemClickListener){
        this.mRecipeStepList = dataset;
        this.customItemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recipe_step_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                customItemClickListener.onItemClick(v, viewHolder.getAdapterPosition());
            }
        });
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Step stepItem = mRecipeStepList.get(holder.getAdapterPosition());
        String stepText = "";
        if (stepItem.getId() != 0){
            stepText = String.valueOf(stepItem.getId()) + ". " + stepItem.getShortDescription();
        } else {
            stepText = stepItem.getShortDescription();
        }
        holder.mRecipeStepText.setText(stepText);
    }


    @Override
    public int getItemCount() {
        return mRecipeStepList != null ? mRecipeStepList.size() : 0;
    }


    public void setRecipeStepList(List<Step> recipeStepList){
        mRecipeStepList.clear();
        mRecipeStepList.addAll(recipeStepList);
        notifyDataSetChanged();
    }
}
