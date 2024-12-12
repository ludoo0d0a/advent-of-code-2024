plugins {
    kotlin("jvm") version "2.0.21"
    application
}

application {
    mainClass = "SetupKt"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.11"
    }
}

dependencies {
    val ktor_version = "2.3.0"
    implementation("io.ktor:ktor-client-core:$ktor_version")  // HTTP client
    implementation("io.ktor:ktor-client-cio:$ktor_version")   // CIO engine for Ktor
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
//    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")

    implementation(kotlin("reflect"))
    implementation("org.jsoup:jsoup:1.15.3")          // HTML parsing
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1") // For async tasks
    implementation("com.zaxxer:HikariCP:5.0.1")       // For scheduling task
    implementation("ch.qos.logback:logback-classic:1.5.6")// For logging
}
