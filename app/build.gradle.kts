plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.cisco.external-build")
    id("org.jetbrains.kotlin.kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "cz.kureii.raintext"
    compileSdk = 34

    defaultConfig {
        applicationId = "cz.kureii.raintext"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/cpp/lib")
        }
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
    buildFeatures {
        viewBinding = true
    }
    buildToolsVersion = "34.0.0"
    ndkVersion = "25.1.8937393"
}

dependencies {
    implementation("androidx.hilt:hilt-common:1.1.0")
    val lifecycle_version = "2.6.2"
    val arch_version = "2.2.0"

    kapt("com.google.dagger:dagger-android-processor:2.48.1")
    kapt("com.google.dagger:hilt-android-compiler:2.48.1")
    kapt("androidx.hilt:hilt-compiler:1.1.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycle_version")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("com.scottyab:rootbeer-lib:0.1.0")
    implementation("androidx.biometric:biometric:1.1.0")
    implementation("com.google.dagger:dagger:2.48.1")
    implementation("com.google.dagger:hilt-android:2.48.1")
    implementation("androidx.hilt:hilt-work:1.1.0")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("androidx.activity:activity-ktx:1.8.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}

kapt {
    correctErrorTypes = true
}