package com.test.moviesdb.api;

import android.os.Looper;
import android.util.Log;
import android.view.View;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Farhan on 1/22/2018.
 */

public class CallbacksManager {
    private Set<CancelableCallback> callbacks = new HashSet<>();

    public void cancelAll() {
        for (CancelableCallback callback : callbacks) {
            // false to avoid java.util.ConcurrentModificationException alternatively we can use
            // iterator
            callback.cancel(false);
        }
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Log.d(">>ThreadNames", "Main:callbacks.clear() : " + Thread.currentThread().getName());
        } else {
            Log.d(">>ThreadNames", "Back : callbacks.clear() : " + Thread.currentThread().getName());
        }
        callbacks.clear();
    }

    public void resumeAll() {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Log.d(">>ThreadNames", "Main:resumeAll : " + Thread.currentThread().getName());
        } else {
            Log.d(">>ThreadNames", "Back : resumeAll : " + Thread.currentThread().getName());
        }
        final Iterator<CancelableCallback> iterator = callbacks.iterator();
        while (iterator.hasNext()) {
            boolean remove = iterator.next().resume();
            if (remove) {
                iterator.remove();
            }
        }
    }

    public void pauseAll() {
        for (CancelableCallback callback : callbacks) {
            callback.pause();
        }
    }

    public void addCallback(CancelableCallback<?> callback) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            Log.d(">>ThreadNames", "Main:addCallback : " + Thread.currentThread().getName());
        } else {
            Log.d(">>ThreadNames", "Back : addCallback : " + Thread.currentThread().getName());
        }
        callbacks.add(callback);
    }

    private void removeCallback(CancelableCallback<?> callback) {
        callbacks.remove(callback);
    }


    public abstract class CancelableCallback<T> implements Callback<T> {
        private boolean canceled;
        private boolean paused;

        private Call<T> pendingT;
        private Response pendingResponse;
        private Throwable pendingError;
        private View mView;

        public CancelableCallback(View mView) {
            this.mView = mView;
            this.canceled = false;
        }

        public void pause() {
            paused = true;
        }

        public boolean resume() {
            paused = false;
            // if callback was cancelled then no need to post pending results
            if (canceled) {
                return true;
            }
            if (pendingError != null) {
                failure(null, pendingError);
                // to make sure not to post it again
                pendingError = null;
                return true;
            } else if (pendingT != null) {
                response(pendingResponse, mView);
                // to make sure not to post it again
                pendingT = null;
                pendingResponse = null;
                return true;
            }
            return false;
        }

        private void cancel(boolean remove) {
            canceled = true;
            if (remove) {
                removeCallback(this);
            }
        }

        @Override
        public void onResponse(Call<T> call, Response<T> response) {
            if (response.isSuccessful() && response.body() != null) {
                // tasks available
                if (canceled) {
                    return;
                }
                if (paused) {
                    pendingT = call;
                    pendingResponse = response;
                    return;
                }
                response(response, mView);
                removeCallback(this);
            } else {
                // error response, no access to resource?
                failure(response, null);
            }

        }

        @Override
        public void onFailure(Call<T> call, Throwable t) {
            if (canceled) {
                return;
            }
            if (paused) {
                pendingError = t;
                return;
            }
            failure(null, t);
            removeCallback(this);
        }

        protected abstract void response(Response response, View view);

        protected abstract void failure(Response response, Throwable error);

    }
}
