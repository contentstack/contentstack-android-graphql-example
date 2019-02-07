package com.contentstack.graphql;

import android.util.Log;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class NetworkInterceptor implements Interceptor {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override public Response intercept(Interceptor.Chain chain) throws IOException {

        Request original = chain.request();
        Request request = original.newBuilder()
                    .url(BuildConfig.ENDPOINT)
                    .header("operationName", "allProduct")
                    .header("query", bodyToString(original))
                    .method(original.method(), original.body())
                    .build();

        Log.e("bodyToString(original)", bodyToString(original));
            return chain.proceed(request);
    }



    private  String bodyToString(final Request request){

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }


}


