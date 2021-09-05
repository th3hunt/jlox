plugins {
  // Apply the application plugin to add support for building a CLI application in Java.
  application
  checkstyle
}

repositories {
  // Use Maven Central for resolving dependencies.
  mavenCentral()
}

dependencies {
  // Use JUnit Jupiter for testing.
  testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")
}

application {
  // Define the main class for the application.
  mainClass.set("com.craftinginterpreters.lox.App")
}

checkstyle {
  configFile = file("checkstyle.xml")
  toolVersion = "8.45.1"
}

tasks.test {
  // Use JUnit Platform for unit tests.
  useJUnitPlatform()
}
