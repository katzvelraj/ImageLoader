package com.search.velrajp.flickrsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.search.velrajp.flickrsearch.fileutils.ImageLoader;
import com.search.velrajp.flickrsearch.model.PhotoItem;
import com.vel.flickrsearch.utils.PaginationAdapterCallback;

import java.util.ArrayList;
import java.util.List;


public class PaginationImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    // View Types
    private static final int IMAGE_ITEM = 0;
    private static final int LOADING_LAYOUT = 1;
    private boolean isLoadingAdded = false;
    private List<PhotoItem> photosList;
    private Context context;
    private PaginationAdapterCallback mCallback;
    private boolean retryPageLoad = false;
    private String errorMsg;

    ImageLoader imageLoader;

    public PaginationImageAdapter(Context context) {
        this.context = context;
        this.mCallback = (PaginationAdapterCallback) context;
        photosList = new ArrayList<>();
        imageLoader = new ImageLoader(context.getApplicationContext());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case IMAGE_ITEM:
                View viewItem = inflater.inflate(R.layout.item_layout, parent, false);
                viewHolder = new ImageViewHolder(viewItem);
                break;
            case LOADING_LAYOUT:
                View viewLoading = inflater.inflate(R.layout.loading_layout, parent, false);
                viewHolder = new LoadingViewHolder(viewLoading);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        PhotoItem photoItem = photosList.get(pos);
        switch (getItemViewType(pos)) {
            case IMAGE_ITEM:
                final ImageViewHolder viewHolder = (ImageViewHolder) holder;
                String url = "http://farm" + photoItem.getFarm() + ".static.flickr.com/" + photoItem.getServer() + "/" + photoItem.getId() + "_" + photoItem.getSecret() + ".jpg";
                //  Picasso.get().load(url).into(viewHolder.imageView);
                imageLoader.displayImage(url, viewHolder.imageView);

                break;

            case LOADING_LAYOUT:
                LoadingViewHolder loadingVH = (LoadingViewHolder) holder;
                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return photosList == null ? 0 : photosList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == photosList.size() - 1 && isLoadingAdded) ? LOADING_LAYOUT : IMAGE_ITEM;
    }

    public void addAll(List<PhotoItem> moveResults) {
        for (PhotoItem result : moveResults) {
            add(result);
        }
    }

    public void clearAll(){
        photosList.clear();
    }

    private void add(PhotoItem item) {
        photosList.add(item);
        notifyItemInserted(photosList.size() - 1);
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new PhotoItem());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = photosList.size() - 1;
        PhotoItem result = getItem(position);

        if (result != null) {
            photosList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public PhotoItem getItem(int position) {
        return photosList.get(position);
    }


    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(photosList.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }

    protected class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }

    protected class LoadingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }
}
