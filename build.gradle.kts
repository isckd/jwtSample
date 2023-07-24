plugins {
	java
	id("org.springframework.boot") version "2.6.5"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"

	// kotlin
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
	kotlin("plugin.jpa") version "1.6.10"
	kotlin("kapt") version "1.6.10"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
//	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity5")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	compileOnly("org.projectlombok:lombok")
	implementation("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

	// swagger 3.0
	implementation("io.springfox:springfox-boot-starter:3.0.0")

	// jwt
	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

	// actuator LatencyUtils/PauseDetector error
	compileOnly("io.micrometer:micrometer-core:1.10.3")

	// redis
	implementation ("org.springframework.boot:spring-boot-starter-data-redis")

	// kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.10")

}

tasks.withType<Test> {
	useJUnitPlatform()
}

// kotlin 컴파일러
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}
