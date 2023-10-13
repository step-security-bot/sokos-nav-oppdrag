package no.nav.sokos.oppdragsinfo.api

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import no.nav.sokos.oppdragsinfo.service.UserService

fun Route.oppdragsInfoApi() {
    val userService = UserService()

    route("/api/v1/oppdragsinfo") {
        get("hello") {
            val response = userService.hello()
            println(response)
            call.respond(HttpStatusCode.OK, response)
        }
    }
}
