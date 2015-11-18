package com.morecocktails.cocktails;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.convert.Convert;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

@Root(name="entry")
public class RecipeEntry implements Parcelable {
    private boolean resolved = false;
    private Ingredient resolution = null;
    
    @Attribute(name="ingredient")
    private String ingredientName;

//    @Attribute(name="alternate-id", required=false)
//    private Integer ingredientAlternateID;
    
    @Attribute(required=false)
    public String brand;

    @Text(required=false)
    public String amount;


    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
        resolved = false;
    }

 /*   public Integer getIngredientAlternadeID() {
        return ingredientAlternateID;
    }

    public void setIngredientAlternateID(Integer ingredientAlternateID) {
        this.ingredientAlternateID = ingredientAlternateID;
        resolved = false;
    }*/
    
    public RecipeEntry() {
        this.amount = "";
        this.brand = null;
        this.ingredientName = "";
     //   this.ingredientAlternateID = null;
    }

    public RecipeEntry(String amount, String ingredientName)
    {
        this.amount = amount;
        this.ingredientName = ingredientName;
        this.brand = null;
       // this.ingredientAlternateID = null;
    }
    public RecipeEntry(String amount, String ingredientName, String brand)
    {
        this.amount = amount;
        this.ingredientName = ingredientName;
        this.brand = brand;
     //   this.ingredientAlternateID = null;
    }

    public void resolve(Ingredients ingredients) { //throws...
        if (amount == null) {
            amount = "";
        }
        resolution = ingredients.findIngredient(ingredientName);//, ingredientAlternateID);
        if (resolution != null) {
            resolved = true;
        } else {
            resolved = false;
            // throw exception here
        }
    }

    public boolean isResolved() {
        return resolved;
    }

    public Ingredient getIngredient() {
        if (isResolved()) {
            return resolution;
        } else {
            return null;
        }
    }

    public boolean matchesIngredient(String search) {
        if (ingredientName.toLowerCase().contains(search.toLowerCase())) {
            return true;
        }
        if (brand != null && brand.toLowerCase().contains(search.toLowerCase())) {
            return true;
        }
        if (isResolved()) {
            Ingredient i = getIngredient();
            if (i.shortName != null && i.shortName.toLowerCase().contains(search.toLowerCase())) {
                return true;
            }
            if (i.searchTerms != null && i.searchTerms.toLowerCase().contains(search.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amount);
        dest.writeValue(brand);
        dest.writeString(ingredientName);
        //dest.writeValue(ingredientAlternateID);
        if (isResolved()) {
            dest.writeInt(1);
            resolution.writeToParcel(dest, 0);
        } else {
            dest.writeInt(0);
        }
    }

    private RecipeEntry(Parcel in) {
        amount = in.readString();
        brand = (String)in.readValue(null);
        ingredientName = in.readString();
     //   ingredientAlternateID = (Integer)in.readValue(null);
        resolved = (in.readInt() == 1);
        if (resolved) {
            resolution = Ingredient.CREATOR.createFromParcel(in);
        } else {
            resolution = null;
        }
    }

    public static final Parcelable.Creator<RecipeEntry> CREATOR = new Parcelable.Creator<RecipeEntry>() {
        public RecipeEntry createFromParcel(Parcel in) {
            return new RecipeEntry(in);
        }

        public RecipeEntry[] newArray(int size) {
            return new RecipeEntry[size];
        }
    };

}

