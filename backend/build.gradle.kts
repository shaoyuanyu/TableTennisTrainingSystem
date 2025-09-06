val kotlin_version: String by project
val ktor_version: String by project
val kotlinx_datetime_version: String by project
val kotlinx_serialization_json_version: String by project
val exposed_version: String by project
val hikaricp_version: String by project
val mysql_connector_version: String by project
val logback_version: String by project

plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.2.3"
    kotlin("plugin.serialization") version "2.2.0"
}

group = "io.github.shaoyuanyu.ttts"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    // ktor
    implementation("io.ktor:ktor-server-core-jvm:${ktor_version}")
    implementation("io.ktor:ktor-server-netty:${ktor_version}")
    implementation("io.ktor:ktor-server-core:${ktor_version}")
    implementation("io.ktor:ktor-server-auth:${ktor_version}")
    implementation("io.ktor:ktor-server-cors:${ktor_version}")
    implementation("io.ktor:ktor-server-content-negotiation:${ktor_version}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")
    implementation("io.ktor:ktor-server-sessions:${ktor_version}")
    testImplementation("io.ktor:ktor-server-test-host:${ktor_version}")

    // kotlinx
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:${kotlinx_datetime_version}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinx_serialization_json_version}")

    // persistence
    implementation("org.jetbrains.exposed:exposed-core:${exposed_version}")
    implementation("org.jetbrains.exposed:exposed-dao:${exposed_version}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${exposed_version}")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:${exposed_version}")
    implementation("com.zaxxer:HikariCP:${hikaricp_version}")
    implementation("com.mysql:mysql-connector-j:${mysql_connector_version}")

    // logback
    implementation("ch.qos.logback:logback-classic:${logback_version}")

    // jbcrypt
    implementation("org.mindrot:jbcrypt:0.4")

    // junit
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
