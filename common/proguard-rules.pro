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


#native
-keepclasseswithmembers class * {
    native <methods>;
}

# 直播SDK 音视频SDK
-keep class com.polly.mobile.util.SdkEnvironment {
    <fields>;
    <methods>;
}

-keep class com.yysdk.mobile.venus.VenusEffectService{*;}
-keepclasseswithmembers class com.yysdk.mobile.venus.VenusEffectService$*{
    <fields>;
    <methods>;}
-keep class com.yysdk.mobile.sharedcontext.* {*;}
-keep class com.yysdk.mobile.vpsdk.EglCore10{*;}

-keep class com.polly.mobile.codec.MediaCodecDecoder2 {
    native <methods>;
    <fields>;
}

-keepclasseswithmembers class com.polly.mobile.audio.render.AudioPlayThread {
	<fields>;
    <methods>;
}

-keepclasseswithmembers class com.polly.mobile.audio.cap.AudioRecordThread {
    <fields>;
    <methods>;
}


-keepclasseswithmembers class com.polly.mobile.audio.AudioParams {
    <fields>;
    <methods>;
}

-keepclassmembers class com.polly.mobile.videosdk.YYVideoJniProxy {
    <fields>;
    <methods>;
}

-keep class com.polly.mobile.mediasdk.YYMediaJniProxy {
    <fields>;
    <methods>;
}

-keep class com.polly.mobile.codec.MediaCodecEncoder2 {
    *;
}

-keepclasseswithmembers class com.polly.mobile.codec.MediaCodecDecoder2 {
    <fields>;
    <methods>;
}

-keepclasseswithmembers class com.polly.mobile.audio.codec.AmrNbFileDecoder {
    <fields>;
    <methods>;
}

-keepclasseswithmembers class com.polly.mobile.audio.codec.AacFileDecoder {
    <fields>;
    <methods>;
}

-keep class com.polly.mobile.audio.codec.SilkFileDecoder {
    <fields>;
    <methods>;
}
