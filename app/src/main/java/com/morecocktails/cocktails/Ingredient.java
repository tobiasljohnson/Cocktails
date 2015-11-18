package com.morecocktails.cocktails;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.Attribute;

@Root
public class Ingredient implements Parcelable, Comparable<Ingredient> {
    
    @Attribute(name="fullname")
    public String fullName;

    @Attribute(name="shortname", required=false)
    public String shortName;
    
    @Attribute(name="searchterms", required=false)
    public String searchTerms;

  //  @Attribute(name="alternate-id", required=false)
  //  public Integer alternateID;

    public Ingredient(String fullName, String shortName, String searchTerms) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.searchTerms = searchTerms;
    //    this.alternateID = null;
    }
    /*public Ingredient(String fullName, String shortName, String searchTerms, int alternateID) {
        this.shortName = shortName;
        this.fullName = fullName;
        this.searchTerms = searchTerms;
        this.alternateID = new Integer(alternateID);
    }*/
    public Ingredient() {
        this.shortName = null;
        this.fullName = "";
        this.searchTerms = null;
       // this.alternateID = null;
    }

    /**
     * Return true if the query matches a prefix of the full or short name.
     */
    public boolean startMatches(String query) {
        query = query.toLowerCase();
        if (fullName.toLowerCase().startsWith(query) ||
                (shortName != null && shortName.toLowerCase().startsWith(query))) {
            return true;
        }
        return false;
    }

    /**
     * Return true if the query matches the beginning of a word in the full or short name
     * or the search terms.
     */
    public boolean startWordMatches(String query) {
        query = query.toLowerCase();

        String[] fullWords = fullName.toLowerCase().split("\\s+");
        for (String s : fullWords) {
            if (s.startsWith(query)) {
                return true;
            }
        }

        if (shortName != null) {
            String[] shortWords = shortName.toLowerCase().split("\\s+");
            for (String s : shortWords) {
                if (s.startsWith(query)) {
                    return true;
                }
            }
        }
        if (searchTerms != null) {
            String[] searchWords = searchTerms.toLowerCase().split("\\s+");
            for (String s : searchWords) {
                if (s.startsWith(query)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean anyMatches(String query) {
        query = query.toLowerCase();
        if (fullName.toLowerCase().contains(query)) {
            return true;
        }
        if (shortName != null && shortName.toLowerCase().contains(query)) {
            return true;
        }
        if (searchTerms != null && searchTerms.toLowerCase().contains(query)) {
            return true;
        }
        return false;
    }
    @Override
    public int compareTo(Ingredient another) {
        return fullName.compareTo(another.fullName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fullName);
        dest.writeValue(shortName);
        dest.writeValue(searchTerms);
     //   dest.writeValue(alternateID);
    }
    public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        public Ingredient createFromParcel(Parcel in) {
            Ingredient i = new Ingredient();
            i.fullName = in.readString();
            i.shortName = (String)in.readValue(null);
            i.searchTerms = (String)in.readValue(null);
       //     i.alternateID = (Integer)in.readValue(null);
            return i;
        }

        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

}
