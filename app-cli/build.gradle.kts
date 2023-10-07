plugins {
	id("conventions.kotlin")
	application
}

kotlin {
	jvm {
		withJava()
	}
	linuxX64 {
		binaries {
			executable {
				entryPoint("opensavvy.notes.cli.main")
			}
		}
	}

	val commonMain by sourceSets.getting {
		dependencies {
			implementation(projects.core)

			implementation(libs.kotter)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(libs.kotlin.test)
		}
	}
}

application {
	mainClass.set("opensavvy.notes.cli.MainKt")
}
