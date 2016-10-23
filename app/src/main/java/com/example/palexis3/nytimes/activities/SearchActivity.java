package com.example.palexis3.nytimes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.palexis3.nytimes.R;
import com.example.palexis3.nytimes.adapters.ArticleArrayAdapter;
import com.example.palexis3.nytimes.clients.NYTimesSearchClient;
import com.example.palexis3.nytimes.fragments.FilterDialogFragment;
import com.example.palexis3.nytimes.models.Article;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;





// Responsible for fetching and deserializing the data and configuring the adapter
public class SearchActivity extends AppCompatActivity implements FilterDialogFragment.onFilterSelectedListener {

    GridView gvResults;
    Toolbar toolbar;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    NYTimesSearchClient client;

    String searchQuery; //holds query that user passes into searchview
    String beginDate;
    String sortOrder;
    ArrayList<String> newsDeskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpViews();
    }

    public void setUpViews() {
        gvResults = (GridView) findViewById(R.id.gvResults);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //allow icons in toolbar to be clickable
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        gridViewListener();
    }


    public void gridViewListener() {
        //bind listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create an intent to display the article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                //get the article to display
                Article article = (Article) parent.getItemAtPosition(position);
                //pass in that article into intent
                i.putExtra("article", article);
                //launch the activity
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //fetching articles
                fetchArticles(query);
                //making query value accessible to other functions
                searchQuery = query;
                //workaround to avoid issues with some devices
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings :
                return true;
            case R.id.filter :
                //start the filter dialog
                showFilterDialog();
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }


    //fetch articles from nytimes endpoint with string query being passed in
    private void fetchArticles(String query) {
        client = new NYTimesSearchClient();

        //calling begin date func in client
        //check if string is a legit number also
        if(beginDate != null && beginDate.matches("^\\d{8}")) {
         client.getBeginDate(beginDate);
        }

        //calling spinner order func in client
        if(sortOrder != null) {
            client.getSortOrder(sortOrder);
        }

        //calling the checkbox func in client
        if(newsDeskList != null) {
            client.getDeskValues(newsDeskList);
        }

        client.getArticles(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    //remove all articles from the adapter
                    adapter.clear();
                    //Load article objects into the adapter
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    Log.d("DEBUG", adapter.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("Failed: ", ""+ statusCode);
                Log.d("Error : ", "" + errorResponse.toString());
            }
        });

      }


    //dialog interface method to receive filter info
    public void onFilterSelected(String date, String order, ArrayList<String> list) {

        beginDate = date;
        if(list.size() > 0) {
            newsDeskList = list;
        }else {
            newsDeskList = null;
        }
        sortOrder = order;

        //run main fetching call
        fetchArticles(searchQuery);
    }

    //method to call filter dialog
    private void showFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterDialogFragment dialog = FilterDialogFragment.newInstance("Filter");
        dialog.show(fm, "fragment_filter");
    }

}
