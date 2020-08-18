plugins {
    java
    kotlin("jvm") version "1.3.72"
}

group = "org.example"
version = "1.0-SNAPSHOT"

buildscript{

}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testCompile("junit", "junit", "4.12")
}
