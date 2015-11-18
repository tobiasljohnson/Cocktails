package com.morecocktails.cocktails;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by tobiasljohnson on 8/8/15.
 */
public class RecipePagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Cocktail> cocktails;
    public RecipePagerAdapter(FragmentManager fm, ArrayList<Cocktail> cocktails) {
        super(fm);
        this.cocktails = cocktails;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment  = new RecipeFragment();
        Bundle args = new Bundle();
        args.putParcelable("cocktail", cocktails.get(position));
        fragment.setArguments(args);
        return fragment;
    }

/*    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }*/
    @Override
    public int getCount() {
        return cocktails.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return cocktails.get(position).name;
    }
}
