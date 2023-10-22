plugins {
	id("conventions.base")
	id("conventions.kotlin")
	id("conventions.library")
	alias(playgroundLibs.plugins.kotlinx.serialization)
}

kotlin {
	jvm()
	linuxX64()

	val commonMain by sourceSets.getting {
		dependencies {
			api(libs.kotlinx.serialization.core)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(playgroundLibs.kotlin.test)

			implementation(projects.httpClient)
			implementation(projects.httpServer)

			implementation(libs.kotlinx.serialization.json)
			implementation(libs.kotlinx.coroutines.test)

			implementation(libs.ktor.server.testHost)
			implementation(libs.ktor.server.contentNegotiation)
			implementation(libs.ktor.client.contentNegotiation)
			implementation(libs.ktor.serialization.kotlinxJson)
		}
	}
}

library {
	name.set("HTTP common code")
	description.set("Common code between the HTTP client and server (DTOs…)")
	homeUrl.set("https://gitlab.com/opensavvy/notes/kotlin-fullstack")
}
