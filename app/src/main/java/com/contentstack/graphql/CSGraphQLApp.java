package com.contentstack.graphql;

import android.app.Application;
import com.apollographql.apollo.ApolloClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CSGraphQLApp extends Application {

    private static ApolloClient apolloClient;

    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder().method(original.method(), original.body());
            //Add headers and authentication keys here like below
            //builder.header("access_token", "access_token");
            return chain.proceed(builder.build());
        }).build();

        apolloClient = ApolloClient.builder().serverUrl(BuildConfig.BASE_URL).okHttpClient(okHttpClient).build();
    }


    public static ApolloClient getApolloClient(){ return apolloClient; }

}
