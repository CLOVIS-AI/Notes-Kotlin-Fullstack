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

			api(libs.prepared.suite)
			api(libs.prepared.kotest)

			api(libs.kotest.assertions)
			api(libs.kotest.engine)
		}
	}

	val jvmMain by sourceSets.getting {
		dependencies {
			api(libs.kotest.runner.junit5)
		}
	}
}

tasks.withType(Test::class) {
	testLogging {
		events("skipped", "failed", "passed")
	}
}

library {
	name.set("Test suites")
	description.set("Test suites to validate implementations of Notes Core")
	homeUrl.set("https://gitlab.com/opensavvy/notes/kotlin-fullstack")
}
