package com.contentstack.graphql;

import android.app.Application;
import android.util.Log;

import com.apollographql.apollo.ApolloClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class CSGraphQLApp extends Application {

    String queryString = "https://dev-graphql.contentstack.io/stacks/blt44d915c18f115370?access_token=cs551d666a332e455a34174bd0&environment=production&query=query%20IntrospectionQuery%20{%20__schema%20{%20queryType%20{%20name%20}%20mutationType%20{%20name%20}%20subscriptionType%20{%20name%20}%20types%20{%20...FullType%20}%20directives%20{%20name%20description%20locations%20args%20{%20...InputValue%20}%20}%20}%20}%20fragment%20FullType%20on%20__Type%20{%20kind%20name%20description%20fields(includeDeprecated:%20true)%20{%20name%20description%20args%20{%20...InputValue%20}%20type%20{%20...TypeRef%20}%20isDeprecated%20deprecationReason%20}%20inputFields%20{%20...InputValue%20}%20interfaces%20{%20...TypeRef%20}%20enumValues(includeDeprecated:%20true)%20{%20name%20description%20isDeprecated%20deprecationReason%20}%20possibleTypes%20{%20...TypeRef%20}%20}%20fragment%20InputValue%20on%20__InputValue%20{%20name%20description%20type%20{%20...TypeRef%20}%20defaultValue%20}%20fragment%20TypeRef%20on%20__Type%20{%20kind%20name%20ofType%20{%20kind%20name%20ofType%20{%20kind%20name%20ofType%20{%20kind%20name%20ofType%20{%20kind%20name%20ofType%20{%20kind%20name%20ofType%20{%20kind%20name%20ofType%20{%20kind%20name%20}%20}%20}%20}%20}%20}%20}%20}";

    @Override
    public void onCreate() {
        super.onCreate();
    }

}
