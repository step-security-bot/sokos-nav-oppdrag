package no.nav.sokos.oppdragsinfo.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
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
