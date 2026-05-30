plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ru.mirea.vakhrushevra.yandexdriver"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.mirea.vakhrushevra.yandexdriver"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)

    implementation("com.yandex.android:maps.mobile:4.3.1-full")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}