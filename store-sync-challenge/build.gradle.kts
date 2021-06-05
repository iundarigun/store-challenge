import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"

	kotlin("jvm") version "1.5.10"
	kotlin("plugin.spring") version "1.5.10"
	kotlin("plugin.jpa") version "1.5.10"

	id("io.gitlab.arturbosch.detekt") version "1.17.1"
}

group = "br.com.devcave.store"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

val springCloudVersion = "2020.0.3"
val detektVersion = "1.17.1"
val testContainersVersion = "1.15.3"
val openApiVersion = "1.5.9"
val fakerVersion = "1.0.2"
val mockkVersion = "1.11.0"

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	runtimeOnly("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core")

	// Cache
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	// Client http
	implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

	// Tracing and monitoring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.cloud:spring-cloud-starter-sleuth")

	// Swagger
	implementation("org.springdoc:springdoc-openapi-ui:$openApiVersion")

	// Test dependencies
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.rest-assured:rest-assured")
	testImplementation("com.github.javafaker:javafaker:$fakerVersion")
	testImplementation("io.mockk:mockk:$mockkVersion")
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