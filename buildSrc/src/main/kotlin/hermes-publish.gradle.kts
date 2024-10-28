plugins {
    `java-library`
    `maven-publish`
}

group = "dev.araucaris"
version = "1.0-SNAPSHOT"

java {
    withSourcesJar()
}

publishing {
    repositories {
        mavenLocal()
        maven(
            name = "araucaris",
            url = "https://repo.araucaris.dev",
            username = "MAVEN_USERNAME",
            password = "MAVEN_PASSWORD"
        )
    }
}

fun RepositoryHandler.maven(
    name: String,
    url: String,
    username: String,
    password: String,
) {
    val isSnapshot = version.toString().endsWith("-SNAPSHOT")
    this.maven {
        this.name = if (isSnapshot) "${name}Snapshots" else "${name}Releases"
        this.url = if (isSnapshot) uri("$url/snapshots") else uri("$url/releases")
        this.credentials {
            this.username = System.getenv(username)
            this.password = System.getenv(password)
        }
    }
}

interface HermesPublishExtension {
    var artifactId: String
}

val extension = extensions.create<HermesPublishExtension>("hermesPublish")

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                artifactId = extension.artifactId
                from(project.components["java"])
            }
        }
    }
}
