-dontwarn com.marusys.auto.music.network.model.**

-keepclasseswithmembers class com.marusys.auto.music.network.model.** { *; }

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation