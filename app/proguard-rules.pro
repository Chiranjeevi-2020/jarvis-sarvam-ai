# Keep Retrofit / Gson data models
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.sarvam.jarvis.data.remote.models.** { *; }
-keep class com.sarvam.jarvis.data.local.entities.** { *; }
