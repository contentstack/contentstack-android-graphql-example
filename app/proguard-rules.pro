# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Apollo GraphQL v4 ProGuard Rules
-keep class com.apollographql.apollo.** { *; }
-keep class * extends com.apollographql.apollo.api.Query { *; }
-keep class * extends com.apollographql.apollo.api.Mutation { *; }
-keep class * extends com.apollographql.apollo.api.Subscription { *; }
-keep class * extends com.apollographql.apollo.api.Fragment { *; }
-keepclassmembers class * { @com.apollographql.apollo.api.* <fields>; }

# Keep generated GraphQL models
-keep class com.contentstack.graphql.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
