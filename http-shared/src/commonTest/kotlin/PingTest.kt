package opensavvy.notes.http

import io.ktor.server.routing.*
import io.ktor.server.testing.*
import opensavvy.notes.http.client.ping
import opensavvy.notes.http.server.ping
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class PingTest {

	@Test
	fun ping() = testApplication {
		application {
			configureTest()

			routing {
				ping()
			}
		}

		val client = createClient { configureTest() }

		val id = Random.nextInt()
		assertEquals(id, client.ping(id))
	}

}
