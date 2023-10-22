plugins {
	id("conventions.base")
	id("conventions.kotlin")
	id("conventions.library")
}

kotlin {
	jvm()
}

kotlin {
	jvm()
	linuxX64()

	val commonMain by sourceSets.getting {
		dependencies {
			api(libs.pedestal.backbone)
			api(libs.pedestal.state)
			api(libs.pedestal.state.arrow)

			api(libs.kotlinx.coroutines.core)
		}
	}
}

library {
	name.set("Core")
	description.set("Domain objects and contracts for OpenSavvy Notes")
	homeUrl.set("https://gitlab.com/opensavvy/notes/kotlin-fullstack")
}
