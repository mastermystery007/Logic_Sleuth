plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  val testAdMobAppId = "ca-app-pub-3940256099942544~3347511713"
  val testRewardedAdUnitId = "ca-app-pub-3940256099942544/5224354917"
  val productionAdMobAppId = "ca-app-pub-3812214492151514~6406689884"
  val productionRewardedRevealLiarId = "ca-app-pub-3812214492151514/3968917161"
  val productionRewardedRevealSolutionId = "ca-app-pub-3812214492151514/9169272957"

  defaultConfig {
    applicationId = "com.mysterybox.deduceit"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    manifestPlaceholders["adMobAppId"] = testAdMobAppId
    buildConfigField("String", "ADMOB_APP_ID", "\"$testAdMobAppId\"")
    buildConfigField("String", "ADMOB_REWARDED_CHECK_ANSWER_ID", "\"$testRewardedAdUnitId\"")
    buildConfigField("String", "ADMOB_REWARDED_REVEAL_LIAR_ID", "\"$testRewardedAdUnitId\"")
    buildConfigField("String", "ADMOB_REWARDED_REVEAL_SOLUTION_ID", "\"$testRewardedAdUnitId\"")

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  val keystorePath = System.getenv("KEYSTORE_PATH") ?: project.findProperty("KEYSTORE_PATH")?.toString()
  val storePassword = System.getenv("STORE_PASSWORD") ?: project.findProperty("STORE_PASSWORD")?.toString()
  val keyPassword = System.getenv("KEY_PASSWORD") ?: project.findProperty("KEY_PASSWORD")?.toString()

  val hasReleaseSigning = keystorePath != null &&
      storePassword != null &&
      keyPassword != null

  signingConfigs {
    create("release") {
      if (hasReleaseSigning) {
        storeFile = file(keystorePath)
        this.storePassword = storePassword
        keyAlias = "upload"
        this.keyPassword = keyPassword
      }
    }
  }

  buildTypes {
    debug {
      manifestPlaceholders["adMobAppId"] = testAdMobAppId
      buildConfigField("String", "ADMOB_APP_ID", "\"$testAdMobAppId\"")
      buildConfigField("String", "ADMOB_REWARDED_CHECK_ANSWER_ID", "\"$testRewardedAdUnitId\"")
      buildConfigField("String", "ADMOB_REWARDED_REVEAL_LIAR_ID", "\"$testRewardedAdUnitId\"")
      buildConfigField("String", "ADMOB_REWARDED_REVEAL_SOLUTION_ID", "\"$testRewardedAdUnitId\"")
    }
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

      manifestPlaceholders["adMobAppId"] = productionAdMobAppId
      buildConfigField("String", "ADMOB_APP_ID", "\"$productionAdMobAppId\"")
      // The app currently uses the solution unit for both answer checking and full-solution reveal.
      buildConfigField("String", "ADMOB_REWARDED_CHECK_ANSWER_ID", "\"$productionRewardedRevealSolutionId\"")
      buildConfigField("String", "ADMOB_REWARDED_REVEAL_LIAR_ID", "\"$productionRewardedRevealLiarId\"")
      buildConfigField("String", "ADMOB_REWARDED_REVEAL_SOLUTION_ID", "\"$productionRewardedRevealSolutionId\"")

      signingConfig = if (hasReleaseSigning) {
        signingConfigs.getByName("release")
      } else {
        null
      }
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  // implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  // implementation(libs.firebase.ai)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  implementation(libs.play.services.ads)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}

tasks.configureEach {
    if (name == "assembleRelease" || name == "bundleRelease") {
        val keystorePathConfig = System.getenv("KEYSTORE_PATH") ?: project.findProperty("KEYSTORE_PATH")?.toString()
        val storePasswordConfig = System.getenv("STORE_PASSWORD") ?: project.findProperty("STORE_PASSWORD")?.toString()
        val keyPasswordConfig = System.getenv("KEY_PASSWORD") ?: project.findProperty("KEY_PASSWORD")?.toString()
        val hasReleaseSigningConfig = keystorePathConfig != null && storePasswordConfig != null && keyPasswordConfig != null

        doFirst {
            if (!hasReleaseSigningConfig) {
                throw GradleException(
                    "Release signing configuration is missing. " +
                    "Set KEYSTORE_PATH, STORE_PASSWORD, and KEY_PASSWORD before running assembleRelease or bundleRelease."
                )
            }
        }
    }
}
