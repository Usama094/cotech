plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
dependencies {
    implementation ("androidx.core:core-ktx:1.12.0") // Core Kotlin extensions
    implementation ("androidx.appcompat:appcompat:1.6.1" )// AppCompat library
    implementation ("com.google.android.material:material:1.11.0" )// Material Components
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4") // ConstraintLayout
    implementation ("com.google.firebase:firebase-database-ktx:20.3.1") // Firebase Realtime Database dependency
    implementation ("com.google.firebase:firebase-auth:22.3.1") // Firebase Authentication
    testImplementation ("junit:junit:4.13.2" )// JUnit for testing
    androidTestImplementation ("androidx.test.ext:junit:1.1.5" )// Android JUnit testing extension
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1") // Espresso for UI testing
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.android.volley:volley:1.2.1")
    implementation ("com.google.android.gms:play-services-location:21.2.0")
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("org.osmdroid:osmdroid-android:6.1.14")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")

}


