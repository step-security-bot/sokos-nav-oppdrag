package no.nav.sokos.oppdragsinfo.api

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import no.nav.sokos.oppdragsinfo.api.model.*
import no.nav.sokos.oppdragsinfo.service.OppdragsInfoService

private const val BASE_PATH = "/api/v1"

fun Route.oppdragsInfoApi(
    oppdragsInfoService: OppdragsInfoService = OppdragsInfoService()
) {
    route(BASE_PATH) {
        get("oppdrag/{oppdragsId}") {
            val response = OppdragsInfoResponse(
                oppdragsInfoService.hentOppdrag(
                    call.parameters.get("oppdragsId").orEmpty(),
                    call
                )
            )
            call.respond(response)
        }

        post("oppdrag") {
            val oppdragsSokRequest: OppdragsSokRequest = call.receive()
            val response = OppdragsSokResponse(
                oppdragsInfoService.sokOppdrag(
                    oppdragsSokRequest,
                    call
                )
            )
            call.respond(response)
        }
    }
}