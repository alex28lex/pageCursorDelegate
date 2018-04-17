package com.example.pagination.pagination;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pagination.pagination.datasource.ImagesDataSource;
import com.example.pagination.pagination.datasource.ImagesRestMockDataSource;
import com.example.pagination.pagination.model.ImageDataDto;
import com.example.pagination.pagination.view.ImagesAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private ImagesDataSource imagesDataSource;
    private PageCursorDelegate<ImageDataDto> pageCursorDelegate;

    @BindView(R.id.recycler_view)
    protected RecyclerView recyclerView;

    @BindView(R.id.progress)
    protected ProgressBar progressBar;

    private ImagesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new ImagesAdapter();
        imagesDataSource = new ImagesRestMockDataSource();

        pageCursorDelegate = new PageCursorDelegate(10, 3, new PageCursorDelegate.RequestConsumer() {
            @Override
            public Flowable request(String nextCursor, int pageSize) {
                return imagesDataSource.getImagesData(nextCursor, pageSize)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int lastVisiblePosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastCompletelyVisibleItemPosition();
                paginateImagesRequest(lastVisiblePosition, adapter.getItemCount());

            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        loadImagesRequest();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pageCursorDelegate.release();
    }

    private void paginateImagesRequest(int lastVisiblePosition, int adapteritemsCount) {
        pageCursorDelegate.paginate(lastVisiblePosition, adapteritemsCount, new PageCursorDelegate.LoadListener<ImageDataDto>() {
            @Override
            public void onLoadPageSuccess(ImageDataDto data) {
                enableProgress(false);
                adapter.addItems(data.getItems());
            }

            @Override
            public void onLoadPageFailed(String message) {
                enableProgress(false);
                showMessage(message);
            }

            @Override
            public void onPageLoading() {
                enableProgress(true);
            }
        });
    }

    private void loadImagesRequest() {
        pageCursorDelegate.refresh(new PageCursorDelegate.LoadListener<ImageDataDto>() {
            @Override
            public void onLoadPageSuccess(ImageDataDto data) {
                enableProgress(false);
                adapter.setItems(data.getItems());
            }

            @Override
            public void onLoadPageFailed(String message) {
                enableProgress(false);
                showMessage(message);
            }

            @Override
            public void onPageLoading() {
                enableProgress(true);
            }
        });
    }


    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void enableProgress(boolean isEnable) {
        progressBar.setVisibility(isEnable ? View.VISIBLE : View.GONE);
    }


}
