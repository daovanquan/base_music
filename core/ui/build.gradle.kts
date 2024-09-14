
plugins {
    id("com.marusys.android.library")
    id("com.marusys.android.compose")
}

android {
    namespace = "com.marusys.auto.music.ui"

}

dependencies {

    api(libs.androidx.compose.foundation)
    api(libs.androidx.compose.foundation.layout)
    api(libs.androidx.compose.material.iconsExtended)
    api(libs.androidx.compose.material3)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.compose.tooling)
    api(libs.androidx.material3.window.size)
    api(libs.androidx.compose.runtime.livedata)
    api(libs.androidx.activity.compose)
    api(libs.androidx.navigation.compose)
    api(libs.coil)
    api(libs.drag.reorder)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(project(mapOf("path" to ":core:model")))
    implementation(project(mapOf("path" to ":core:store")))
    implementation(project(mapOf("path" to ":core:playback")))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    debugImplementation(libs.androidx.ui.tooling)
}