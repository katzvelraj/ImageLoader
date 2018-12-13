package com.search.velrajp.flickrsearch.presenter;


import com.search.velrajp.flickrsearch.model.PhotoItem;

import java.util.List;

public interface PresenterContract {
    public interface MainActivityView {

        void showError();

        void hideErrorView();

        void addAllPhotos(List<PhotoItem> photoItems);

        void addLoadingFooter();

        void hideProgress();

        void showErrorView(Throwable e);

        void showRetry(Throwable e);

        void removeLoadingFooter();

        void showProgress();

    }

    public interface Presenter {

        void loadFirstPage(String word);

        void loadNextPage(String searchWord);


        void loadMoreItems();

        int getTotalPageCount();

        boolean getIsLastPage();

        boolean getIsLoading();


    }
}
