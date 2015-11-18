package com.morecocktails.cocktails;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class AddCocktailsActivity extends ActionBarActivity  {

    private ListView cocktailListView;
    private Button cancelButton;
    private Button addButton;
    private Cocktails toAdd;

    private void displayCocktails() {
        CocktailListAdapter cocktailListAdapter = new CocktailListAdapter(this,
                getLayoutInflater(),
                toAdd.cocktails);
        cocktailListView.setAdapter(cocktailListAdapter);
    }
    private Cocktails getCocktailsToAdd(InputStream in) {
        Cocktails cocktails;
        if (in == null) {
            return new Cocktails();
        }

        try {
            Strategy strategy = new AnnotationStrategy();
            Serializer serializer = new Persister(strategy);
            cocktails = serializer.read(Cocktails.class, in);
        } catch (Exception e) {
            //Log.e("ioerror", e.toString());
            cocktails = new Cocktails();
        }
        return cocktails;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cocktails);
        cocktailListView = (ListView) findViewById(R.id.add_cocktail_listview);
        cancelButton = (Button) findViewById(R.id.cancel_add_button);
        addButton = (Button) findViewById(R.id.add_button);


        Intent intent = getIntent();
        Uri cocktailFile = intent.getData();
        if (intent.getScheme().equals("http") || intent.getScheme().equals("https")) {
            AsyncHttpClient client = new AsyncHttpClient();
            //Log.d("file to open", cocktailFile.toString());
            client.get(cocktailFile.toString(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    toAdd = getCocktailsToAdd(new ByteArrayInputStream(responseBody));
                    displayCocktails();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(AddCocktailsActivity.this);
                    alert.setMessage("Couldn't download file");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });
                    alert.show();
                    toAdd = new Cocktails();
                }
            });
        } else {
            InputStream stream;
            try {
                stream = getContentResolver().openInputStream(cocktailFile);
                toAdd = getCocktailsToAdd(stream);
                displayCocktails();
            } catch (FileNotFoundException e) {
                //Log.e("ioerror", e.toString());
                stream = null;
                toAdd = new Cocktails();
                AlertDialog.Builder alert = new AlertDialog.Builder(AddCocktailsActivity.this);
                alert.setMessage("Couldn't download file");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_cocktails, menu);
        return true;
    }

/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    public void cancelClick(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void addClick(View v) {
        Cocktails cocktails = MainActivity.getCocktails(this);
        cocktails.addCocktails(toAdd);
        cocktails.resolve();
        MainActivity.notifyChangedCocktails(this);
        MainActivity.saveCocktails(this);
        setResult(RESULT_OK);
        finish();
    }
}
