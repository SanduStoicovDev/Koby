plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

val openAiKey: String by project

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

        buildConfigField("String", "OPENAI_API_KEY", "\"$openAiKey\"")
        println("▶ BuildConfig debug – OPENAI = ${openAiKey.takeLast(4)}")
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
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    //Design UI
    implementation(libs.appcompat)
    implementation (libs.cardview)
    implementation (libs.recyclerview)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.transition)
    implementation(libs.material.v1110)
    implementation(libs.lottie)

    //Navigation Lifecycle
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation (libs.navigation.fragment.ktx)
    implementation (libs.navigation.ui.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)


    implementation(libs.firebase.firestore.v2500)
    implementation(libs.firebase.auth.v2300)

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.14.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.firebaseui:firebase-ui-firestore:9.0.0")
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
    implementation(libs.tom.roush.pdfbox.android)

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
    implementation("com.tom-roush:pdfbox-android:2.0.27.0")

    //Testing JUnit Espresso
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}