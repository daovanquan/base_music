plugins {
    id("com.marusys.android.library")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.marusys.auto.music.testing"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    defaultConfig {
        testInstrumentationRunner = "com.marusys.auto.music.testing.MusicTestRunner"
    }
}

dependencies {
    api(projects.core.store)
    api(projects.core.model)
    api(projects.core.database)
    api(projects.core.network)
    api(projects.core.playback)

//    implementation(libs.room.runtime)
//    implementation(libs.room.ktx)
    implementation(libs.kotlin.coroutine.test)
    implementation(libs.mockito)
    implementation(libs.media3.session)
    api(libs.android.test.rules)
    api(libs.android.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}