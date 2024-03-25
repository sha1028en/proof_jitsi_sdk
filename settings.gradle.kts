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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
//        maven( url = uri("file:repo/"))
        maven( url = uri("https://github.com/sha1028en/jitshi-meet-repo/raw/feature/disable_components/releases"))
//        maven( url = uri("https://github.com/sha1028en/jitshi-meet-repo/raw/main/releases"))
        google()
//        mavenLocal()
        mavenCentral()
        maven( url = uri("https://jitpack.io"))
    }
}

rootProject.name = "ProofJitsiSDK"
include(":app")
 