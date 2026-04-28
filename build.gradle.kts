plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.room) apply false
    alias(libs.plugins.kover)
}

dependencies {
    kover(project(":app"))
    kover(project(":data"))
    kover(project(":domain"))
    kover(project(":player"))
}

android {
    defaultConfig {
        minSdk = 23        // was probably 26+ — change to 23
        targetSdk = 35     // keep this high
        compileSdk = 35
    }
}

kover {
    currentProject {
        createVariant("ci") {}
    }
    reports {
        variant("ci") {
            xml {
                onCheck = false
                xmlFile = layout.buildDirectory.file("reports/kover/report.xml")
            }
            html {
                onCheck = false
                htmlDir = layout.buildDirectory.dir("reports/kover/html")
            }
        }
        filters {
            excludes {
                classes(
                    "*.BuildConfig",
                    "*.Manifest",
                    "*.Manifest*",
                    "*.R",
                    "*.R$*",
                    "*.ComposableSingletons*",
                    "dagger.hilt.internal.*",
                    "hilt_aggregated_deps.*",
                    "*Hilt*",
                    "*_Factory",
                    "*_Factory$*",
                    "*_MembersInjector",
                    "*_HiltModules*"
                )
            }
        }
    }
}
