import com.marusys.auto.music.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.kapt")
            dependencies {
                add("implementation", libs.findLibrary("koin-android").get())
                add("implementation", libs.findLibrary("koin-android-compose").get())
                add("implementation", libs.findLibrary("koin-annotations").get())
                add("testImplementation", libs.findLibrary("koin-test").get())
                add("androidTestImplementation", libs.findLibrary("koin-test").get())
                add("testImplementation", libs.findLibrary("koin-test-junit").get())
            }
        }
    }
}