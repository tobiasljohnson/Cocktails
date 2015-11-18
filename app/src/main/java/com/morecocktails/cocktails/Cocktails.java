package com.morecocktails.cocktails;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

@Root
public class Cocktails {

    @ElementList(inline=true, required=false)
    public ArrayList<Cocktail> cocktails;

    @ElementList(type=Ingredient.class, inline=true, required=false)
    public Ingredients ingredients;

    public Cocktails() {
        cocktails = new ArrayList<Cocktail>();
        ingredients = new Ingredients();
    }

    public Cocktails (ArrayList<Cocktail> cocktails, Ingredients ingredients) {
        this.cocktails = cocktails;
        this.ingredients = ingredients;
    }

    /**
     * Return a cocktail with a given name or null if it doesn't exist. Must match exactly.
     * @param name
     * @return
     */
    public Cocktail lookupCocktail(String name) {
        for (Cocktail cocktail : cocktails) {
            if (cocktail.name.equals(name)) {
                return cocktail;
            }
        }
        return null;
    }
    public void resolve() { //throws
        for (Cocktail cocktail : cocktails) {
            cocktail.resolve(ingredients);
        }
    }

    public void addCocktails(Cocktails newCocktails) {
        for (Cocktail cocktail : newCocktails.cocktails) {
            cocktails.add(cocktail);
        }
        Collections.sort(cocktails);
        for (Ingredient ingredient : newCocktails.ingredients) {
            if (ingredients.findIngredient(ingredient.fullName) == null) {
                ingredients.add(ingredient);
            } //should probably do something more complicated than this!
        }
    }

    public Cocktails nameSearch(String searchText) {
        Cocktails results = new Cocktails(new ArrayList<Cocktail>(), ingredients);
        for (Cocktail cocktail : cocktails) {
            if (cocktail.name.toLowerCase().contains(searchText.toLowerCase())) {
                results.cocktails.add(cocktail);
            }
        }
        return results;
    }

    public ArrayList<Cocktail> generalSearch(String searchText) {
        ArrayList<Cocktail> results = new ArrayList<>();
        for (Cocktail cocktail : cocktails) {
            if (cocktail.name.toLowerCase().contains(searchText.toLowerCase()) ||
                    cocktail.matchesIngredient(searchText)) {
                results.add(cocktail);
            }
        }
        return results;
    }

    public ArrayList<Cocktail> ingredientsAllSearch(String[] ingredientStrings) {
        ArrayList<Cocktail> results = new ArrayList<Cocktail>();
        for (Cocktail cocktail : cocktails) {
            boolean matches = true;
            for (String searchString : ingredientStrings) {
                if (!cocktail.matchesIngredient(searchString)) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                results.add(cocktail);
            }
        }
        return results;
    }

}
