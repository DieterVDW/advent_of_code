/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    id("advent2023.kotlin-application-conventions")
}

dependencies {
    implementation("org.apache.commons:commons-text")
}

application {
    // Define the main class for the application.
    mainClass.set("advent2023.app.AppKt")
}