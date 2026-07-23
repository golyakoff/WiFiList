import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.bugsnag)
}

val appVersionName = "1.3.2"

base {
    archivesName.set("WiFiList_$appVersionName")
}

// Signing credentials come from the environment (CI) or Gradle properties
// (e.g. ~/.gradle/gradle.properties locally). Both lookups are configuration
// cache friendly. When nothing is set the release build stays unsigned.
fun secret(name: String): String? =
    providers.environmentVariable(name).orNull ?: providers.gradleProperty(name).orNull

val releaseKeystore = secret("KEYSTORE_FILE")

android {
    compileSdk = 35
    namespace = "tk.zwander.wifilist"

    defaultConfig {
        applicationId = "tk.zwander.wifilist"
        minSdk = 30
        targetSdk = 35
        versionCode = 13
        versionName = appVersionName

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            releaseKeystore?.let { keystore ->
                storeFile = rootProject.file(keystore)
                storePassword = secret("KEYSTORE_PASSWORD")
                keyAlias = secret("KEY_ALIAS")
                keyPassword = secret("KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            // Without credentials this stays null and the APK is emitted as
            // -release-unsigned.apk, which Android refuses to install.
            signingConfig = releaseKeystore?.let { signingConfigs.getByName("release") }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    implementation(libs.compose.ui)
    implementation(libs.compose.compiler)
    implementation(libs.core.ktx)
    implementation(libs.compose.material3)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(libs.fragment.ktx)
    implementation(libs.datastore.preferences)

    implementation(libs.shizuku.api)
    implementation(libs.shizuku.provider)
    implementation(libs.hiddenapibypass)
    implementation(libs.material)

    implementation(libs.patreonSupportersRetrieval)
    implementation(libs.gson)
    implementation(libs.bugsnag.android)
    implementation(libs.fastcsv)
    implementation(libs.relinker)
}