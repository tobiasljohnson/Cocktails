package com.morecocktails.cocktails;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by tobiasljohnson on 8/7/15.
 */
public class RecipeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe, container,false);
        Cocktail cocktail = getArguments().getParcelable("cocktail");
        TextView recipeView = (TextView) view.findViewById(R.id.fragment_recipe_view);

        String displayedRecipe = "";
        for (RecipeEntry recipeEntry : cocktail.recipe) {
            displayedRecipe += recipeEntry.amount + " " + recipeEntry.getIngredientName() + "\n";
        }
        if (cocktail.instructions != null && !cocktail.instructions.equals("")) {
            displayedRecipe += "\n" + cocktail.instructions + "\n";
        }
        if (cocktail.garnish != null && !cocktail.garnish.equals("")) {
            displayedRecipe += "\n" + cocktail.garnish + "\n";
        }
        if (cocktail.year != null) {
            displayedRecipe += "\n" + cocktail.year.toString() + "\n";
        }
        if (cocktail.other != null && !cocktail.other.equals("")) {
            displayedRecipe += "\n" + cocktail.other + "\n";
        }

        recipeView.setText(displayedRecipe);

        return view;

    }
}
