package no.nav.sokos.app.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.naisApi(alive: () -> Boolean, ready: () -> Boolean) {
    route("internal") {
        get("isAlive") {
            when (alive()) {
                true -> call.respondText { "Application is alive" }
                else -> call.respondText(
                    text = "Application is not alive",
                    status = HttpStatusCode.InternalServerError
                )
            }
        }
        get("isReady") {
            when (ready()) {
                true -> call.respondText { "Application is ready" }
                else -> call.respondText(
                    text = "Application is not ready",
                    status = HttpStatusCode.InternalServerError
                )
            }
        }
    }
}
