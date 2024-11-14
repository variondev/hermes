plugins {
    `hermes-java`
    `hermes-publish`
    `hermes-repositories`
}

dependencies {
    compileOnlyApi(project(":hermes-common"))
    compileOnlyApi("com.fasterxml.jackson.core:jackson-databind:2.18.1")
}

hermesPublish {
    artifactId = "hermes-codec-jackson"
}