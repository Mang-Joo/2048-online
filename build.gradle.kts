import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val applicationVersion: String by project
val kotlinVersion: String by project
val jdkVersion: String by project
val junitVersion: String by project
val assertJVersion: String by project

plugins {
    java
    kotlin("jvm")
    id("org.jlleitschuh.gradle.ktlint-idea") version "11.0.0"
}

group = "io.github.gunkim"
version = applicationVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-websocket:3.0.1")
    implementation("org.springframework.boot:spring-boot-starter-security:3.0.1")
    implementation("org.springframework.boot:spring-boot-starter-mustache:3.0.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.0.1")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client:3.0.1")
    implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.6.3")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = jdkVersion
}

tasks.test {
    useJUnitPlatform()
}
