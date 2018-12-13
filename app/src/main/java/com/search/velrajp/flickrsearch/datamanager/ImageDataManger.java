package com.search.velrajp.flickrsearch.datamanager;

import com.search.velrajp.flickrsearch.api.SearchService;
import com.search.velrajp.flickrsearch.model.SearchResponse;


import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageDataManger implements DataMangerContract {

    @Override
    public Observable<SearchResponse> getFirstPageData(final int currentPage, final String searchKeyword) {

        return Observable.create(new ObservableOnSubscribe<SearchResponse>() {
            @Override
            public void subscribe(final ObservableEmitter<SearchResponse> subscriber) throws Exception {


                Call<SearchResponse> searchResponseCall = SearchService.getSearchService().getImages(searchKeyword, currentPage);
                searchResponseCall.enqueue(new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                        if (response.isSuccessful()) {
                            subscriber.onNext(response.body());
                        }
                        subscriber.onComplete();
                    }

                    @Override
                    public void onFailure(Call<SearchResponse> call, Throwable t) {
                        subscriber.onError(t);
                        subscriber.onComplete();
                    }
                });


            }
        });
}

}
