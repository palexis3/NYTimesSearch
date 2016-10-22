package com.example.palexis3.nytimes.clients;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


//Responsible for executing the API requests and retrieving the JSON
public class NYTimesSearchClient {
    private AsyncHttpClient client;
    private final String API_KEY = "a0d936f8e6304a8393cd064c16a648c8";
    private final String API_BASE_URL = "https://api.nytimes.com/";
    private RequestParams params = new RequestParams();

    public NYTimesSearchClient(){
        this.client = new AsyncHttpClient();
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;
    }

    //method for accessing the NYTIMES api  within our client by executing an asynchronous request to the appropriate endpoint:
    public void getArticles(String query, JsonHttpResponseHandler handler) {
        String url = getApiUrl("svc/search/v2/articlesearch.json");

        params.add("api-key", API_KEY);
        params.add("page", "0");
        params.add("q", query);

        try {
            client.get(url, params, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
