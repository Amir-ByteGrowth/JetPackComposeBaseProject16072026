# Add project specific ProGuard rules here.
# Keep data/DTO/Entity classes used by kotlinx.serialization and Room reflection.
-keepattributes *Annotation*
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
