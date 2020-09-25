import Versions.androidTestVersion
import Versions.annotationTestVersion
import Versions.appCompat
import Versions.archLifecycleVersion
import Versions.biometricVersion
import Versions.coroutinesVersion
import Versions.espressoTestVersion
import Versions.extJunitVersion
import Versions.junitVersion
import Versions.koinVersion
import Versions.kotlinVersion
import Versions.ktxCoreVersion
import Versions.ktxFragmentVersion
import Versions.ktxViewModelVersion
import Versions.mockkVersion
import Versions.roomVersion
import Versions.sdkCompileVersion
import Versions.sdkMinVersion
import Versions.sdkTargetVersion
import Versions.uiAutomatorTestVersion

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-allopen")
    id("kotlin-android-extensions")
}

val commitSHA1 = "COMMIT_SHA1"

dependencies {
    // Data and domain modules
    implementation(project(":owncloudDomain"))
    implementation(project(":owncloudData"))

    // Dependencies for app building)
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.annotation:annotation:1.1.0")
    implementation("com.google.android.material:material:1.0.0")
    implementation("com.jakewharton:disklrucache:2.0.2")
    implementation("com.google.android.exoplayer:exoplayer:r2.2.0")
    implementation("com.andrognito.patternlockview:patternlockview:1.0.0")
    implementation("androidx.appcompat:appcompat:$appCompat")
    implementation("com.getbase:floatingactionbutton:1.10.1")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.browser:browser:1.2.0")
    implementation("commons-io:commons-io:2.6")
    implementation("androidx.sqlite:sqlite:2.1.0")
    implementation("androidx.biometric:biometric:$biometricVersion")

    // Image loading
    implementation("com.github.bumptech.glide:glide:4.6.1") {
        exclude(group = "com.android.support")
    }

    //Zooming Android ImageView.
    implementation("com.github.chrisbanes:PhotoView:2.1.4")

    implementation("androidx.multidex:multidex:2.0.1")

    // Convert Java Objects into JSON and back
    implementation("com.google.code.gson:gson:2.8.6")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel:$archLifecycleVersion")
    kapt("androidx.lifecycle:lifecycle-common-java8:$archLifecycleVersion")

    implementation("androidx.room:room-runtime:$roomVersion")

    // Koin dependency injector
    implementation("org.koin:koin-core:$koinVersion")
    implementation("org.koin:koin-androidx-viewmodel:$koinVersion")

    // KTX extensions, see https://developer.android.com/kotlin/ktx.html
    implementation("androidx.core:core-ktx:$ktxCoreVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$ktxViewModelVersion")
    implementation("androidx.fragment:fragment-ktx:$ktxFragmentVersion")

    // Tests
    testImplementation(project(":owncloudTestUtil"))
    testImplementation("junit:junit:$junitVersion")
    testImplementation("androidx.arch.core:core-testing:$archLifecycleVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutinesVersion")

    // Instrumented tests
    androidTestImplementation(project(":owncloudTestUtil"))
    androidTestImplementation("androidx.test:core:$androidTestVersion")
    androidTestImplementation("androidx.test:rules:$androidTestVersion")
    androidTestImplementation("androidx.test:runner:$androidTestVersion")
    androidTestImplementation("androidx.test:runner:$androidTestVersion")
    androidTestImplementation("androidx.test.ext:junit:$extJunitVersion")
    androidTestImplementation("androidx.test:core-ktx:$androidTestVersion")
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoTestVersion")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:$espressoTestVersion")
    androidTestImplementation("androidx.test.espresso:espresso-intents:$espressoTestVersion")
    androidTestImplementation("androidx.test.espresso:espresso-web:$espressoTestVersion")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:$uiAutomatorTestVersion")
    androidTestImplementation("androidx.annotation:annotation:$annotationTestVersion")
    androidTestImplementation("androidx.arch.core:core-testing:$archLifecycleVersion")
    androidTestImplementation("io.mockk:mockk-android:$mockkVersion") {
        exclude(module = "objenesis")
    }
    androidTestImplementation("com.github.tmurakami:dexopener:2.0.4")
    androidTestImplementation("androidx.test:runner:1.3.0")
}

