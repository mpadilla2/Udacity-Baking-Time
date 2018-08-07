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

import java.util.ArrayList;
import java.util.List;

// Reference: click listener code: https://gist.github.com/riyazMuhammad/1c7b1f9fa3065aa5a46f
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> mRecipeList = new ArrayList<>();
    private CustomItemClickListener customItemClickListener;


    class ViewHolder extends RecyclerView.ViewHolder{
        TextView mRecipeText;

        ViewHolder(final View view) {
            super(view);
            mRecipeText = (TextView) view.findViewById(R.id.recipe_item_textview);
        }
    }


    public RecipeAdapter(List<Recipe> dataset, CustomItemClickListener itemClickListener) {
        this.mRecipeList = dataset;
        this.customItemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public RecipeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_recipe_item, parent, false);
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
    public void onBindViewHolder(@NonNull final RecipeAdapter.ViewHolder holder, final int position) {
        holder.mRecipeText.setText(mRecipeList.get(holder.getAdapterPosition()).getName());
    }


    @Override
    public int getItemCount() {
        return mRecipeList != null ? mRecipeList.size() : 0;
    }


    public void setRecipeList(List<Recipe> recipeList){
        mRecipeList.clear();
        mRecipeList.addAll(recipeList);
        notifyDataSetChanged();
    }
}

