package com.example.ma_mobile.network;

import android.content.Context;

import com.example.ma_mobile.utils.SessionManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;
    private static Context appContext = null;

    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    public static ApiService getApiService() {
        if (apiService == null) {
            retrofit = createRetrofit();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    private static Retrofit createRetrofit() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor);

        // Add auth interceptor if context is available
        if (appContext != null) {
            httpClient.addInterceptor(new AuthInterceptor(appContext));
        }

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static class AuthInterceptor implements Interceptor {
        private Context context;

        public AuthInterceptor(Context context) {
            this.context = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();

            SessionManager sessionManager = new SessionManager(context);
            String token = sessionManager.getBearerToken();

            if (token != null) {
                Request authenticatedRequest = originalRequest.newBuilder()
                        .header("Authorization", token)
                        .build();
                return chain.proceed(authenticatedRequest);
            }

            return chain.proceed(originalRequest);
        }
    }
}
