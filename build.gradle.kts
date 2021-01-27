import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("multiplatform") version "1.4.20"
    application
}

group = "me.andresantos"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://dl.bintray.com/kotlin/kotlin-js-wrappers") }
    maven { url = uri("https://dl.bintray.com/kotlin/kotlinx") }
    maven { url = uri("https://dl.bintray.com/kotlin/ktor") }
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}



kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }

    js(LEGACY) {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.ktor:ktor-client-serialization:1.5.0")
                implementation("io.ktor:ktor-client-core:1.5.0")
            }
        }
        val commonTest by getting
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-server-netty:1.5.0")
                implementation("io.ktor:ktor-html-builder:1.5.0")
                implementation("io.ktor:ktor-gson:1.5.0")
                implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
                implementation(fileTree("lib") {
                    include("paddle.jar")
                    include("paddle-java.jar")
                    include("chardet.jar")
                    include("antlr-runtime.jar")
                })
            }
            resources.srcDir("resources")
        }
        val jvmTest by getting
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains:kotlin-react:16.13.1-pre.113-kotlin-1.4.0")
                implementation("org.jetbrains:kotlin-react-dom:16.13.1-pre.113-kotlin-1.4.0")
            }
        }
        val jsTest by getting
    }
}

sourceSets {
    main {
        resources.srcDir("resources")
        kotlin {
            resources.srcDir("src/main/resources")
        }
    }
}


application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack") {
    outputFileName = "output.js"
}

tasks.getByName<Jar>("jvmJar") {
    dependsOn(tasks.getByName("jsBrowserProductionWebpack"))
    val jsBrowserProductionWebpack = tasks.getByName<KotlinWebpack>("jsBrowserProductionWebpack")
    from(File(jsBrowserProductionWebpack.destinationDirectory, jsBrowserProductionWebpack.outputFileName))
}

tasks.getByName<JavaExec>("run") {
    dependsOn(tasks.getByName<Jar>("jvmJar"))
    classpath(tasks.getByName<Jar>("jvmJar"))
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

sourceSets["main"].resources.srcDirs("resources")

tasks.register("stage") {
    dependsOn("installDist")
}





//val compileKotlin: KotlinCompile by tasks
//compileKotlin.kotlinOptions {
//    jvmTarget = "1.8"
//}
//val compileTestKotlin: KotlinCompile by tasks
//compileTestKotlin.kotlinOptions {
//    jvmTarget = "1.8"
//}