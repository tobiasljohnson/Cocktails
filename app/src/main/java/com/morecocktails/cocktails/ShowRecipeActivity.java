package com.morecocktails.cocktails;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tobiasljohnson on 6/19/15.
 */
public class ShowRecipeActivity extends AppCompatActivity {
    TextView recipeView;
    ViewPager recipePager;
    RecipePagerAdapter recipePagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ArrayList<Cocktail> cocktails = getIntent().getParcelableArrayListExtra("com.morecocktails.cocktails.cocktails");
        int position = getIntent().getIntExtra("com.morecocktails.cocktails.position", 0);

        recipePagerAdapter = new RecipePagerAdapter(getSupportFragmentManager(), cocktails);
        recipePager = (ViewPager) findViewById(R.id.recipe_pager);
        recipePager.setAdapter(recipePagerAdapter);
        recipePager.setCurrentItem(position);
        getSupportActionBar().setTitle(recipePagerAdapter.getPageTitle(position));
        recipePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(recipePagerAdapter.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
