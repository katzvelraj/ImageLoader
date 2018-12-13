package com.search.velrajp.flickrsearch.imagesearch;


import com.search.velrajp.flickrsearch.TestDataFactory;
import com.search.velrajp.flickrsearch.datamanager.DataMangerContract;
import com.search.velrajp.flickrsearch.model.SearchResponse;
import com.search.velrajp.flickrsearch.presenter.PresenterContract;
import com.search.velrajp.flickrsearch.presenter.PresenterImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PresenterImplTest {


    @Mock
    private
    PresenterContract.MainActivityView mView;

    @Mock
    private PresenterImpl mPresenter;

    private PresenterImpl mPresenterImpl;
    private TestScheduler testScheduler = new TestScheduler();

    @Mock
    private DataMangerContract dataMangerContract;


    @Before
    public void setup() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(h -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(h -> Schedulers.trampoline());
        mPresenterImpl = new PresenterImpl(mView, dataMangerContract);
    }


    @Test
    public void testGetFirstPage() {
        SearchResponse searchResponse = TestDataFactory.getValidObject();
        given(dataMangerContract.getFirstPageData(1, "")).willReturn(Observable.just(searchResponse));
        mPresenterImpl.loadFirstPage("");
        testScheduler.triggerActions();
        then(mView).should().hideErrorView();
        then(mView).should().hideProgress();

    }


    @Test
    public void testGetLastPage() {
        SearchResponse searchResponse = TestDataFactory.getValidObject();
        mPresenterImpl.currentPage = 1444;
        given(dataMangerContract.getFirstPageData(1444, "")).willReturn(Observable.just(searchResponse));
        mPresenterImpl.loadFirstPage("");
        testScheduler.triggerActions();
        then(mView).should().hideErrorView();
        then(mView).should().hideProgress();
    }

    @Test
    public void testGetNextPage() {
        SearchResponse searchResponse = TestDataFactory.getValidObject();
        mPresenterImpl.currentPage = 3;
        given(dataMangerContract.getFirstPageData(3, "")).willReturn(Observable.just(searchResponse));
        mPresenterImpl.loadNextPage("");
        testScheduler.triggerActions();
        then(mView).should().removeLoadingFooter();
        then(mView).should().addLoadingFooter();

    }

    @Test
    public void testErrorData() {
        Throwable throwable = new Throwable();
        mPresenterImpl.currentPage = 1;
        given(dataMangerContract.getFirstPageData(1, "")).willReturn(Observable.error(throwable));
        mPresenterImpl.loadFirstPage("");
        testScheduler.triggerActions();
        mView.showRetry(throwable);

    }

    @Test
    public void testErrorNextData() {
        Throwable throwable = new Throwable();
        mPresenterImpl.currentPage = 1;
        given(dataMangerContract.getFirstPageData(1, "")).willReturn(Observable.error(throwable));
        mPresenterImpl.loadNextPage("");
        testScheduler.triggerActions();
        mView.showRetry(throwable);

    }


    @Test
    public void fetchImages() {
        SearchResponse searchResponse = TestDataFactory.getEmptyObject();
        mPresenterImpl.fetchImages(searchResponse);

    }


    @Test
    public void getTotalPageCountTestFailure() {
        when(mPresenter.getTotalPageCount()).thenReturn(4);
        int expected = mPresenter.getTotalPageCount();
        Assert.assertNotEquals(expected, 5);
    }

    @Test
    public void getTotalPageCountTest() {
        when(mPresenter.getTotalPageCount()).thenReturn(5);
        int expected = mPresenter.getTotalPageCount();
        Assert.assertEquals(expected, 5);
    }


    @Test
    public void getLastPagePositive() {
        // when(mPresenter.getIsLastPage()).thenReturn(true);
        mPresenterImpl.isLastPage = true;
        boolean expected = mPresenterImpl.getIsLastPage();
        Assert.assertTrue(expected);
    }

    @Test
    public void getIsLastPageTestFailure() {
        //when(mPresenter.getIsLastPage()).thenReturn(false);
        mPresenterImpl.isLastPage = false;
        boolean expected = mPresenterImpl.getIsLastPage();
        Assert.assertFalse(expected);
    }


    @Test
    public void getIsLoadingPositive() {
        mPresenterImpl.isLoading = true;
        boolean expected = mPresenterImpl.getIsLoading();
        Assert.assertTrue(expected);
    }

    @Test
    public void getIsLoadingTestFailure() {
        mPresenterImpl.isLoading = false;
        boolean expected = mPresenterImpl.getIsLoading();
        Assert.assertFalse(expected);
    }
}
