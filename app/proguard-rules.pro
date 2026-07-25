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

# Preserve line number information so Play Console can map obfuscated
# stack traces back to source lines using the uploaded mapping file.
-keepattributes SourceFile,LineNumberTable

# Line numbers are kept above, so the original file name is redundant.
-renamesourcefileattribute SourceFile