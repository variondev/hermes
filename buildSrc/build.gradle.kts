plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("script-runtime"))
    implementation("com.gradleup.shadow:shadow-gradle-plugin:8.3.5")
}

sourceSets {
    main {
        java.setSrcDirs(emptyList<String>())
        groovy.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
    test {
        java.setSrcDirs(emptyList<String>())
        kotlin.setSrcDirs(emptyList<String>())
        groovy.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
}