package com.example.palexis3.nytimes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.palexis3.nytimes.EndlessRecyclerViewScrollListener;
import com.example.palexis3.nytimes.ItemClickSupport;
import com.example.palexis3.nytimes.R;
import com.example.palexis3.nytimes.adapters.ArticlesRecyclerAdapter;
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

    Toolbar toolbar;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;


    ArrayList<Article> articles;
    RecyclerView rvArticles;
    ArticlesRecyclerAdapter recAdapter;

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

        //lookup recycleview in activity layout
        rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
        articles = new ArrayList<>();

        recAdapter = new ArticlesRecyclerAdapter(this, articles);

        //attach adapter to recycleview to populate items
        rvArticles.setAdapter(recAdapter);

        //first param in num of columns and second is orientation
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);

        //set layout manager to position the items
        rvArticles.setLayoutManager(gridLayoutManager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //allow icons in toolbar to be clickable
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //implements endless pagination
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextDataFromApi(page);
            }
        };

        // Adds the scroll listener to RecyclerView
        rvArticles.addOnScrollListener(scrollListener);

        recyclerViewsListener();
    }


    private void recyclerViewsListener() {

        ItemClickSupport.addTo(rvArticles).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // call intent
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                //get the article to display
                Article article = (Article) articles.get(position);
                //pass in that article into intent
                i.putExtra("article", article);
                //launch the activity
                startActivity(i);
            }
        });

    }

    // Append the next page of data into the adapter
    // This method sends out a network request and appends new data items to adapter.
    public void loadNextDataFromApi(int offset) {

        //getting the page to start from
        String page = String.valueOf(offset);
        client = new NYTimesSearchClient();
        client.pageParam(page);

            client.getArticles(searchQuery, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray articleJsonResults = null;

                    try {
                        articleJsonResults = response.getJSONObject("response").getJSONArray("docs");

                        // record this value before making any changes to the existing list
                        int curSize = recAdapter.getItemCount();

                        ArrayList<Article> temp = Article.fromJSONArray(articleJsonResults);

                        //update the existing list
                        articles.addAll(temp);

                        //notify adapter that items have been changes
                        recAdapter.notifyItemRangeChanged(curSize, temp.size());

                        //Log.d("DEBUG", recAdapter.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    Log.d("Failed: ", "" + statusCode);
                    Log.d("Error : ", "" + errorResponse);
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
         client.beginDateParam(beginDate);
        }

        //calling spinner order func in client
        if(sortOrder != null) {
            client.sortOrderParam(sortOrder);
        }

        //calling the checkbox func in client
        if(newsDeskList != null) {
            client.deskValuesParam(newsDeskList);
        }

        client.getArticles(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");

                    // record this value before making any changes to the existing list
                    int curSize = recAdapter.getItemCount();

                    ArrayList<Article> temp = Article.fromJSONArray(articleJsonResults);

                    //update the existing list
                    articles.addAll(temp);

                    //notify adapter that items have been changes
                    recAdapter.notifyItemRangeChanged(curSize, temp.size());

                    //Log.d("DEBUG", recAdapter.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("Failed: ", ""+ statusCode);
                Log.d("Error : ", "" + errorResponse);
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
