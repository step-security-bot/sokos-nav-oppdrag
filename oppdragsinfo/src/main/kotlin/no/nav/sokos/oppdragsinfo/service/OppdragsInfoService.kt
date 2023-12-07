package no.nav.sokos.oppdragsinfo.service

import io.ktor.server.application.ApplicationCall
import mu.KotlinLogging
import no.nav.sokos.oppdragsinfo.audit.AuditLogg
import no.nav.sokos.oppdragsinfo.audit.AuditLogger
import no.nav.sokos.oppdragsinfo.audit.Saksbehandler
import no.nav.sokos.oppdragsinfo.config.SECURE_LOGGER
import no.nav.sokos.oppdragsinfo.database.Db2DataSource
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdrag
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.useAndHandleErrors
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import no.nav.sokos.oppdragsinfo.security.getSaksbehandler
import no.nav.sokos.oppdragsinfo.api.model.OppdragVO
import no.nav.sokos.oppdragsinfo.api.model.OppdragsSokRequest
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentFagomraade
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdragstatus
import no.nav.sokos.oppdragsinfo.domain.Faggruppe
import no.nav.sokos.oppdragsinfo.domain.Fagomraade

private val logger = KotlinLogging.logger {}
private val secureLogger = KotlinLogging.logger(SECURE_LOGGER)

class OppdragsInfoService(
    private val db2DataSource: Db2DataSource = Db2DataSource(),
    private val auditLogger: AuditLogger = AuditLogger(),
) {

    fun hentOppdrag(
        oppdragsId: String,
        applicationCall: ApplicationCall
    ): List<Oppdrag> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info("Henter oppdrag med java.sql")
        secureLogger.info("Henter oppdrag med id: $oppdragsId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                oppdragsId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors { connection ->
            connection.hentOppdrag(oppdragsId.trim().toInt())
        }
    }

    fun sokOppdrag(
        oppdragsSokRequest: OppdragsSokRequest,
        applicationCall: ApplicationCall
    ): List<OppdragVO> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info(
            "Søker etter oppdrag med gjelderId = {}, fagSystemId = {}, fagGruppeKode = {}, vedtakFom = {}",
            oppdragsSokRequest.gjelderId,
            oppdragsSokRequest.fagSystemId,
            oppdragsSokRequest.fagGruppeKode,
            oppdragsSokRequest.vedtakFom
        )
        secureLogger.info("Søker etter oppdrag med gjelderId = {}", oppdragsSokRequest.gjelderId)
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                oppdragsId = oppdragsSokRequest.gjelderId
            )
        )
        return db2DataSource.connection.useAndHandleErrors { connection ->
            val oppdrag = connection.hentOppdrag(
                oppdragsSokRequest.gjelderId,
                oppdragsSokRequest.fagSystemId,
                oppdragsSokRequest.fagGruppeKode,
                oppdragsSokRequest.vedtakFom
            )
            val fagomraader = oppdrag
                .map { connection.hentFagomraade(it.kodeFagOmrade) }
                .flatten()

            oppdrag.map { oppd ->
                val fagomraade = fagomraader.first { it.kode == oppd.kodeFagOmrade }
                val oppdragStatuser = connection.hentOppdragstatus(oppd.oppdragsId)
                fagomraade.let {
                    OppdragVO(
                        oppd.oppdragsId,
                        "test testesen",
                        oppd.fagsystemId,
                        Fagomraade(
                            fagomraade.kode,
                            fagomraade.navn,
                            Faggruppe(fagomraade.faggruppe.kode, fagomraade.faggruppe.navn)
                        ),
                        oppd.frekvens,
                        oppd.kjorIdag,
                        oppd.stonadId,
                        oppd.datoForfall?.orEmpty(),
                        oppd.oppdragGjelderId,
                        oppd.typeBilag,
                        oppd.brukerId,
                        oppd.tidspunktReg,
                        oppdragStatuser
                    )
                }
            }
        }
    }

    private fun hentSaksbehandler(call: ApplicationCall): Saksbehandler {
        return getSaksbehandler(call)
    }
}