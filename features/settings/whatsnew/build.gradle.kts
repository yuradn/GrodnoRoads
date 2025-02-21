plugins {
    id("grodnoroads.library")
    id("grodnoroads.library.compose")
}

android {
    namespace = "com.egoriku.grodnoroads.settings.whatsnew"
}

dependencies {
    implementation(projects.libraries.crashlytics)
    implementation(projects.libraries.extensions)
    implementation(projects.libraries.foundation)
    implementation(projects.libraries.resources)

    implementation(projects.shared.appSettings)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.decompose)
    implementation(libs.decompose.compose.jetpack)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)

    implementation(libs.koin.android)

    implementation(libs.mvikotlin)
    implementation(libs.mvikotlin.extensions)
    implementation(libs.mvikotlin.main)
}