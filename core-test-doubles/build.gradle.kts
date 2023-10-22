plugins {
	id("conventions.base")
	id("conventions.kotlin")
	id("conventions.library")
	alias(libs.plugins.kotest)
}

kotlin {
	jvm {
		testRuns.named("test") {
			executionTask.configure {
				useJUnitPlatform()
			}
		}
	}
	linuxX64()

	val commonMain by sourceSets.getting {
		dependencies {
			api(projects.core)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(projects.coreTestSuites)
		}
	}
}

library {
	name.set("Test doubles")
	description.set("Fake implementations and fixtures to help test implementations of Notes Core")
	homeUrl.set("https://gitlab.com/opensavvy/notes/kotlin-fullstack")
}
