// build.gradle.kts (Project: Aike)
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false // Asegúrate de que este alias está definido en tu TOML
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.google.gms.services) apply false
}