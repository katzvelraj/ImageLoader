package com.search.velrajp.flickrsearch.presenter;
import com.search.velrajp.flickrsearch.datamanager.DataMangerContract;
import com.search.velrajp.flickrsearch.model.PhotoItem;
import com.search.velrajp.flickrsearch.model.SearchResponse;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class PresenterImpl implements PresenterContract.Presenter {

    private PresenterContract.MainActivityView mView;
    private int TOTAL_PAGES = 0;
    private static final int PAGE_START = 1;

    public boolean isLastPage = false;
    public int currentPage = PAGE_START;
    public boolean isLoading = false;

    private DataMangerContract dataMangerContract;

    private CompositeDisposable mCompositeDisposable;

    @Inject
    public PresenterImpl(PresenterContract.MainActivityView view, DataMangerContract dataMangerContract) {
        this.mView = view;
        this.dataMangerContract = dataMangerContract;
        mCompositeDisposable = new CompositeDisposable();
    }


    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    @Override
    public void loadFirstPage(String word) {
        mView.showProgress();
        getCompositeDisposable().add((Disposable) dataMangerContract.getFirstPageData(currentPage, word)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver()));


    }

    @Override
    public void loadNextPage(String searchWord) {
        getCompositeDisposable().add((Disposable) dataMangerContract.getFirstPageData(currentPage, searchWord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getNextObserver()));
    }

    @Override
    public void loadMoreItems() {
        isLoading = true;
        currentPage += 1;
        loadNextPage("");
    }

    @Override
    public int getTotalPageCount() {
        return TOTAL_PAGES;
    }

    @Override
    public boolean getIsLastPage() {
        return isLastPage;
    }

    @Override
    public boolean getIsLoading() {
        return isLoading;
    }

    private DisposableObserver<SearchResponse> getNextObserver() {
        return new DisposableObserver<SearchResponse>() {
            @Override
            public void onNext(SearchResponse response) {
                processNextPage(response);
            }

            @Override
            public void onError(Throwable e) {
                mView.showRetry(e);
            }

            @Override
            public void onComplete() {
            }
        };
    }

    private DisposableObserver<SearchResponse> getObserver() {
        return new DisposableObserver<SearchResponse>() {
            @Override
            public void onNext(SearchResponse response) {
                processResponse(response);
            }

            @Override
            public void onError(Throwable e) {
                mView.showErrorView(e);
            }

            @Override
            public void onComplete() {
            }
        };
    }

    public void processNextPage(SearchResponse response) {
        if (response != null) {
            mView.removeLoadingFooter();
            isLoading = false;
            List<PhotoItem> photoItems = fetchImages(response);
            mView.addAllPhotos(photoItems);
            if (currentPage != TOTAL_PAGES) {
                mView.addLoadingFooter();
            } else {
                isLastPage = true;
            }

        }
    }

    public void processResponse(SearchResponse response) {
        if (response == null) {
            mView.showError();
        } else {
            mView.hideErrorView();
            mView.hideProgress();
            int pages = response.getPhotos().getPages();
            TOTAL_PAGES = pages;
            List<PhotoItem> photoItems = fetchImages(response);
            mView.addAllPhotos(photoItems);
            if (currentPage <= TOTAL_PAGES) {
                mView.addLoadingFooter();
            } else {
                isLastPage = true;
            }
        }

    }

    public List<PhotoItem> fetchImages(SearchResponse body) {
        if (body != null && (body.getPhotos() != null && body.getPhotos().getPhoto().isEmpty())) {
            return Collections.emptyList();
        } else if (body.getPhotos() != null) {
            return body.getPhotos().getPhoto();
        }
        return null;
    }
}
