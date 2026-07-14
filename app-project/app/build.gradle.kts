import java.util.Properties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.serialization)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.ksp)
}

// ─── Signing credentials ────────────────────────────────────────
// Load from keystore.properties (gitignored, local only)
// Fall back to environment variables for CI/CD
val keystoreProps = Properties().apply {
    val propsFile = rootProject.file("app/keystore.properties")
    if (propsFile.exists()) {
        load(propsFile.inputStream())
        logger.lifecycle("Loaded signing credentials from keystore.properties")
    } else {
        logger.warn("keystore.properties not found — falling back to env vars")
    }
}

fun String?.orEnv(key: String, fallback: String = ""): String =
    this?.takeIf { it.isNotBlank() }
        ?: System.getenv(key)
        ?: fallback

android {
    namespace = "com.stufflocate.app"
    compileSdk = 36

    signingConfigs {
        create("release") {
            storeFile = project.file(
                keystoreProps.getProperty("storeFile")?.orEnv("STORE_FILE") ?: "keystore.jks"
            )
            storePassword = keystoreProps.getProperty("storePassword").orEnv("STORE_PASSWORD")
            keyAlias = keystoreProps.getProperty("keyAlias").orEnv("KEY_ALIAS")
            keyPassword = keystoreProps.getProperty("keyPassword").orEnv("KEY_PASSWORD")
        }
    }

    defaultConfig {
        applicationId = "com.stufflocate.app"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
      compose = true
      aidl = false
      buildConfig = false
      shaders = false
    }
    packaging {
      resources {
        excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
    }
}

kotlin {
    jvmToolchain(17)
}

// ─── Rename APKs after build ────────────────────────────────────
gradle.buildFinished {
    listOf("debug", "release").forEach { variant ->
        val apkDir = file("build/outputs/apk/$variant")
        if (!apkDir.exists()) return@forEach
        apkDir.listFiles { f -> f.name.endsWith(".apk") && f.name.startsWith("app-") }?.forEach { apk ->
            val newName = apk.name.replace("app-", "StuffLocate-")
            if (apk.renameTo(apkDir.resolve(newName))) {
                logger.lifecycle("APK renamed: ${apk.name} -> $newName")
            }
        }
    }
}

dependencies {
  val composeBom = platform(libs.androidx.compose.bom)
  implementation(composeBom)
  androidTestImplementation(composeBom)

  // Core Android dependencies
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)

  // Arch Components
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.viewmodel.compose)

  // Compose
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  // Tooling
  debugImplementation(libs.androidx.compose.ui.tooling)
  // Instrumented tests
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  debugImplementation(libs.androidx.compose.ui.test.manifest)

  // Local tests: jUnit, coroutines, Android runner
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)

  // Instrumented tests: jUnit rules and runners
  androidTestImplementation(libs.androidx.test.core)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.test.espresso.core)

  // Navigation
  implementation(libs.androidx.navigation3.ui)
  implementation(libs.androidx.navigation3.runtime)
  implementation(libs.androidx.lifecycle.viewmodel.navigation3)

  // Room
  implementation(libs.room.runtime)
  implementation(libs.room.ktx)
  ksp(libs.room.compiler)

  // Serialization
  implementation(libs.kotlinx.serialization.json)

  // CameraX
  implementation(libs.androidx.camera.core)
  implementation(libs.androidx.camera.camera2)
  implementation(libs.androidx.camera.lifecycle)
  implementation(libs.androidx.camera.view)

  // Image loading
  implementation(libs.coil.compose)
}

