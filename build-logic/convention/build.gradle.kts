@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    `kotlin-dsl`
}


dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


gradlePlugin {
    plugins {
        register("AndroidLibrary") {
            id = "com.marusys.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("JvmLibrary") {
            id = "com.marusys.kotlin.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("AndroidFeature") {
            id = "com.marusys.android.feature"
            implementationClass = "AndroidFeatureConventionPlugin"
        }
        register("AndroidCompose") {
            id = "com.marusys.android.compose"
            implementationClass = "AndroidLibraryComposeConventionPlugin"
        }
        register("AndroidKoin") {
            id = "com.marusys.android.koin"
            implementationClass = "KoinConventionPlugin"
        }
        register("AndroidApplication") {
            id = "com.marusys.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("AndroidApplicationCompose") {
            id = "com.marusys.android.application.compose"
            implementationClass = "AndroidApplicationComposeConventionPlugin"
        }
    }
}