package com.example.kelok_000.recruit.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by someguy233 on 02-Nov-15.
 */
public class Caller<T> implements Callback<T> {
    static final String TAG = "Caller";

    private static Object[] extendArray(Object[] array, int extension) {
        Object[] extended = new Object[array.length + extension];
        System.arraycopy(array, 0, extended, extension, array.length);
        return extended;

    }

    public final Call<T> call;

    // Callback
    CallChannel channel;

    String responseMethodName = null;
    Object[] responseMethodParams = null;

    String errorMethodName = null;
    Object[] errorMethodParams = null;

    String failureMethodName = null;
    Object[] failureMethodParams = null;

    private void assertNotYetCalled() {
        if(channel != null)
            throw new IllegalStateException("Call already initiated");
    }

    public Caller(Call<T> call) {
        this.call = call;
    }

    public void cancel() {
        call.cancel();
    }

    public Caller<T> forResponse(String name, Object ... params) {
        assertNotYetCalled();
        responseMethodName = name;
        responseMethodParams = extendArray(params, 1);
        return this;
    }

    public Caller<T> forError(String name, Object ... params) {
        assertNotYetCalled();
        errorMethodName = name;
        errorMethodParams = extendArray(params, 3);
        return this;
    }

    public Caller<T> forFailure(String name, Object ... params) {
        assertNotYetCalled();
        failureMethodName = name;
        failureMethodParams = extendArray(params, 1);
        return this;
    }

    public void call(CallChannel channel) {
        assertNotYetCalled();
        this.channel = channel;
        if(channel == null && (responseMethodName != null || errorMethodName != null || failureMethodName != null))
            throw new IllegalArgumentException("listener cannot be null if listener methods are set");
        call.enqueue(this);
    }

    public void call() {
        call(null);
    }

    @Override
    public void onResponse(final Response<T> response, final Retrofit retrofit) {
        if(Thread.currentThread() != Looper.getMainLooper().getThread()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onResponse(response, retrofit);
                }
            });
            return;
        }
        if (!response.isSuccess()) {
            // Error typeFrom server
            int code = response.code();
            String message = response.message();
            String body;
            try {
                body = response.errorBody().string();
            } catch (Throwable e) {
                onFailure(e);
                return;
            }
            if (errorMethodName == null)
                onFailure(new RuntimeException("Unexpected response typeFrom server: " + code + " " + message + " " + body));
            else {
                errorMethodParams[0] = code;
                errorMethodParams[1] = message;
                errorMethodParams[2] = body;
                channel.call(errorMethodName, errorMethodParams);
            }
            return;
        }
        // Success
        if(responseMethodName == null)
            return;
        responseMethodParams[0] = response.body();
        channel.call(responseMethodName, responseMethodParams);
    }

    @Override
    public void onFailure(final Throwable t) {
        if(Thread.currentThread() != Looper.getMainLooper().getThread()) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onFailure(t);
                }
            });
            return;
        }
        if (failureMethodName == null) {
            // Ignored
            Log.d(TAG, "Network failure ignored", t);
            return;
        }
        failureMethodParams[0] = t;
        channel.call(failureMethodName, failureMethodParams);
    }

    @Override
    protected Caller<T> clone() {
        return new Caller<>(call.clone());
    }
}
