package opensavvy.notes.http.server

import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import opensavvy.notes.http.shared.Ping

fun Route.ping() {
	get("/ping") {
		val body = call.receive<Ping>()
		call.respond(body)
	}
}
