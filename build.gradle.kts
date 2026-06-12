plugins {
    kotlin("jvm") version "2.4.0"

    id("com.gradle.plugin-publish") version "2.0.0"
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

group = "io.github.t45k"
version = "1.0.3"

dependencies {
    implementation("com.github.t45k:feature-flag-remover:1.0.3")
    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("gradle-plugin"))

    testImplementation(kotlin("test-junit5"))
    testImplementation(rootProject.libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
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
