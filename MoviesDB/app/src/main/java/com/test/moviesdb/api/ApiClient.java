package com.test.moviesdb.api;

import android.util.Log;

import com.test.moviesdb.BuildConfig;
import com.test.moviesdb.MovieApplication;
import com.test.moviesdb.utils.Constant;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.NONE;

/**
 * Created by Farhan on 1/22/2018.
 */

/*
 * API client class for Retrofit to create and request API calls
 */
public class ApiClient {
    //TAG for debugging and logging purpose
    private static final String TAG = ApiClient.class.getSimpleName();

    private static final String CACHE_CONTROL = "Cache-Control";
    private static Retrofit retrofit = null;

    //Function to get Retrofit object with all the settings including base url of the API
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .client(provideOkHttpClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    //Function to get OkHttpClient object with all the settings
    private static OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(provideHttpLoggingInterceptor())
                .addInterceptor(provideOfflineCacheInterceptor())
                .addNetworkInterceptor(provideCacheInterceptor())
                .cache(provideCache())
                .build();
    }

    //Cache settings
    private static Cache provideCache() {
        Cache cache = null;
        try {
            cache = new Cache(new File(MovieApplication.getInstance().getCacheDir(), "http-cache"),
                    20 * 1024 * 1024); // 20 MB
        } catch (Exception e) {
            Log.e(TAG, "Could not create Cache!" + e);
        }
        return cache;
    }

    private static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor =
                new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        Log.d(TAG, message);
                    }
                });
        httpLoggingInterceptor.setLevel(BuildConfig.DEBUG ? HEADERS : NONE);
        return httpLoggingInterceptor;
    }

    public static Interceptor provideCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());

                // re-write response header to force use of cache
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(1, TimeUnit.MINUTES)
                        .build();

                return response.newBuilder()
                        .header(CACHE_CONTROL, cacheControl.toString())
                        .removeHeader("Pragma")
                        .build();
            }
        };
    }

    public static Interceptor provideOfflineCacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                if (!MovieApplication.hasNetwork()) {
                    CacheControl cacheControl = new CacheControl.Builder()
                            .maxStale(365, TimeUnit.DAYS)
                            .build();

                    request = request.newBuilder()
                            .cacheControl(cacheControl)
                            .removeHeader("Pragma")
                            .build();
                }

                return chain.proceed(request);
            }
        };
    }

    //Function to get ApiInterface
    public static ApiInterface getApiService() {

        return getClient().create(ApiInterface.class);
    }
}

