package com.morecocktails.cocktails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Ingredients extends ArrayList<Ingredient> {
    private Map<String, LinkedList<Ingredient> > lookupTable;
    private boolean validLookupTable;

    private void createLookupTable() {
        lookupTable = new HashMap<String, LinkedList<Ingredient>>();
        for (Ingredient i : this) {
            LinkedList<Ingredient> l = lookupTable.get(i.fullName);
            if (l == null) {
                l = new LinkedList<Ingredient>();
                lookupTable.put(i.fullName, l);
            }
            l.addFirst(i);
        }
        validLookupTable = true;
    }

    public Ingredients() {
        super();
        lookupTable = null;
        validLookupTable = false;
    }

    @Override
    public boolean add(Ingredient i) {
        super.add(i);
        if (validLookupTable) {
            LinkedList<Ingredient> l = lookupTable.get(i.fullName);
            if (l == null) {
                l = new LinkedList<Ingredient>();
                lookupTable.put(i.fullName, l);
            }
            l.addFirst(i);
        } else {
            createLookupTable();
        }
        return true;
    }

    public Ingredient findIngredient(String ingredientName) {
        if (!validLookupTable) {
            createLookupTable();
        }
        List<Ingredient> l = lookupTable.get(ingredientName);
        if (l != null) {
            for (Ingredient i : l) {
                if (i.fullName.equals(ingredientName)) {
                    return i;
                }
            }
        }
        return null;
    }

   /* public Ingredient findIngredient(String ingredientName, Integer ingredientAlternateID) { //throws...
        if (ingredientAlternateID == null) {
            return findIngredient(ingredientName);
        }

        if (!validLookupTable) {
            createLookupTable();
        }
        List<Ingredient> l = lookupTable.get(ingredientName);
        if (l != null) {
            for (Ingredient i : l) {
                if (i.fullName.equals(ingredientName) &&
                        i.alternateID != null &&
                        i.alternateID.equals(ingredientAlternateID)) {
                    return i;
                }
            }
        }
        return null;
    }*/

}
