plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    implementation(project(":hermes-common"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.0")
}

hermesPublish {
    artifactId = "hermes-serdes-jackson"
}