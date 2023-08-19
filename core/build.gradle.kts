plugins {
	id("conventions.kotlin")
}

kotlin {
	jvm()
	linuxX64()

	val commonMain by sourceSets.getting {
		dependencies {
			api(libs.pedestal.backbone)

			api(libs.kotlinx.coroutines.core)
		}
	}
}
