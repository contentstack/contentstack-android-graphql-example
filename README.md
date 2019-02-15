
  
## Build an example app using Contentstack Android SDK, GraphQL API, and Apollo Client


We have created a sample product catalog app that is built using Contentstack’s Android SDK. The content of this app is powered by Contentstack GraphQL APIs, and the app uses Apollo client on the client side to consume GraphQL APIs.

This document covers the steps to get this app up and running for you. Try out the app and play with it, before building bigger and better applications.  
  
Screenshot  
  
<img src='https://github.com/contentstack/contentstack-android-graphql-example/blob/master/app/src/main/java/com/contentstack/graphql/screenshot/ProductList.png' width='320' height='600'/>  
  
## Prerequisites  
  
- [Android Studio](https://developer.android.com/studio/) 
- [Contentstack account](https://app.contentstack.com/#!/login)  
- [Basic knowledge of Contentstack](https://www.contentstack.com/docs/platforms/android)  
      
  
In this tutorial, we will first go through the steps involved in configuring Contentstack and then look at the steps required to customize and use the presentation layer.  
  
## Step 1: Create a stack  
  Log in to your Contentstack account, and [create a new stack](https://www.contentstack.com/docs/guide/stack#create-a-new-stack). Read more about [stack](https://www.contentstack.com/docs/guide/stack).  
  
## Step 2: Add a publishing environment  
  [Add a publishing environment](https://www.contentstack.com/docs/guide/environments#add-an-environment) to publish your content in Contentstack. Provide the necessary details as per your requirement. Read more about [environments](https://www.contentstack.com/docs/guide/environments).  
  
## Step 3: Import content types  
  For this app, we need just one content type: Product. Here’s what it’s needed for:  
  - Product: Lets you add the session content to your app  
      
For quick integration, we have already created the content type. [Download the content type](https://github.com/contentstack/contentstack-android-graphql-example/blob/master/app/src/main/assets/ContentTypes.zip) and [import](https://www.contentstack.com/docs/guide/content-types#importing-a-content-type) it to your stack. (If needed, you can [create your own content types](https://www.contentstack.com/docs/guide/content-types#creating-a-content-type). Read more about [Content Types](https://www.contentstack.com/docs/guide/content-types)).  
  
Now that all the content types are ready, let’s add some content for your Stack.  
  
## Step 4: Adding content  
  
[Create](https://www.contentstack.com/docs/guide/content-management#add-a-new-entry) and [publish](https://www.contentstack.com/docs/guide/content-management#publish-an-entry) entries for the ‘Product’ content type.  
  
Now that we have created the sample data, it’s time to use and configure the presentation layer.  
  
## Step 5: Clone and configure application  
  
To get your app up and running quickly, we have created a sample Android app for this project. You need to download it and change the configuration. Download the app using the command given below:  
  
```
$ git clone https://github.com/contentstack/contentstack-android-graphql-example.git  
```

## Step 6: Add Gradle plugin  
  
To add the Gradle plugin, you need to first install the following dependencies into the root build.gradle file as follows:  
  
```
buildscript {
  repositories {
    jcenter()
  }
  
  dependencies {
    classpath 'com.apollographql.apollo:apollo-gradle-plugin:x.y.z'
  }
}

```

Next, add the Gradle plugin within your app module’s build.gradle file as follows:

``` 
apply plugin: 'com.apollographql.android'

dependencies {
  ...
  implementation 'com.apollographql.apollo:apollo-runtime:x.y.z'
  ...
}
 
``` 
  
Refer the [Apollo-Android](https://www.apollographql.com/docs/android/) documentation for more details on what needs to be performed to add the Apollo SDK for Android into your project.  
  
## Step 7: Download your schema  
  
In this step, you need to construct a GraphQL schema file for your content model and include the schema file in your project. This schema file is a JSON file that contains the results of introspection queries and is used by Apollo-Android for the code generation process.  
  
Download the GraphQL schema for your content model using Apollo CLI or you can use apollo-codegen as follows:

```  
apollo-codegen introspect-schema “https://graphql.contentstack.io/stacks/api_key/explore?access_token=environment-specific_delivery_token&environment=environment_name” --output schema.json  
```  

Note: Place the schema file next to your .graphql files or within the /app/src/main/graphql/com/contentstack/graphql directory.

## Step 8: Write GraphQL Queries  
  
Contentstack provides a GraphQL playground, which is a GraphiQL interface, to test your GraphQL queries in your browser. 
Use this interface to write and test your queries.
  
Open a browser of your choice and hit the URL given below (after entering the required details):  
```
https://graphql.contentstack.io/stacks/api_key/explore?access_token=environment-specific_delivery_token&environment=environment_name  
```
The following is an example of a sample query for GraphQL:  
  
 ```
 query allProduct {    
 all_Product(locale: "en-us") {    
      title    
      price    
      featured_image {    
      url    
    }}}
   ```  

Next, you need to create an instance of Apollo Client to fetch data.
 
  
## Step 9: Create ApolloClient

After downloading the schema and creating the queries, let’s create an instance of ApolloClient and point it at the GraphQL server.
Create an instance of OkHttpClient and pass it to the ApolloClient builder as follows:  
  
 ```
okHttpClient = new OkHttpClient.Builder().addInterceptor(chain -> {    
    Request original = chain.request();    
    Request.Builder builder = original.newBuilder().method(original.method(), original.body());    
    //authentications    
    //builder.header("User-Agent", "Android Apollo Client");    
        return chain.proceed(builder.build());    
    }).build();    
        
apolloClient = ApolloClient.builder().serverUrl(ContentstackApp.BASE_URL)    
    .okHttpClient(okHttpClient)    
    .build();  
```
  
This creates our Apollo Client which is ready to fetch data.  
  
## Step 10: Fetch data using ApolloClient    
   
Finally, integrate ApolloClient into the app and pass in the generated queries. write the logic for handling already-parsed responses.  
                       
```
apolloClient.query(AllProductQuery.builder().build()).enqueue(new      
ApolloCall.Callback<AllProductQuery.Data>() {    
    @Override    
    public  void  onResponse(@NotNull Response<AllProductQuery.Data> response) {    
    Log.d(TAG, response.toString());    
    }    
    @Override    
    public  void  onFailure(@NotNull ApolloException e) {    
    Log.e(TAG, e.getLocalizedMessage());    
    }    
});
```
Additionally, the snippet above sets the Stack and the Locale to be used by the client.

##  More Resources

-   [Getting started with Android SDK](https://www.contentstack.com/docs/platforms/android)
-   [Using GraphQL queries with Android SDK](https://www.contentstack.com/docs/guide/contentstack-graphql-api/using-graphql-with-contentstack-android-sdk)
-   [GraphQL API documentation ](https://www.contentstack.com/docs/apis/graphql-content-delivery-api/)

