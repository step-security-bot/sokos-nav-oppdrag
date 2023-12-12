package no.nav.sokos.oppdragsinfo.service

import io.ktor.server.application.ApplicationCall
import no.nav.sokos.oppdragsinfo.audit.AuditLogg
import no.nav.sokos.oppdragsinfo.audit.AuditLogger
import no.nav.sokos.oppdragsinfo.audit.Saksbehandler
import no.nav.sokos.oppdragsinfo.config.logger
import no.nav.sokos.oppdragsinfo.config.secureLogger
import no.nav.sokos.oppdragsinfo.database.Db2DataSource
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdrag
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.setAcceleration
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.useAndHandleErrors
import no.nav.sokos.oppdragsinfo.security.getSaksbehandler
import no.nav.sokos.oppdragsinfo.api.model.OppdragVO
import no.nav.sokos.oppdragsinfo.api.model.OppdragsSokRequest
import no.nav.sokos.oppdragsinfo.api.model.OppdragslinjeVO
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentFagomraade
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdragslinje
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdragstatus
import no.nav.sokos.oppdragsinfo.domain.Faggruppe
import no.nav.sokos.oppdragsinfo.domain.Fagomraade
import no.nav.sokos.oppdragsinfo.domain.Grad
import no.nav.sokos.oppdragsinfo.domain.Kid
import no.nav.sokos.oppdragsinfo.domain.Kravhaver
import no.nav.sokos.oppdragsinfo.domain.Linjeenhet
import no.nav.sokos.oppdragsinfo.domain.Maksdato
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import no.nav.sokos.oppdragsinfo.domain.OppdragsTekst
import no.nav.sokos.oppdragsinfo.domain.Skyldner
import no.nav.sokos.oppdragsinfo.domain.Valuta

class OppdragsInfoService(
    private val db2DataSource: Db2DataSource = Db2DataSource(),
    private val auditLogger: AuditLogger = AuditLogger(),
) {

    fun hentOppdragslinje(
        oppdragsId: String,
        oppdragslinje: String,
        applicationCall: ApplicationCall
    ): List<OppdragslinjeVO> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdrag med id: $oppdragsId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                oppdragsId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors { connection ->
            connection.setAcceleration()
            val oppdragslinjer = connection.hentOppdragslinje(oppdragsId.trim().toInt(), oppdragslinje.trim().toInt())
            oppdragslinjer.map { oppdLinje ->
                OppdragslinjeVO(
                    oppdLinje.oppdragsId,
                    oppdLinje.linjeId,
                    oppdLinje.delytelseId,
                    oppdLinje.sats,
                    oppdLinje.typeSats,
                    oppdLinje.vedtakFom?.orEmpty(),
                    oppdLinje.vedtakTom?.orEmpty(),
                    oppdLinje.attestert,
                    oppdLinje.vedtaksId,
                    oppdLinje.utbetalesTilId,
                    oppdLinje.refunderesOrgnr,
                    oppdLinje.brukerid,
                    oppdLinje.tidspktReg,
                    mutableListOf(Skyldner(oppdLinje.oppdragsId, oppdLinje.linjeId)),
                    mutableListOf(Valuta(oppdLinje.oppdragsId, oppdLinje.linjeId)),
                    mutableListOf(Linjeenhet(oppdLinje.oppdragsId, oppdLinje.linjeId)),
                    mutableListOf(Kid(oppdLinje.oppdragsId, oppdLinje.linjeId)),
                    mutableListOf(OppdragsTekst(oppdLinje.oppdragsId, oppdLinje.linjeId)),
                    mutableListOf(Grad(oppdLinje.oppdragsId, oppdLinje.linjeId)),
                    mutableListOf(Kravhaver(oppdLinje.oppdragsId, oppdLinje.linjeId)),
                    mutableListOf(Maksdato(oppdLinje.oppdragsId, oppdLinje.linjeId))
                )
            }
        }
    }

    fun hentOppdrag(
        oppdragsId: String,
        applicationCall: ApplicationCall
    ): List<Oppdrag> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdrag med id: $oppdragsId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                oppdragsId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors { connection ->
            connection.setAcceleration()
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
            connection.setAcceleration()
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