package no.nav.sokos.venteregister.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.sokos.venteregister.service.UserService

fun Route.venteregisterApi() {
    val helloService = UserService()

    route("/api/v1/venteregister") {
        get("hello") {
            val response = helloService.hello()
            call.respond(HttpStatusCode.OK, response)
        }
    }
}
