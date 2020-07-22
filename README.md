
  
## Build an example app using Contentstack GraphQL API, and Apollo Client


We have created a sample product catalog app that is built using Apollo Android SDK. The content of this app is powered by Contentstack GraphQL APIs, and the app uses Apollo client on the client side to consume GraphQL APIs.

This document covers the steps to get this app up and running for you. Try out the app and play with it, before building bigger and better applications.  
    
<img src='https://github.com/contentstack/contentstack-android-graphql-example/blob/V8/app/src/main/assets/products.png?raw=true' width='260' height='500'/>  
  
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

Once you have downloaded the project, add your Contentstack API Key, Delivery Token, and Environment to the project during the SDK initialization step. ((Learn how to find your Stack's [API Key](https://www.contentstack.com/docs/guide/stack#edit-a-stack) and [Delivery Token](https://www.contentstack.com/docs/guide/tokens#create-a-delivery-token). Read more about [Environments](https://www.contentstack.com/docs/guide/environments).)

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
apply plugin: 'com.apollographql.apollo'

dependencies {
  ...
  implementation 'com.apollographql.apollo:apollo-runtime:x.y.z'
  ...
}
 
``` 
  
The latest Gradle plugin version is [here](https://bintray.com/apollographql/android/apollo-gradle-plugin/_latestVersion). Refer the [Apollo-Android](https://www.apollographql.com/docs/android/) documentation for more details on what needs to be performed to add the Apollo SDK for Android into your project.  
  
## Step 7: Download your schema  
  
In this step, you need to construct a GraphQL schema file for your content model and include the schema file in your project. This schema file is a JSON file that contains the results of introspection queries and is used by Apollo-Android for the code generation process.  
  
Download the GraphQL schema for your content model using Apollo CLI or you can use apollo-codegen as follows:

``` 
./gradlew downloadApolloSchema --endpoint="https://graphql.contentstack.com/stacks/<API_KEY>/explore?environment=<ENVIRONMENT_NAME>" \
  --header="access_token: <ENVIRONMENT_SPECIFIC_DELIVERY_TOKEN>" 
```  

#### Or: Download 'schema.json' Form the contentstack:

Follow the steps in the repository to download the schema file:
```
https://github.com/contentstack/contentstack-graphql-schema-download
```


Note: Place the schema file next to your .graphql files or within the /app/src/main/graphql/com/contentstack/graphql directory.

## Step 8: Write GraphQL Queries  
  
Contentstack provides a GraphQL playground, which is a GraphiQL interface, to test your GraphQL queries in your browser. 
Use this interface to write and test your queries.
  
Open a browser of your choice and hit the URL given below (after entering the required details):  
```
https://graphql.contentstack.com/stacks/<API_KEY>/explore?access_token=<ENVIRONMENT_SPECIFIC_DELIVERY_TOKEN>&environment=<ENVIRONMENT_NAME>
```
The following is an example of a sample query for GraphQL:  
  
 ```
 query ALLProducts($skip:Int, $limit:Int) {
 all_product(locale: "en-us", skip:$skip, limit:$limit){
     items{
         title
         price
         url
         description
         featured_imageConnection{
             edges{
                 node{
                     title
                     url
                 }
             }
         }
     }
 }}
   ```  

Next, you need to create an instance of Apollo Client to fetch data.
 
  
## Step 9: Create ApolloClient

After downloading the schema and creating the queries, let’s create an instance of ApolloClient and point it at the GraphQL server.
Create an instance of OkHttpClient and pass it to the ApolloClient builder as follows:  
  
 ``` 
String BASE_URL = "graphql.contentstack.com/stacks/<API_KEY>?access_token=<ENVIRONMENT_SPECIFIC_DELIVERY_TOKEN>&environment=<ENVIRONMENT_NAME>";
OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
ApolloClient apolloClient = ApolloClient.builder().serverUrl(BASE_URL).okHttpClient(okHttpClient).build();    
 ```
  
This creates our Apollo Client which is ready to fetch data.  
  
## Step 10: Fetch data using ApolloClient    
   
Finally, integrate ApolloClient into the app and pass in the generated queries. write the logic for handling already-parsed responses.  
                       
```
apolloClient.query(AllProductQuery.builder().build()).enqueue(new      
ApolloCall.Callback<AllProductQuery.Data>() {    
    @Override    
    public  void  onResponse(@NotNull Response<AllProductQuery.Data> response) {        
        response.data().all_product().items().stream().forEach(item -> {
              Log.i("Title", item.title());
              Log.i("Price", item.price().toString());
              Log.i("description", item.description());
              Log.i("image", item.featured_imageConnection().edges().get(0).node().url());
         });
    }    
    @Override    
    public  void  onFailure(@NotNull ApolloException e) {    
        Log.e(TAG, e.getLocalizedMessage());    
    }    
});
```
Additionally, the snippet above sets the Stack and the Locale to be used by the client.

##  More Resources

-   [Getting started with Android SDK](https://www.contentstack.com/docs/developers/android)
-   [Using GraphQL queries with Apollo client Android SDK](https://www.contentstack.com/docs/developers/use-the-graphql-queries-with-apollo-sdks/use-graphql-queries-with-apollo-client-android-sdk)
-   [GraphQL API documentation](https://www.contentstack.com/docs/developers/apis/graphql-content-delivery-api)

