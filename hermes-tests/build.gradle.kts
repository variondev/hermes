plugins {
    `hermes-unit`
    `hermes-repositories`
}

dependencies {
    testImplementation(project(":hermes-common"))
    testImplementation(project(":hermes-serdes:hermes-serdes-jackson"))
    testImplementation(project(":hermes-platforms:hermes-platform-redis"))
}