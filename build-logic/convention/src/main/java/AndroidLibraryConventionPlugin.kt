
import com.android.build.gradle.LibraryExtension
import com.marusys.auto.music.convention.configureKotlinAndroid
import com.marusys.auto.music.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("com.marusys.android.koin")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 34
            }
            dependencies {
                add("implementation", libs.findLibrary("timber").get())
                add("androidTestImplementation", kotlin("test"))
                add("testImplementation", kotlin("test"))
                add("testImplementation", libs.findLibrary("mockito").get())
            }
        }
    }
}