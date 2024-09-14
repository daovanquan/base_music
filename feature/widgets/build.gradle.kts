plugins {
    id("com.marusys.android.feature")
    id("com.marusys.android.compose")
}

android {
    namespace = "com.marusys.auto.music.widgets"
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(project(mapOf("path" to ":core:ui")))
    implementation(project(mapOf("path" to ":core:store")))
    implementation(project(mapOf("path" to ":core:model")))
    implementation(project(mapOf("path" to ":core:playback")))
    implementation(project(mapOf("path" to ":core:database")))
    implementation(libs.glance)
    implementation(libs.glance.material)
    implementation(libs.androidx.media3.session)
    implementation("androidx.palette:palette:1.0.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}