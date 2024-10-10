plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    implementation("dev.shiza:dew:1.1.0")
}

hermesPublish {
    artifactId = "hermes"
}