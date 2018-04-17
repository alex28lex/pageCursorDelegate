package com.example.pagination.pagination;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import lombok.Getter;
import lombok.Setter;

/**
 * Developed by Magora Team (magora-systems.com). 2017.
 *
 * @author mihaylov
 */
public class PageCursorDelegate<T extends PageCursorDelegate.CursorData> {

    public static final String FIRST_PAGE_CURSOR = null;

    private final RequestConsumer<T> requestConsumer;

    @Getter
    @Setter
    private String nextCursor;

    @Getter
    private int pageSize;

    @Getter
    private int countItemsBeforeEnd;

    @Getter
    @Setter
    private boolean isLoading;

    private Disposable disposable;


    public PageCursorDelegate(int pageSize, int countItemsBeforeEnd, RequestConsumer<T> requestConsumer) {
        this.nextCursor = null;
        this.pageSize = pageSize;
        this.countItemsBeforeEnd = countItemsBeforeEnd;
        this.requestConsumer = requestConsumer;
    }

    private void loadPage(String pageCursor, final LoadListener<T> loadListener) {
        isLoading = true;
        loadListener.onPageLoading();
        disposable = requestConsumer.request(pageCursor, pageSize)
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        isLoading = false;
                        nextCursor = t.provideNextCursor();
                        loadListener.onLoadPageSuccess(t);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        isLoading = false;
                        loadListener.onLoadPageFailed(throwable.getMessage());
                    }
                });
    }

    public void paginate(int lastVisiblePosition, int adapterItemCount, final LoadListener<T> loadListener) {
        if (lastVisiblePosition >= (adapterItemCount - countItemsBeforeEnd - 1)) {
            if (nextCursor != null && !isLoading) {
                loadPage(nextCursor, loadListener);
            }
        }
    }

    public void refresh(final LoadListener<T> loadListener) {
        isLoading = false;
        nextCursor = null;
        loadPage(FIRST_PAGE_CURSOR, loadListener);
    }

    public void release() {
        if (disposable != null)
            disposable.dispose();
    }

    //  public void paginate()

    public interface LoadListener<T extends PageCursorDelegate.CursorData> {
        void onPageLoading();

        void onLoadPageSuccess(T data);

        void onLoadPageFailed(String message);

    }

    public interface CursorData {
        String provideNextCursor();
    }

    public interface RequestConsumer<T extends PageCursorDelegate.CursorData> {
        Flowable<T> request(String nextCursor, int pageSize);
    }
}
