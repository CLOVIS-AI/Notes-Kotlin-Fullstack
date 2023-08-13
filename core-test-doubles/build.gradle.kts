plugins {
	id("conventions.kotlin")
}

kotlin {
	jvm()
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
