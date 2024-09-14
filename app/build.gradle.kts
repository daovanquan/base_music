import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.marusys.android.application")
    id("com.marusys.android.application.compose")
}

android {
    namespace = "com.marusys.auto.music"

    defaultConfig {
        applicationId = "com.marusys.auto.music"

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("autovaz") {
            storeFile = file("../keystore/platform.jks")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("autovaz")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }

        debug {
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("autovaz")
            resValue("string", "app_name", "Music debug")
        }
    }


    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    applicationVariants.all {
        val variantName = name
        sourceSets {
            getByName("main") {
                java.srcDir(File("build/generated/ksp/$variantName/kotlin"))
            }
        }
    }
}


dependencies {
    implementation(libs.core.ktx)
    implementation(libs.media3.session)
    implementation(libs.media3.exoplayer)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.timber)
    implementation(projects.feature.songs)
    implementation(projects.feature.albums)
    implementation(projects.core.ui)
    implementation(projects.core.store)
    implementation(projects.core.model)
    implementation(projects.core.playback)
    implementation(projects.feature.playlists)
    implementation(projects.feature.nowplaying)
    implementation(projects.feature.settings)
    implementation(projects.feature.tageditor)
    implementation(projects.feature.widgets)
    api(libs.accompanist.permissions)

    implementation(libs.androidx.adaptive)
    implementation(libs.androidx.adaptive.layout)
    implementation(libs.adaptive.navigation)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}