package com.search.velrajp.flickrsearch.api;


import com.search.velrajp.flickrsearch.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            Interceptor interceptorHeader = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {


                    Request request = chain.request();
                    HttpUrl url = request.url().newBuilder()
                            .addQueryParameter("method", "flickr.photos.search")
                            .addQueryParameter("per_page","20")
                            .addQueryParameter("api_key", BuildConfig.SEARCH_API_KEY)
                            .addQueryParameter("format", "json")
                            .addQueryParameter("nojsoncallback", "1")
                            .addQueryParameter("safe_search", "1")
                            .build();
                    request = request.newBuilder().url(url).build();
                    return chain.proceed(request);
                }
            };
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .addInterceptor(getLoggingInterceptor())
                    .addInterceptor(interceptorHeader)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    private static Interceptor getLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        return interceptor;
    }
}
