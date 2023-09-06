plugins {
	id("conventions.kotlin")
	alias(libs.plugins.kotlinx.serialization)
}

kotlin {
	jvm()
	linuxX64()

	val commonMain by sourceSets.getting {
		dependencies {
			api(libs.kotlinx.serialization.core)
		}
	}

	val commonTest by sourceSets.getting {
		dependencies {
			implementation(libs.kotlin.test)

			implementation(libs.kotlinx.serialization.json)
			implementation(libs.kotlinx.coroutines.test)
		}
	}
}
