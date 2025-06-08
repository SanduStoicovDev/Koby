plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.unimib.koby"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.unimib.koby"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        isCoreLibraryDesugaringEnabled = true
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {



    //Design UI
    implementation(libs.appcompat)
    implementation (libs.cardview)
    implementation (libs.recyclerview)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    //Navigation Lifecycle
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation (libs.navigation.fragment.ktx)
    implementation (libs.navigation.ui.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.database)
    implementation(libs.google.firebase.auth)
    implementation(libs.play.services.auth) //login google
    implementation(libs.play.services.base)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Retrofit & Converter GSON
    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    // Validator
    implementation(libs.commons.validator)
    implementation(libs.security.crypto.v110alpha07)

    // OkHttp & Logging Interceptor per OkHttp
    implementation (libs.okhttp)
    implementation (libs.logging.interceptor)

    // Room - Database
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)

    // Picasso Image & PDF Parser
    implementation (libs.picasso)
    implementation(libs.pdfbox.android)

    //Testing JUnit Espresso
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}