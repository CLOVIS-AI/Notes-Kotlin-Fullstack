plugins {
	id("conventions.kotlin")
}

kotlin {
	jvm()
	linuxX64()

	val commonMain by sourceSets.getting {
		dependencies {
			api(projects.core)
			implementation(projects.httpShared)
		}
	}
}
