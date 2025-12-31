# Keep Firebase models if needed
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
}
