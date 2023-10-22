plugins {
	id("conventions.base")
	id("conventions.kotlin")
	id("conventions.library")
}

kotlin {
	jvm()
	linuxX64()

	val commonMain by sourceSets.getting {
		dependencies {
			api(projects.core)
			implementation(projects.httpShared)

			api(libs.ktor.server.core)
		}
	}
}

library {
	name.set("HTTP server")
	description.set("HTTP server to expose a Notes implementation")
	homeUrl.set("https://gitlab.com/opensavvy/notes/kotlin-fullstack")
}
