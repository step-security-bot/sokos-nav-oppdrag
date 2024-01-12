package no.nav.sokos.oppdragsinfo.api

import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import no.nav.sokos.oppdragsinfo.api.model.OppdragsInfoRequest
import no.nav.sokos.oppdragsinfo.service.OppdragsInfoService

private const val BASE_PATH = "/api/v1/oppdragsinfo"

fun Route.oppdragsInfoApi(
    oppdragsInfoService: OppdragsInfoService = OppdragsInfoService()
) {
    route(BASE_PATH) {

        post("oppdrag") {
            val oppdragsInfoRequest: OppdragsInfoRequest = call.receive()
            call.respond(
                oppdragsInfoService.hentOppdrag(
                    oppdragsInfoRequest.gjelderId,
                    oppdragsInfoRequest.faggruppeKode,
                    call
                )
            )
        }

        get("faggrupper") {
            call.respond(
                oppdragsInfoService.hentFaggrupper()
            )
        }

        post("oppdrag/omposteringer") {
            val oppdragsInfoRequest: OppdragsInfoRequest = call.receive()
            call.respond(
                oppdragsInfoService.hentOppdragsOmposteringer(
                    oppdragsInfoRequest.gjelderId
                )
            )
        }

        get("oppdrag/{oppdragsId}/oppdragslinjer") {
            call.respond(
                oppdragsInfoService.hentOppdragsLinjer(
                    call.parameters["oppdragsId"].orEmpty()
                )
            )
        }

        get("oppdrag/{oppdragsId}/enhetshistorikk") {
            call.respond(
                oppdragsInfoService.hentOppdragsEnhetsHistorikk(
                    call.parameters["oppdragsId"].orEmpty()
                )
            )
        }

        get("oppdrag/{oppdragsId}/statushistorikk") {
            call.respond(
                oppdragsInfoService.hentOppdragsStatusHistorikk(
                    call.parameters["oppdragsId"].orEmpty()
                )
            )
        }

        get("oppdrag/{oppdragsId}/{linjeId}/statuser") {
            call.respond(
                oppdragsInfoService.hentOppdragLinjeStatuser(
                    call.parameters["oppdragsId"].orEmpty(),
                    call.parameters["linjeId"].orEmpty()
                )
            )
        }

        get("oppdrag/{oppdragsId}/{linjeId}/attestanter") {
            call.respond(
                oppdragsInfoService.hentOppdragsLinjeAttestanter(
                    call.parameters["oppdragsId"].orEmpty(),
                    call.parameters["linjeId"].orEmpty()
                )
            )
        }

        get("oppdrag/{oppdragsId}/{linjeId}/detaljer") {
            call.respond(
                oppdragsInfoService.eksistererOppdragsLinjeDetaljer(
                    call.parameters["oppdragsId"].orEmpty(),
                    call.parameters["linjeId"].orEmpty()
                )
            )
        }

        get("oppdrag/{oppdragsId}/{linjeId}/valuta") {
            call.respond(
                oppdragsInfoService.hentOppdragsLinjeValuta(
                    call.parameters["oppdragsId"].orEmpty(),
                    call.parameters["linjeId"].orEmpty()
                )
            )
        }

        get("oppdrag/{oppdragsId}/{linjeId}/skyldner") {
            call.respond(
                oppdragsInfoService.hentOppdragsLinjeSkyldner(
                    call.parameters["oppdragsId"].orEmpty(),
                    call.parameters["linjeId"].orEmpty(),
                    call
                )
            )
        }

        get("oppdrag/{oppdragsId}/{linjeId}/kravhaver") {
            call.respond(
                oppdragsInfoService.hentOppdragsLinjeKravhaver(
                    call.parameters["oppdragsId"].orEmpty(),
                    call.parameters["linjeId"].orEmpty()
                )
            )
        }

        get("oppdrag/{oppdragsId}/{linjeId}/enheter") {
            call.respond(
                oppdragsInfoService.hentOppdragsLinjeEnheter(
                    call.parameters["oppdragsId"].orEmpty(),
                    call.parameters["linjeId"].orEmpty()
                )
            )
        }

        get("oppdrag/{oppdragsId}/{linjeId}/grad") {
            call.respond(
                oppdragsInfoService.hentOppdragsLinjeGrad(
                    call.parameters["oppdragsId"].orEmpty(),
                    call.parameters["linjeId"].orEmpty()
                )
            )
        }

        get("oppdrag/{oppdragsId}/{linjeId}/tekst") {
            call.respond(
                oppdragsInfoService.hentOppdragsLinjeTekst(
                    call.parameters["oppdragsId"].orEmpty(),
                    call.parameters["linjeId"].orEmpty()
                )
            )
        }

        get("oppdrag/{oppdragsId}/{linjeId}/kidliste") {
            call.respond(
                oppdragsInfoService.hentOppdragsLinjeKidListe(
                    call.parameters["oppdragsId"].orEmpty(),
                    call.parameters["linjeId"].orEmpty()
                )
            )
        }

        get("oppdrag/{oppdragsId}/{linjeId}/maksdato") {
            call.respond(
                oppdragsInfoService.hentOppdragsLinjeMaksdato(
                    call.parameters["oppdragsId"].orEmpty(),
                    call.parameters["linjeId"].orEmpty()
                )
            )
        }

        get("oppdrag/{oppdragsId}/{linjeId}/ovrig") {
            call.respond(
                oppdragsInfoService.hentOppdragsLinjeOvrig(
                    call.parameters["oppdragsId"].orEmpty(),
                    call.parameters["linjeId"].orEmpty()
                )
            )
        }
    }
}