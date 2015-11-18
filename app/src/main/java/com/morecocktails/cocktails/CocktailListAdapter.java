package com.morecocktails.cocktails;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tobiasljohnson on 6/27/15.
 */
public class CocktailListAdapter extends BaseAdapter {
    private ArrayList<Cocktail> cocktails;
    private LayoutInflater mInflater;

    public ArrayList<Cocktail> getCocktails() {
        return cocktails;
    }

    @Override
    public int getCount() {
        return cocktails.size();
    }

    @Override
    public Cocktail getItem(int position) {
        return cocktails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(android.R.layout.simple_list_item_1, null);
        }
        Cocktail cocktail = getItem(position);
        SpannableString titleBuilder = new SpannableString(cocktail.name + " " +cocktail.recipeSummary());
        titleBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, cocktail.name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((TextView) convertView).setText(titleBuilder);
        return convertView;
    }

    public CocktailListAdapter(Context context, LayoutInflater inflater, ArrayList<Cocktail> cocktails) {
        this.cocktails = cocktails;
        this.mInflater = inflater;
    }

    public void replaceCocktails(ArrayList<Cocktail> cocktails) {
        this.cocktails = cocktails;
    }
}
