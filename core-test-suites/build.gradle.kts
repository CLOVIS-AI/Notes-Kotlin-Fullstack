plugins {
	id("conventions.kotlin")
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
