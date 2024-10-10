# yet not ready, cuz repository isn't hosted

## Getting Started

### 1. Add the Repository

To get started with Hermes, add the following repository to your project:

```kotlin
maven("https://repo.varion.dev/releases")
```

### 2. Add the Dependency

Next, include the Hermes library in your project by adding this dependency:

```kotlin
implementation("dev.varion:hermes:1.0-SNAPSHOT")
```

## deploy.yml

```yml
name: Publish hermes
on:
  push:
    branches: [ master ]
jobs:
  publish:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: 17
          distribution: 'adopt'
          cache: 'gradle'
      - name: Grant execute permission for gradlew
        run: chmod +x gradlew
      - name: Publish with Gradle
        run: ./gradlew clean build publish
        env:
          MAVEN_USERNAME: ${{ secrets.MAVEN_USERNAME }}
          MAVEN_PASSWORD: ${{ secrets.MAVEN_PASSWORD }}
```