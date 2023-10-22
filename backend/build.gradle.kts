plugins {
	id("conventions.base")
	id("conventions.kotlin")
	application
}

kotlin {
	jvm {
		withJava()
	}

	val commonMain by sourceSets.getting {
		dependencies {
			implementation(projects.core)
		}
	}
}

application {
	mainClass.set("opensavvy.notes.backend.BackendKt")
}
