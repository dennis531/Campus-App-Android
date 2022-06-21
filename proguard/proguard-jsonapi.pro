# Keep jsonapi-converter relative fields
-keepclassmembers class * {
    @com.github.jasminb.jsonapi.annotations.Id <fields>;
}

# Keep custom id handlers
-keep class * implements com.github.jasminb.jsonapi.ResourceIdHandler