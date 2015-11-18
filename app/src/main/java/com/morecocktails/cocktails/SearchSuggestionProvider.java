package com.morecocktails.cocktails;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by tobiasljohnson on 7/25/15.
 */
public class SearchSuggestionProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
//        Cocktails cocktails = MainActivity.getCocktails(getContext());
/*        for (Cocktail cocktail : cocktails.cocktails) {
            cocktailNames.add(cocktail.name);
        }*/
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cocktails cocktails = MainActivity.getCocktailsIfAvailable();
        String fullQuery = uri.getLastPathSegment();
        int lastComma = fullQuery.lastIndexOf(',');
        boolean hasComma = (lastComma != -1);
        int firstChar = lastComma + 1;
        while (firstChar < fullQuery.length()
                && (fullQuery.charAt(firstChar) == ' ' || fullQuery.charAt(firstChar) == ',')) {
            ++firstChar;
        }

        String query;
        query = fullQuery.substring(firstChar).trim().toLowerCase();
        MatrixCursor cursor = new MatrixCursor(
                new String[]{BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_TEXT_2,
                        SearchManager.SUGGEST_COLUMN_INTENT_ACTION,
                        SearchManager.SUGGEST_COLUMN_QUERY,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA});

        if (query.isEmpty() || cocktails == null) {
            return cursor;
        }

        ArrayList<Ingredient> startHitsIngredient = new ArrayList<>();
        ArrayList<Ingredient> startWordHitsIngredient = new ArrayList<>();
        ArrayList<Ingredient> otherHitsIngredient = new ArrayList<>();
        ArrayList<Cocktail> startHitsCocktail = new ArrayList<>();
        ArrayList<Cocktail> startWordHitsCocktail = new ArrayList<>();
        ArrayList<Cocktail> otherHitsCocktail = new ArrayList<>();


        for (Ingredient ingredient : cocktails.ingredients) {
            if (ingredient.startMatches(query)) {
                startHitsIngredient.add(ingredient);
            } else if (ingredient.startWordMatches(query)) {
                startWordHitsIngredient.add(ingredient);
            } else if (ingredient.anyMatches(query)) {
                otherHitsIngredient.add(ingredient);
            }
        }
        Collections.sort(startHitsIngredient);
        Collections.sort(startWordHitsIngredient);
        Collections.sort(otherHitsIngredient);
        if (!hasComma) {
            for (Cocktail cocktail : cocktails.cocktails) {
                if (cocktail.startMatches(query)) {
                    startHitsCocktail.add(cocktail);
                } else if (cocktail.startWordMatches(query)) {
                    startWordHitsCocktail.add(cocktail);
                } else if (cocktail.anyMatches(query)) {
                    otherHitsCocktail.add(cocktail);
                }
            }
            Collections.sort(startHitsCocktail);
            Collections.sort(startWordHitsCocktail);
            Collections.sort(otherHitsCocktail);
        }

        int i = 0;
        for (Ingredient ingredient : startHitsIngredient) {
            cursor.addRow(new Object[]{i, ingredient.fullName, null, Intent.ACTION_SEARCH,
                    fullQuery.substring(0, firstChar) + ingredient.fullName+", ", ingredient.fullName});
            ++i;
        }
        for (Cocktail cocktail : startHitsCocktail) {
            cursor.addRow(new Object[]{i, cocktail.name, cocktail.recipeSummary(), Intent.ACTION_VIEW, null, cocktail.name});
            ++i;
        }
        for (Ingredient ingredient : startWordHitsIngredient) {
            cursor.addRow(new Object[]{i, ingredient.fullName, null, Intent.ACTION_SEARCH,
                    fullQuery.substring(0, firstChar) + ingredient.fullName+", ", ingredient.fullName});
            ++i;
        }
        for (Cocktail cocktail : startWordHitsCocktail) {
            cursor.addRow(new Object[]{i, cocktail.name, cocktail.recipeSummary(), Intent.ACTION_VIEW, null, cocktail.name});
            ++i;
        }
        for (Ingredient ingredient : otherHitsIngredient) {
            cursor.addRow(new Object[]{i, ingredient.fullName, null, Intent.ACTION_SEARCH,
                    fullQuery.substring(0, firstChar) + ingredient.fullName+", ", ingredient.fullName});
            ++i;
        }
        for (Cocktail cocktail : otherHitsCocktail) {
            cursor.addRow(new Object[]{i, cocktail.name, cocktail.recipeSummary(), Intent.ACTION_VIEW, null, cocktail.name});
            ++i;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
