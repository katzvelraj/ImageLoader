package com.search.velrajp.flickrsearch.datamanager;

import com.search.velrajp.flickrsearch.model.SearchResponse;

import io.reactivex.Observable;

public interface DataMangerContract {
    Observable<SearchResponse> getFirstPageData(int page, String searchKeyWord);
}
