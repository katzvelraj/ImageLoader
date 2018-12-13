package com.search.velrajp.flickrsearch.api;


public class SearchService {

    public static FlickrSearch getSearchService() {
        return RetrofitClient.getClient().create(FlickrSearch.class);
    }
}
