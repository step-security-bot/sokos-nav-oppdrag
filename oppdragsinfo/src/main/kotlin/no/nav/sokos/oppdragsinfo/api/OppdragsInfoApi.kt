package no.nav.sokos.oppdragsinfo.api

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import no.nav.sokos.oppdragsinfo.api.model.OppdragsSokRequest
import no.nav.sokos.oppdragsinfo.api.model.OppdragsSokResponse
import no.nav.sokos.oppdragsinfo.api.model.OppdragslinjeResponse
import no.nav.sokos.oppdragsinfo.api.model.OrganisasjonsNummerRequest
import no.nav.sokos.oppdragsinfo.api.model.TssIdRequest
import no.nav.sokos.oppdragsinfo.integration.EregService
import no.nav.sokos.oppdragsinfo.integration.TpService
import no.nav.sokos.oppdragsinfo.service.OppdragsInfoService

private const val BASE_PATH = "/api/v1/oppdragsinfo"

fun Route.oppdragsInfoApi(
    oppdragsInfoService: OppdragsInfoService = OppdragsInfoService(),
    tpService: TpService = TpService(),
    eregService: EregService = EregService()
) {
    route(BASE_PATH) {
        get("oppdrag/{oppdragsId}") {
            val response = oppdragsInfoService.hentOppdrag(
                call.parameters.get("oppdragsId").orEmpty(),
                call
            )
            call.respond(response)
        }

//        get("oppdrag/{oppdragsId}/detaljer") {
//            val response = OppdragsInfoDetaljerResponse(
//                oppdragsInfoService.hentOppdragsdetaljer (
//                    call.parameters.get("oppdragsId").orEmpty(),
//                    call
//                )
//            )
//            call.respond(response)
//        }

        get("oppdrag/{oppdragsId}/{oppdragslinje}") {
            val response = OppdragslinjeResponse(
                oppdragsInfoService.hentOppdragslinje(
                    call.parameters.get("oppdragsId").orEmpty(),
                    call.parameters.get("oppdragslinje").orEmpty(),
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

        post("/henttss") {
            val tssIdRequest: TssIdRequest = call.receive()
            val response = tpService.hentLeverandorNavn(
                tssIdRequest.tssId
            )
            call.respond(response)
        }

        post("/hentorg") {
            val organisasjonsNummerRequest: OrganisasjonsNummerRequest = call.receive()
            val response = eregService.hentOrganisasjonsNavn(
                organisasjonsNummerRequest.organisasjonsNummer
            )
            call.respond(response)
        }
    }
}