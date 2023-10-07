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
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(projects.coreTestSuites)
		}
	}
}
