package com.morecocktails.cocktails;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Root
public class Cocktail implements Parcelable, Comparable<Cocktail> {

    @Attribute
    public String name;
    
    @Element(required=false)
    public String instructions;
    
    @Element(required=false)
    public String garnish;
    
    @Element(required=false)
    public Integer year;
    
    @Element(required=false)
    public String other;

    @ElementList
    public ArrayList<RecipeEntry> recipe;

    public Cocktail() {
        this.name = "";
        this.instructions = null;
        this.garnish = null;
        this.year = null;
        this.other = null;
        this.recipe = new ArrayList<RecipeEntry>();
    }

    /**
     * Return true if the cocktail contains an ingredient marching the search term.
     */
    public boolean matchesIngredient(String search) {
        for (RecipeEntry entry : recipe) {
            if (entry.matchesIngredient(search)) {
                return true;
            }
        }
        return false;
    }

    public String recipeSummary() {
        Set<String> ingredients = new LinkedHashSet<>();

        for (int i = 0; i < recipe.size(); ++i) {
            RecipeEntry entry = recipe.get(i);
            String ing;
            if (entry.isResolved()) {
                ing = entry.getIngredient().shortName;
            } else {
                ing = entry.getIngredientName();
            }
            ingredients.add(ing);
        }

        String summary = new String();
        int i = 0;
        for (String ing : ingredients) {
            summary += ing;
            if (i < ingredients.size() - 1) {
                summary += ", ";
            }
            ++i;
        }
        return summary;
    }

    /**
     * Return true if the query matches a prefix of the cocktail name.
     */
    public boolean startMatches(String query) {
        query = query.toLowerCase();
        if (name.toLowerCase().startsWith(query)) {
            return true;
        }
        return false;
    }

    /**
     * Return true if the query matches the beginning of a word in cocktail name.
     */
    public boolean startWordMatches(String query) {
        query = query.toLowerCase();

        String[] nameWords = name.toLowerCase().split("\\s*");
        for (String s : nameWords) {
            if (s.startsWith(query)) {
                return true;
            }
        }
        return false;
    }

    public boolean anyMatches(String query) {
        query = query.toLowerCase();
        if (name.toLowerCase().contains(query)) {
            return true;
        }
        return false;
    }

    public void resolve(Ingredients ingredients) { //throws...
        for (RecipeEntry entry : recipe) {
            entry.resolve(ingredients);
        }
    }

    @Override
    public int compareTo(Cocktail another) {
        return name.compareTo(another.name);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeValue(instructions);
        dest.writeValue(garnish);
        dest.writeValue(year);
        dest.writeValue(other);
        dest.writeTypedList(recipe);
    }

    public static final Parcelable.Creator<Cocktail> CREATOR = new Parcelable.Creator<Cocktail>() {
        public Cocktail createFromParcel(Parcel in) {
            Cocktail cocktail = new Cocktail();
            cocktail.name = in.readString();
            cocktail.instructions = (String)in.readValue(null);
            cocktail.garnish = (String)in.readValue(null);
            cocktail.year = (Integer)in.readValue(null);
            cocktail.other = (String)in.readValue(null);
            in.readTypedList(cocktail.recipe, RecipeEntry.CREATOR);
            return cocktail;
        }

        public Cocktail[] newArray(int size) {
            return new Cocktail[size];
        }
    };

}
