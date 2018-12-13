package com.search.velrajp.flickrsearch.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.search.velrajp.flickrsearch.PaginationImageAdapter;
import com.search.velrajp.flickrsearch.R;
import com.search.velrajp.flickrsearch.di.DaggerDiComponent;
import com.search.velrajp.flickrsearch.di.DiModule;
import com.search.velrajp.flickrsearch.model.PhotoItem;
import com.search.velrajp.flickrsearch.presenter.PresenterContract;
import com.vel.flickrsearch.utils.PaginationAdapterCallback;
import com.vel.flickrsearch.utils.ScrollListener;

import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity implements PaginationAdapterCallback, PresenterContract.MainActivityView {

    private PaginationImageAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button btnRetry;
    private TextView txtError;
    private EditText searchImage;
    private Button btn_search;

    @Inject
    PresenterContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeDagger();
        initializeViews();
        setUpAdapter();
        initializeScrollListener();

        if (!searchImage.getText().toString().isEmpty())
            presenter.loadFirstPage(searchImage.getText().toString());
        else
            showEmptyError();


        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!searchImage.getText().toString().isEmpty())
                    presenter.loadFirstPage(searchImage.getText().toString());
                else
                    showEmptyError();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!searchImage.getText().toString().isEmpty())
                    presenter.loadFirstPage(searchImage.getText().toString());
                else
                    showEmptyError();
            }
        });


        searchImage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    adapter.clearAll();
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void showEmptyError() {
        hideProgress();
        Toast.makeText(this, "Please Enter word to search", Toast.LENGTH_SHORT).show();
    }

    private void initializeScrollListener() {
        recyclerView.addOnScrollListener(new ScrollListener(gridLayoutManager) {
            @Override
            protected void loadMoreItems() {
                presenter.loadMoreItems();
                if (!searchImage.getText().toString().isEmpty())
                    presenter.loadNextPage(searchImage.getText().toString());
                else
                    showEmptyError();
            }

            @Override
            public int getTotalPageCount() {
                return presenter.getTotalPageCount();
            }

            @Override
            public boolean isLastPage()
            {
                return presenter.getIsLastPage();
            }

            @Override
            public boolean isLoading() {
                return presenter.getIsLoading();
            }
        });


    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.main_progress);
        errorLayout = findViewById(R.id.error_layout);
        btnRetry = findViewById(R.id.error_btn_retry);
        txtError = findViewById(R.id.error_txt_cause);
        searchImage = findViewById(R.id.edTxt_search);
        btn_search = findViewById(R.id.btn_search);
    }


    /**
     * Initialize adapter
     */
    private void setUpAdapter() {
        adapter = new PaginationImageAdapter(this);
        gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }


    /**
     * initialize dagger for DI
     */
    private void initializeDagger() {
        DaggerDiComponent.builder()
                .diModule(new DiModule(this))
                .build().inject(this);
    }


    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void retryPageLoad() {
        presenter.loadNextPage("");
    }

    public void showErrorView(Throwable throwable) {
        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    @Override
    public void showRetry(Throwable e) {
        adapter.showRetry(true, fetchErrorMessage(e));
    }

    @Override
    public void removeLoadingFooter() {
        adapter.removeLoadingFooter();
    }

    public void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void addAllPhotos(List<PhotoItem> photoItems) {
        if (photoItems != null && !photoItems.isEmpty())
            adapter.addAll(photoItems);
        else if (photoItems != null && photoItems.isEmpty()) {
            adapter.clearAll();
            showErrorView(new Throwable("Empty Photo"));
        }


    }

    @Override
    public void addLoadingFooter() {
        adapter.addLoadingFooter();
    }

    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    /**
     *
     * @return  networkinfo
     */

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void showError() {
        hideProgress();
    }
}
