package com.search.velrajp.flickrsearch.api;


import com.search.velrajp.flickrsearch.model.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface FlickrSearch {
    @GET("/services/rest")
    Call<SearchResponse> getImages(@Query("text") String queryText,
                                   @Query("page") int page);
}
