# Proguard configuration for Jackson 2.x
-dontwarn com.fasterxml.jackson.databind.**
-keepclassmembers class * {
     @com.fasterxml.jackson.annotation.* *;
}