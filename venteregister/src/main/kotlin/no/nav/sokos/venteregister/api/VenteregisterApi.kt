package no.nav.sokos.venteregister.api

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
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
