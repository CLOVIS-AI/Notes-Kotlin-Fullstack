/*
 * This is the root project.
 *
 * The root project should remain empty. Code, etc, should happen in subprojects, and the root project should only delegate
 * work to subprojects.
 *
 * In particular, *avoid using the 'allprojects' and 'subprojects' Gradle blocks!* They slow down the configuration phase.
 */

plugins {
	id("conventions.base")
	id("conventions.root")

	// Some plugins *must* be configured on the root project.
	// In these cases, we explicitly tell Gradle not to apply them.
	alias(playgroundLibs.plugins.kotlin) apply false

	alias(playgroundLibs.plugins.dokkatoo)
}

dependencies {
	// List the 'library' projects
	dokkatoo(projects.core)
	dokkatoo(projects.coreTestDoubles)
	dokkatoo(projects.coreTestSuites)
	dokkatoo(projects.httpClient)
	dokkatoo(projects.httpServer)
	dokkatoo(projects.httpShared)

	// This is required at the moment, see https://github.com/adamko-dev/dokkatoo/issues/14
	dokkatooPluginHtml(
		dokkatoo.versions.jetbrainsDokka.map { dokkaVersion ->
			"org.jetbrains.dokka:all-modules-page-plugin:$dokkaVersion"
		}
	)
}

repositories {
	mavenCentral()
	google()
}
