# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\software\exesdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontoptimize

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends io.dcloud.DHInterface.IPlugin
-keep public class * extends io.dcloud.DHInterface.IFeature
-keep public class * extends io.dcloud.DHInterface.IBoot
-keep public class * extends io.dcloud.DHInterface.IReflectAble
-keep public class * extends io.dcloud.js.geolocation.IGeoManager


-keep class com.** {*;}

-keep class io.dcloud.net.** {*;}
-keep class io.dcloud.DHInterface.** {*;}
-keep class io.dcloud.adapter.** {*;}
-keep class io.dcloud.util.** {*;}
-keep class io.dcloud.constant.** {*;}
-keep class io.dcloud.feature.speech.** {*;}
-keep class io.dcloud.feature.payment.** {*;}
-keep class io.dcloud.sdk.** {*;}
-keep class vi.com.gdi.** {*;}
-keep class android.support.v4.** {*;}


-keepclasseswithmembers class * extends io.dcloud.js.geolocation.GeoManagerBase {
    <methods>;
}

-keep class io.dcloud.share.AbsWebviewClient
-keepclasseswithmembers class io.dcloud.share.AbsWebviewClient {
    <methods>;
}

-keep class io.dcloud.share.ShareAuthorizeView
-keepclasseswithmembers class io.dcloud.share.ShareAuthorizeView {
    <methods>;
}

-keep class io.dcloud.share.IFShareApi
-keep public class * extends io.dcloud.share.IFShareApi
-keepclasseswithmembers class io.dcloud.share.IFShareApi {
    <methods>;
}


-keepattributes Exceptions,InnerClasses,Signature,Deprecated, SourceFile,LineNumberTable,*Annotation*,EnclosingMethod

-keepclasseswithmembers class io.dcloud.EntryProxy {
    <methods>;
}

-keepclasseswithmembers class *{
	public static java.lang.String getJsContent();
}
-keepclasseswithmembers class io.dcloud.appstream.StreamAppScriptEntry {
    <methods>;
}
-keepclasseswithmembers class *{
	public static void onReceiver1(android.content.Intent, android.content.Context);
}

-keepclasseswithmembers class *{
	public static io.dcloud.share.AbsWebviewClient getWebviewClient(io.dcloud.share.ShareAuthorizeView);
}

-keepclasseswithmembers class *{
	public java.lang.String exec(java.lang.String,java.lang.String,java.lang.String[]);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

