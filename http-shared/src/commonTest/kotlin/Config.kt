package opensavvy.notes.http

import io.ktor.client.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation.Plugin as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation

fun Application.configureTest() {
	install(ServerContentNegotiation) {
		json()
	}
}

fun HttpClientConfig<*>.configureTest() {
	install(ClientContentNegotiation) {
		json()
	}
}
