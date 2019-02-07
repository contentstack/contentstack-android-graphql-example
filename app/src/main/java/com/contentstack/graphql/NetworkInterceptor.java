package com.contentstack.graphql;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkInterceptor implements Interceptor {

    @Override public Response intercept(Interceptor.Chain chain) throws IOException {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .url(BuildConfig.ENDPOINT)
                    .header("operationName", "allProduct")
                    .header("query", "query%20allProduct%20{all_product(locale:%22en-us%22)%20{%20items{title%20price%20featured_image{url}}}}")
                    .header("variables", "")
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
    }


}


