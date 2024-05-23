-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class io.wax911.emojify.serializer.kotlinx.**$$serializer { *; }
-keepclasseswithmembers class io.wax911.emojify.serializer.kotlinx.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep class io.wax911.emojify.serializer.kotlinx.KotlinxDeserializer { *;}
