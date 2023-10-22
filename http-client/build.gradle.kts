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

			api(libs.ktor.client.core)
		}
	}
}

library {
	name.set("HTTP client")
	description.set("HTTP client to communicate with a Notes backend")
	homeUrl.set("https://gitlab.com/opensavvy/notes/kotlin-fullstack")
}
