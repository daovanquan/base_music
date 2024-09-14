
plugins {
    id("com.marusys.android.library")
}

android {
    namespace = "com.marusys.auto.music.store"

    buildTypes {
        release {
            consumerProguardFile("proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.datastore)
    implementation(libs.jaudio.tagger)
    implementation(libs.media3.session)
    implementation(projects.core.model)
    implementation(projects.core.database)
    implementation(projects.core.network)
    testImplementation(libs.junit)
    testImplementation(projects.core.database)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}