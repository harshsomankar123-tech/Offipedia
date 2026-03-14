import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.detekt)
    alias(libs.plugins.paparazzi)
    alias(libs.plugins.kover)
}

kotlin {
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.koin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
        androidUnitTest.dependencies {
            implementation(libs.kotlin.test.junit)
            implementation(libs.junit)
            implementation(libs.robolectric)
            implementation(libs.mockk.android)
        }
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }

    sourceSets {
        val desktopMain by getting
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            
            implementation(libs.kotlinx.serialization.json)
            
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            
            implementation(libs.navigation.compose)
        }
        
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.kotlinx.coroutines.android)
        }
        
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        
        desktopMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.swing)
        }

        dependencies {
            ksp(libs.room.compiler)
        }
    }
}

android {
    namespace = "com.plcoding.bookpedia"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.plcoding.bookpedia"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}

compose.desktop {
    application {
        mainClass = "com.plcoding.bookpedia.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.plcoding.bookpedia"
            packageVersion = "1.0.0"
        }
    }
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "*.ComposableSingletons*",
                    "*_Composable*",
                    "*.components.*",
                    "*ScreenKt*",
                    "*PreviewKt*",
                    "*.di.*",
                    "*.app.Route*",
                    "com.plcoding.bookpedia.MainKt",
                    "offipedia.composeapp.generated.*",
                    "com.plcoding.bookpedia.core.presentation.*",
                    "com.plcoding.bookpedia.book.presentation.*.components.*",
                    "*_Impl*",
                    "*_Impl$*",
                    "*Action*",
                    "*Event*",
                    "*State",
                    "*Route",
                    "*Dto",
                    "*Dto$*"
                )
                packages(
                    "com.plcoding.bookpedia.di",
                    "com.plcoding.bookpedia.core.data",
                    "com.plcoding.bookpedia.core.presentation",
                    "com.plcoding.bookpedia.app",
                    "offipedia.composeapp.generated",
                    "com.plcoding.bookpedia.core.domain"
                )
                annotatedBy("androidx.compose.runtime.Composable")
            }
        }
    }
}
