package com.search.velrajp.flickrsearch.di;

import com.search.velrajp.flickrsearch.datamanager.DataMangerContract;
import com.search.velrajp.flickrsearch.datamanager.ImageDataManger;
import com.search.velrajp.flickrsearch.presenter.PresenterContract;
import com.search.velrajp.flickrsearch.presenter.PresenterImpl;

import dagger.Module;
import dagger.Provides;


@Module
public class DiModule {

    private PresenterContract.MainActivityView mView;

    public DiModule(PresenterContract.MainActivityView mView) {
        this.mView = mView;
    }


    @Provides
    public PresenterContract.MainActivityView provideView() {
        return mView;
    }

    @Provides
    DataMangerContract providesDataManager() {
        return new ImageDataManger();
    }

    @Provides
    PresenterContract.Presenter providePresenter(PresenterImpl view) {
        return view;
    }

}
