
plugins {
    id("com.marusys.android.library")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.marusys.auto.music.database"
}

dependencies {
    ksp(libs.room.compiler)

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    api(libs.room.runtime)
    api(libs.room.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}