plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.ciluu.smcl"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.formdev:flatlaf:+")
    implementation("com.formdev:flatlaf-intellij-themes:+")
    implementation("com.formdev:flatlaf-extras:+")
    implementation("org.glavo.hmcl:hmcl-dev:+")
    implementation("cn.hutool:hutool-setting:+")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    minimize {
        exclude("/assets/about/**")
        exclude("/assets/css/**")
        exclude("/assets/game/**")
        exclude("/assets/img/**")
        exclude("/assets/lang/**")
        exclude("/assets/hmcl*")
        exclude("/assets/HMCL*")
        exclude("/assets/mod*")
   }

    manifest {
        attributes(
            "Main-Class" to "com.ciluu.smcl.Main",
            "Add-Opens" to listOf(
                "java.base/java.lang",
                "java.base/java.lang.reflect",
                "java.base/jdk.internal.loader",
                "javafx.base/com.sun.javafx.binding",
                "javafx.base/com.sun.javafx.event",
                "javafx.base/com.sun.javafx.runtime",
                "javafx.graphics/javafx.css",
                "javafx.graphics/com.sun.javafx.stage",
                "javafx.graphics/com.sun.prism",
                "javafx.controls/com.sun.javafx.scene.control",
                "javafx.controls/com.sun.javafx.scene.control.behavior",
                "javafx.controls/javafx.scene.control.skin",
                "jdk.attach/sun.tools.attach",
            ).joinToString(" "),
            "Enable-Native-Access" to "ALL-UNNAMED"
        )
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.jar {
    enabled = false
    dependsOn(tasks["shadowJar"])
}

tasks.withType<JavaExec> {
    jvmArgs(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
        "--add-opens", "java.base/jdk.internal.loader=ALL-UNNAMED"
    )
}

javafx {
    configuration = "compileOnly"
    version = "17"
    modules("javafx.controls", "javafx.media")
}
