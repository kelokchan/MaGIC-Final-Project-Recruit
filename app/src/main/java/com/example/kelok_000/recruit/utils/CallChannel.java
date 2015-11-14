package com.example.kelok_000.recruit.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by someguy233 on 06-Nov-15.
 */
public class CallChannel implements Runnable {

    public static class Call {
        public static Method getMethod(Object o, String name, int numParameters) {
            Class<?> type = o.getClass();
            Method[] methods = type.getDeclaredMethods();
            for (int c = 0; c < methods.length; c++) {
                Method method = methods[c];
                if (method.getName().contentEquals(name) && method.getParameterTypes().length == numParameters) {
                    if (!method.isAccessible())
                        method.setAccessible(true);
                    return method;
                }
            }
            return null;        // not found
        }

        public static void invoke(Object o, String name, Object ... params) {
            Method method = getMethod(o, name, params.length);
            if(method == null)
                throw new IllegalArgumentException("No method found named " + name + " with " + params.length + " parameters in " + o.getClass().getSimpleName());
            try {
                method.invoke(o, params);
            } catch (Throwable e) {
                throw new RuntimeException("Exception in call", e);
            }
        }

        public final String methodName;
        public final Object[] params;

        public Call(String methodName, Object[] params) {
            this.methodName = methodName;
            this.params = params;
        }

        public void call(Object listener) {
            invoke(listener, methodName, params);
        }
    }

    public static class Request implements Runnable {
        public static <T> Constructor<T> getConstructor(Class<T> type, int numParameters) {
            Constructor<?>[] constructors = type.getConstructors();
            for(int c = 0; c < constructors.length; c++) {
                Constructor<?> constructor = constructors[c];
                if(constructor.getParameterTypes().length == numParameters) {
                    if(!constructor.isAccessible())
                        constructor.setAccessible(true);
                    return (Constructor<T>) constructor;
                }
            }
            return null;        // not found
        }

        public static <T> T construct(Class<T> type, Object ... params) {
            Constructor<T> constructor = getConstructor(type, params.length);
            if(constructor == null)
                throw new IllegalArgumentException("No constructor found with " + params.length + " parameters in " + type.getSimpleName());
            try {
                return constructor.newInstance(params);
            } catch (Throwable e) {
                throw new RuntimeException("Exception in construction", e);
            }
        }

        public final CallChannel channel;
        public final String methodName;
        public final Class<?> requestType;
        public final Object[] requestParams;

        public Request(CallChannel channel, String methodName, Class<?> requestType, Object ... requestParams) {
            this.channel = channel;
            this.methodName = methodName;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        public void send() {
            AsyncTask.THREAD_POOL_EXECUTOR.execute(this);
        }

        @Override
        public void run() {
            Object result = null;
            Throwable t = null;
            try {
                result = Request.construct(requestType, requestParams);
            } catch (Throwable e) {
                t = e;
            }
            channel.call(methodName, result, t);
        }
    }

    private final ArrayList<Call> queued = new ArrayList<>();
    private Object listener = null;
    private Thread listenerThread = null;
    private Handler listenerHandler = null;

    public void request(String methodName, Class<?> requestType, Object ... requestParams) {
        Request request = new Request(this, methodName, requestType, requestParams);
        request.send();
    }

    public synchronized void call(String methodName, Object... params) {
        if (listener == null) {
            // No listener at all, just queue
            queued.add(new Call(methodName, params));
            return;
        } else if (listenerThread != Thread.currentThread()) {
            // Is listening but different thread, queue up
            queued.add(new Call(methodName, params));
            // Need to ask handler to run the queue, if available
            if (listenerHandler != null)
                listenerHandler.post(this);
            return;
        }
        // Else is listening and is on the current thread, just invoke
        Call.invoke(listener, methodName, params);
    }

    @Override
    public synchronized void run() {
        if (listener == null)
            throw new IllegalStateException("Cannot run a closed channel");
        if (Thread.currentThread() != listenerThread)
            throw new IllegalStateException("Current thread does not own the channel");
        // Execute all queued
        while (queued.size() > 0) {
            Call c = queued.remove(0);
            c.call(listener);
        }
    }

    public synchronized void open(Object listener) {
        if (listener == null)
            throw new IllegalArgumentException("listener cannot be null");
        if (this.listener != null)
            throw new IllegalStateException("Channel is already open");
        // Execute queued calls, passthrough exceptions
        while (queued.size() > 0) {
            Call c = queued.remove(0);
            c.call(listener);
        }            // Register listener
        this.listener = listener;
        listenerThread = Thread.currentThread();
        Looper currentLooper = Looper.myLooper();
        if (currentLooper != null)
            listenerHandler = new Handler(currentLooper);
    }

    public synchronized void close(Object listener) {
        if (listener == null)
            throw new IllegalArgumentException("listener cannot be null");
        if (this.listener != listener)
            throw new IllegalStateException("Channel is open on another listener");
        if (listenerThread != Thread.currentThread())
            throw new IllegalStateException("Can only close() on the same thread that called open()");
        this.listener = null;
        listenerThread = null;
        listenerHandler = null;
    }
}
