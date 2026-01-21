plugins {
    id("java")
    id("com.gradleup.shadow") version "9.3.0"
}

group = "codes.reactor"
version = "1.0.0"

repositories {
    mavenCentral()
}

val appDataDir = System.getenv("APPDATA") ?: (System.getProperty("user.home") + "/AppData/Roaming")
val hytaleServerJar = file("$appDataDir/Hytale/install/release/package/game/latest/Server/HytaleServer.jar")

if (!hytaleServerJar.exists()) {
    throw GradleException("Can't found HytaleServer.jar in: $hytaleServerJar")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")

    compileOnly("org.jetbrains:annotations:26.0.2-1")

    compileOnly(files(hytaleServerJar))
    testRuntimeOnly(files(hytaleServerJar))

    implementation("org.snakeyaml:snakeyaml-engine:3.0.1")
}

tasks.withType<JavaCompile>().configureEach {
    doFirst {
        if (!hytaleServerJar.exists()) {
            throw GradleException("Can't found HytaleServer.jar in: $hytaleServerJar")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveBaseName = "ReactorSDK"
}

allprojects {
    apply<JavaPlugin>()

    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(25)
    }

    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    }
}