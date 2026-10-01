plugins {
    kotlin("jvm") version "2.2.21"
    alias(libs.plugins.kotlinSerialization)

    id("io.gitlab.arturbosch.detekt") version "1.23.5"
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.postgrest)
    implementation(libs.ktor.client.cio)
    testImplementation(kotlin("test"))
    testImplementation("com.lemonappdev:konsist:0.17.3")
    testImplementation("io.mockk:mockk:1.14.11")
    implementation("io.insert-koin:koin-core:4.2.2")
    testImplementation(libs.kotlinx.coroutines.test)

}

kotlin {
    jvmToolchain(17)
}


tasks.test {
    useJUnitPlatform()
}