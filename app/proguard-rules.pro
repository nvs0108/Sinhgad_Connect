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

# Gson rules
# Keep generic type information
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Keep Gson specific classes
-keep class sun.misc.Unsafe { *; }

# Keep Gson stream classes
-keep class com.google.gson.stream.** { *; }

# Keep Gson library classes
-keep class com.google.gson.** { *; }

# Keep custom model classes (replace 'com.example.myapp.models.**' with your actual package path)
-keep class com.example.myapp.models.** { *; }

# Keep custom TypeAdapters
-keep class * extends com.google.gson.TypeAdapter {
   <init>(...);
}

# Keep custom TypeAdapterFactories
-keep class * extends com.google.gson.TypeAdapterFactory {
   <init>(...);
}
