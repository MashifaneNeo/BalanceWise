pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
<<<<<<< HEAD
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
=======
>>>>>>> 1268648ee3a6dcf37bdae6bcdceb0d2a642ef3bb
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

<<<<<<< HEAD
rootProject.name = "BalanceWise"
include(":app")
 
=======
rootProject.name = "Balance_Wise"
include(":app")
>>>>>>> 1268648ee3a6dcf37bdae6bcdceb0d2a642ef3bb
