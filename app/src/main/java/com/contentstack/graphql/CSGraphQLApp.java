package com.contentstack.graphql;

import android.app.Application;

import com.apollographql.apollo.ApolloClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CSGraphQLApp extends Application {
    /*
      /***********Download schema.json***********/
    //apollo-codegen introspect-schema "https://dev-graphql.contentstack.io/stacks/blt44d915c18f115370/explore?
    // access_token=cs551d666a332e455a34174bd0&environment=production" --output schema.json

    private static ApolloClient apolloClient;

    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder().method(original.method(), original.body());
            // Add headers and authentication keys here
            //builder.header("access_token", "access_token");
            return chain.proceed(builder.build());
        }).build();

        apolloClient = ApolloClient.builder().serverUrl(BuildConfig.BASE_URL).okHttpClient(okHttpClient).build();
    }


    public static ApolloClient getApolloClient(){ return apolloClient; }

}
