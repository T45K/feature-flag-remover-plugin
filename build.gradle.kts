plugins {
    kotlin("jvm") version "2.2.21"

    id("com.gradle.plugin-publish") version "2.0.0"
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

group = "io.github.t45k"
version = "1.0.2"

dependencies {
    implementation("com.github.t45k:feature-flag-remover:1.0.2")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation(rootProject.libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

gradlePlugin {
    website = "https://t45k.github.io"
    vcsUrl = "https://github.com/t45k/feature-flag-remover"
    plugins {
        create("featureFlagRemover") {
            id = "io.github.t45k.feature_flag_remover"
            implementationClass = "io.github.t45k.feature_flag_remover.plugin.FeatureFlagRemoverPlugin"
            displayName = "Feature Flag Remover Plugin"
            description = "A Gradle plugin to remove feature flags from Kotlin code"
            tags = listOf("kotlin", "feature", "flag", "remover")
        }
    }
}
