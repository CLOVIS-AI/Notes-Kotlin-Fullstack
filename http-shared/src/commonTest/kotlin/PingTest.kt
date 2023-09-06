package opensavvy.notes.http

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import opensavvy.note.http.shared.Ping
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation.Plugin as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation

class PingTest {

	@Test
	fun ping() = testApplication {
		application {
			install(ServerContentNegotiation) {
				json()
			}

			routing {
				get("/ping") {
					val body = call.receive<Ping>()
					call.respond(body)
				}
			}
		}

		val client = createClient {
			install(ClientContentNegotiation) {
				json()
			}
		}

		val id = Random.nextInt()

		val response = client.get("/ping") {
			setBody(Ping(id))
			contentType(ContentType.Application.Json)
		}
		assertEquals(HttpStatusCode.OK, response.status)
		assertEquals(id, response.body<Ping>().id)
	}

}