//tasks.withType(Test) {
//    /// increased logging for tests
//    testLogging {
//        events "passed", "skipped", "failed"
//    }
//}

//allOpen {
//    // allows mocking for classes w/o directly opening them for release builds
//    annotation 'com.owncloud.android.testing.OpenClass'
//}

android {
    compileSdkVersion(sdkCompileVersion)

    defaultConfig {
        minSdkVersion(sdkMinVersion)
        targetSdkVersion(sdkTargetVersion)

        testInstrumentationRunner = "com.owncloud.android.utils.OCTestAndroidJUnitRunner"

        versionCode = 21500200
        versionName = "2.15.2"

        buildConfigField("String", commitSHA1, "\"${getLatestGitHash()}\"")

        multiDexEnabled = true

        manifestPlaceholders = mapOf("appAuthRedirectScheme" to "")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    sourceSets {
        map { it.java.srcDir("src/${it.name}/kotlin") }
    }

    lintOptions {
        isAbortOnError = true
        isIgnoreWarnings = false

        xmlReport = false
        htmlOutput = file("../lint-app-report.html")
    }

    packagingOptions {
        exclude("META-INF/LICENSE.txt")
    }

    adbOptions {
        timeOutInMs(20 * 60 * 1000)
    }

    signingConfigs {
        create("release") {
            //if (System.getenv("OC_RELEASE_KEYSTORE") != null) {
            storeFile = file(System.getenv("OC_RELEASE_KEYSTORE"))  // use an absolute path
            storePassword = System.getenv("OC_RELEASE_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("OC_RELEASE_KEY_ALIAS")
            keyPassword = System.getenv("OC_RELEASE_KEY_PASSWORD")
            //}
        }
    }

    buildTypes {
        getByName("release") {
//            if (System.env.OC_RELEASE_KEYSTORE) {
//                signingConfig = signingConfigs.release
//            }
        }

        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
    }

    applicationVariants.all { variant ->
        val appName = System.getenv("OC_APP_NAME")
        setOutputFileName(
            variant,
            appName,
            project
        )
        true

    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        animationsDisabled = true
    }
}

// Updates output file names of a given variant to format
// [appName].[variant.versionName].[OC_BUILD_NUMBER]-[variant.name].apk.
//
// OC_BUILD_NUMBER is an environment variable read directly in this method. If undefined, it's not added.
//
// @param variant           Build variant instance which output file name will be updated.
// @param appName           String to use as first part of the new file name. May be undefined, the original
//                          project.archivesBaseName property will be used instead.
// @param callerProject     Caller project.
//
fun setOutputFileName(
    variant: com.android.build.gradle.api.ApplicationVariant,
    appName: String,
    callerProject: Project
) {
    logger.info("Setting new name for output of variant $variant.name")

//    val originalFile = variant.outputs[0].outputFile
//    val originalName = originalFile.name
//    println("originalName is $originalName")

    var newName = if (appName.isNotBlank()) {
        appName
    } else {
        "owncloud"
    }

    var versionName = "${variant.mergedFlavor.versionName}"
//    if (variant.mergedFlavor.manifestPlaceholders.versionName != null) {
//        versionName = "$variant.mergedFlavor.manifestPlaceholders.versionName"
//    }
//    if (variant.buildType.manifestPlaceholders.versionName != null) {
//        versionName = "$variant.buildType.manifestPlaceholders.versionName"
//    }
    newName += "_$versionName"

    val buildNumber = System.getenv("OC_BUILD_NUMBER")
    if (buildNumber != null) {
        newName += ".$buildNumber"
    }

//newName += originalName.substring(callerProject.archivesBaseName.length())

    println("${variant.name}: newName is $newName")

    //variant.outputFileName = File(".", newName).name
}

fun getLatestGitHash(): String = "git rev-parse --short HEAD".runCommand()

fun String.runCommand(workingDir: File = file("./")): String {
    val parts = this.split("\\s".toRegex())
    val process = ProcessBuilder(*parts.toTypedArray())
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()

    process.waitFor(1, TimeUnit.MINUTES)
    return process.inputStream.bufferedReader().readText().trim()
}
