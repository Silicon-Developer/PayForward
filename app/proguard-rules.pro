# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keep class com.payforward.app.data.** { *; }
-dontwarn okhttp3.**
-dontwarn org.json.**
