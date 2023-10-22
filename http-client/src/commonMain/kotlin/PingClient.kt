package opensavvy.notes.http.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import opensavvy.notes.http.shared.Ping

suspend fun HttpClient.ping(data: Int) = get("/ping") {
	contentType(ContentType.Application.Json)
	setBody(Ping(data))
}
	.takeIf { it.status.isSuccess() }
	?.body<Ping>()?.id
