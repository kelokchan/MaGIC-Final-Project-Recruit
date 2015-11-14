package com.example.kelok_000.recruit.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit.Call;
import retrofit.CallAdapter;
import retrofit.Retrofit;

/**
 * Created by someguy233 on 02-Nov-15.
 */
public class CallerAdapterFactory implements CallAdapter.Factory {

    @Override
    public CallAdapter<Caller<?>> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (!(returnType instanceof ParameterizedType))
            return null;
        ParameterizedType type = (ParameterizedType) returnType;
        if (type.getRawType() != Caller.class || type.getActualTypeArguments().length != 1)
            return null;

        final Type responseType = type.getActualTypeArguments()[0];

        return new CallAdapter<Caller<?>>() {
            @Override
            public Type responseType() {
                return responseType;
            }

            @Override
            public <R> Caller<?> adapt(Call<R> call) {
                return new Caller<R>(call);
            }
        };
    }
}
