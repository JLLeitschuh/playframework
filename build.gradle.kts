plugins {
    groovy
    `java-gradle-plugin`
    id("com.lightbend.play.integration-test-fixtures")
    id("com.lightbend.play.integration-test")
}

group = "com.lightbend"

repositories {
    jcenter()
}

dependencies {
    testImplementation("org.spockframework:spock-core:1.2-groovy-2.4") {
        exclude(group = "org.codehaus.groovy")
    }
    integTestFixturesImplementation("com.google.guava:guava:23.0")
    integTestFixturesImplementation("org.hamcrest:hamcrest-library:1.3")
    integTestFixturesImplementation("org.apache.ant:ant:1.9.3")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

gradlePlugin {
    testSourceSets(sourceSets["integTest"])
}