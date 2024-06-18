package com.example.ijk.util;

import android.util.Log;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class RxTimerUtil {
    private Disposable mDisposable = null;
    private String tag = "RxTimerUtil";

    public void interval(long seconds, IRxNext next) {
        Observable.interval(seconds, TimeUnit.SECONDS)
            .compose(RxSchedulerHelper.io2main())
            .subscribe(new Observer<Long>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    mDisposable = d;
                }
                @Override
                public void onNext(@NonNull Long data) {
                    next.doNext(data);
                }
                @Override
                public void onError(@NonNull Throwable e) {
                    Log.d(tag, "onError:" + e.getMessage());
                }
                @Override
                public void onComplete() {
                    Log.d(tag, "onComplete");
                }
            });
    }

    public void cancel() {
        if (!mDisposable.isDisposed()) {
            mDisposable.dispose();
            Log.d(tag, "cancel");
        }
        mDisposable = null;
    }

    public interface IRxNext {
        void doNext(Long data);
    }
}