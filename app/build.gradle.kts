import java.util.Properties  // ✅ Ensure correct import

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.codefix"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.codefix"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ✅ Load API Key from `local.properties`
        val properties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
        }
        val apiKey = properties.getProperty("GEMINI_API_KEY", "")

        buildConfigField("String", "GEMINI_API_KEY", "\"${apiKey}\"")
    }


    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true // ✅ Use "isShrinkResources" in Kotlin DSL
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}


dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)

    // ✅ Use Firebase BoM for Auto Sync
    implementation(platform("com.google.firebase:firebase-bom:33.10.0")) // 🔄 Auto-manages Firebase versions
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    // ✅ Google Sign-In
    implementation("com.google.android.gms:play-services-auth:21.3.0")

    // ✅ Material Components (Latest)
    implementation("com.google.android.material:material:1.12.0")

    // ✅ Swiperefresh Layout
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // ✅ Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ✅ Retrofit (If Needed)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

