import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.0"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.spring") version "1.5.10"

    id("io.gitlab.arturbosch.detekt") version "1.17.1"
}

group = "br.com.devcave.store"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

val springCloudVersion = "2020.0.3"
val detektVersion = "1.17.1"
val testContainersVersion = "1.15.3"
val openApiVersion = "1.5.9"
val fakerVersion = "1.0.2"

dependencies {
    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // webflux and validation
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Kotlin reactor and coroutines
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Database
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("io.r2dbc:r2dbc-postgresql")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.postgresql:postgresql")

    // Tracing and monitoring
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-webflux-ui:$openApiVersion")

    // Tests dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("com.github.javafaker:javafaker:$fakerVersion")
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

task<Test>("unitTests") {
    useJUnitPlatform {
        excludeTags("integration")
        includeTags("unit")
    }
}

task<Test>("integrationTests") {
    useJUnitPlatform {
        includeTags("integration")
        excludeTags("unit")
    }
}

detekt {
    input = files("src/main/kotlin", "src/test/kotlin")
    config = files("custom-detekt.yml")
}
