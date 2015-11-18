package com.morecocktails.cocktails;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static Cocktails cocktails;
    private static CocktailListAdapter cocktailListAdapter;

    public static void showDebugDialog(String message, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    /**
     * Return the shared cocktail database, loading it if it doesn't exist yet.
     * @return the cocktails database
     */
    static Cocktails getCocktails(Context context) {
        if (MainActivity.cocktails == null) {
            MainActivity.cocktails = MainActivity.loadCocktails(context);
            CocktailListAdapter adapter = MainActivity.getCocktailListAdapter(context);
            adapter.replaceCocktails(MainActivity.cocktails.cocktails);
            adapter.notifyDataSetChanged();
        }
        return MainActivity.cocktails;
    }

    static Cocktails getCocktailsIfAvailable() {
        return MainActivity.cocktails;
    }

    private static CocktailListAdapter getCocktailListAdapter(Context context) {
        if (MainActivity.cocktailListAdapter == null) {
            if (MainActivity.cocktails == null) {
                MainActivity.getCocktails(context); // this will update MainActivity.cocktailListAdapter
            } else {
                MainActivity.cocktailListAdapter = new CocktailListAdapter(context,
                        LayoutInflater.from(context),
                        MainActivity.cocktails.cocktails);
            }
        }
        return MainActivity.cocktailListAdapter;
    }

    private static Cocktails loadDefaultCocktails(Context context) {
        Cocktails newCocktails;
        Serializer serializer = new Persister();
        try {
            InputStream is = context.getResources().openRawResource(R.raw.sample_library);
            newCocktails = serializer.read(Cocktails.class, is);
            is.close();
        } catch (Exception unexpected) {
            //Log.e("can't load sample library", unexpected.toString());
            newCocktails = new Cocktails();
        }
        Collections.sort(newCocktails.cocktails);
        newCocktails.resolve();
        return newCocktails;
    }


    /**
     * Reload cocktails from "library.xml" and resolve their ingredients. Return result, never null.
     */
    private static Cocktails loadCocktails(Context context) {
        Cocktails newCocktails;
        Serializer serializer = new Persister();
        try {
            FileInputStream fis = context.openFileInput("library.xml");
            newCocktails = serializer.read(Cocktails.class, fis);
            newCocktails.resolve();
            fis.close();
        } catch (Exception e) {
            //Log.w("library.xml not found", e.toString());
            newCocktails = MainActivity.loadDefaultCocktails(context);
        }
        return newCocktails;
    }


    /**
     * Writes the current cocktail database to library.xml.
     */
    public static void saveCocktails(Context context) {
        Serializer serializer = new Persister();
        try {
            FileOutputStream fos = context.openFileOutput("library.xml", MODE_PRIVATE);
            serializer.write(MainActivity.cocktails, fos);
            fos.close();
        } catch (Exception e) {
            //Log.w("library.xml not written", e.toString());
        }

    }

    public static void notifyChangedCocktails(Context context) {
        MainActivity.getCocktailListAdapter(context).notifyDataSetChanged();
    }

    private ListView cocktailListView;
    private CocktailListAdapter searchListAdapter;
    private boolean showingSearchResults = false;

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            handleSearch(intent);
        } else {
            if (Intent.ACTION_VIEW.equals(intent.getAction())) {
                String selected = intent.getData().getLastPathSegment();
                Cocktail cocktail = getCocktails(this).lookupCocktail(selected);
                if (cocktail != null) {
                    ArrayList<Cocktail> selectedCocktail = new ArrayList<>();
                    selectedCocktail.add(cocktail);
                    Intent showRecipeIntent = new Intent(this, ShowRecipeActivity.class);
                    showRecipeIntent.putExtra("com.morecocktails.cocktails.position", 0)
                            .putParcelableArrayListExtra("com.morecocktails.cocktails.cocktails", selectedCocktail);
                    startActivity(showRecipeIntent);

                }
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        MainActivity.getCocktails(this);

        cocktailListView = (ListView) findViewById(R.id.cocktail_listview);
        cocktailListView.setAdapter(MainActivity.getCocktailListAdapter(this));
        cocktailListView.setOnItemClickListener(this);

        handleIntent(getIntent());
    }


    private void showSearchResults(ArrayList<Cocktail> searchResults) {
        searchListAdapter = new CocktailListAdapter(this,
                getLayoutInflater(),
                searchResults);
        cocktailListView.setAdapter(searchListAdapter);
        showingSearchResults = true;
        TextView noResultsView = (TextView) findViewById(R.id.no_results_view);
        if (searchResults.isEmpty()) {
            noResultsView.setVisibility(View.VISIBLE);
        } else {
            noResultsView.setVisibility(View.GONE);
        }

    }

    private void exitSearchResults() {
        if (showingSearchResults) {
            cocktailListView.setAdapter(MainActivity.getCocktailListAdapter(this));
            showingSearchResults = false;
        }
        TextView noResultsView = (TextView) findViewById(R.id.no_results_view);
        noResultsView.setVisibility(View.GONE);

    }

    private CocktailListAdapter getCurrentCocktailListAdapter() {
        if (showingSearchResults) {
            return searchListAdapter;
        } else {
            return MainActivity.getCocktailListAdapter(this);
        }
    }

    private void handleSearch(Intent intent) {
        String query = intent.getStringExtra(SearchManager.QUERY);
        EditText editText = (EditText)findViewById(R.id.search_src_text);
        if (editText != null) {
            editText.setText(query);
            editText.setSelection(query.length());
            if (query.matches(".*,\\s*\\z")) {
                SearchView searchView = (SearchView) findViewById(R.id.action_search);
                searchView.requestFocus();
//                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        }
        Cocktails cocktails = MainActivity.getCocktails(this);
        ArrayList<Cocktail> results;
        if (query.contains(",")) { //comma indicates searching for ingredients
            String[] tokenized = query.split("\\s*,\\s*");
            results = cocktails.ingredientsAllSearch(query.split("\\s*,\\s*"));
        } else {
            results = cocktails.generalSearch(query);
        }
        showSearchResults(results);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setIconified(false);
//        EditText editText = (EditText)findViewById(R.id.search_src_text);
 //       editText.setTextColor(R.color.red);

        searchView.clearFocus();
        final ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setVisibility(View.GONE);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitSearchResults();
                EditText editText = (EditText)findViewById(R.id.search_src_text);
                editText.setText("");
                searchView.setQuery("", false);
                searchView.clearFocus();
                closeButton.setVisibility(View.GONE);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    exitSearchResults();
                }
                return false;
            }
        });

        return true;
    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.reset_cocktails:
                Log.d("reset", "resetting cocktails");
                MainActivity.cocktails = MainActivity.loadDefaultCocktails(this);
                MainActivity.getCocktailListAdapter(this).replaceCocktails(MainActivity.cocktails.cocktails);
                MainActivity.notifyChangedCocktails(this);
                MainActivity.saveCocktails(this);
                break;
        }

        return super.onOptionsItemSelected(item);
    }  */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CocktailListAdapter currentAdapter = getCurrentCocktailListAdapter();
        ArrayList<Cocktail> currentCocktails = currentAdapter.getCocktails();
        Intent showRecipeIntent = new Intent(this, ShowRecipeActivity.class);
        showRecipeIntent.putExtra("com.morecocktails.cocktails.position", position)
            .putParcelableArrayListExtra("com.morecocktails.cocktails.cocktails", currentCocktails);
        startActivity(showRecipeIntent);
    }
}
