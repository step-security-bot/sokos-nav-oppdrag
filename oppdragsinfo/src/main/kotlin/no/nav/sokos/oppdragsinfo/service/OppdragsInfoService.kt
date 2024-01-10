package no.nav.sokos.oppdragsinfo.service

import io.ktor.server.application.ApplicationCall
import no.nav.sokos.oppdragsinfo.audit.AuditLogg
import no.nav.sokos.oppdragsinfo.audit.AuditLogger
import no.nav.sokos.oppdragsinfo.audit.Saksbehandler
import no.nav.sokos.oppdragsinfo.config.logger
import no.nav.sokos.oppdragsinfo.config.secureLogger
import no.nav.sokos.oppdragsinfo.database.Db2DataSource
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.getOppdrag
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.getOppdragsListe
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.useAndHandleErrors
import no.nav.sokos.oppdragsinfo.database.eksistererEnheter
import no.nav.sokos.oppdragsinfo.database.eksistererGrader
import no.nav.sokos.oppdragsinfo.database.eksistererKidliste
import no.nav.sokos.oppdragsinfo.database.eksistererKravhavere
import no.nav.sokos.oppdragsinfo.database.eksistererMaksdatoer
import no.nav.sokos.oppdragsinfo.database.eksistererSkyldnere
import no.nav.sokos.oppdragsinfo.database.eksistererTekster
import no.nav.sokos.oppdragsinfo.database.eksistererValutaer
import no.nav.sokos.oppdragsinfo.database.hentOppdragsLinjeAttestanter
import no.nav.sokos.oppdragsinfo.database.hentOppdragsLinjeStatuser
import no.nav.sokos.oppdragsinfo.database.hentOppdragsLinjer
import no.nav.sokos.oppdragsinfo.domain.Attestant
import no.nav.sokos.oppdragsinfo.domain.LinjeStatus
import no.nav.sokos.oppdragsinfo.domain.OppdragsInfo
import no.nav.sokos.oppdragsinfo.domain.OppdragsLinje
import no.nav.sokos.oppdragsinfo.domain.OppdragsLinjeDetaljer
import no.nav.sokos.oppdragsinfo.integration.EregService
import no.nav.sokos.oppdragsinfo.integration.TpService
import no.nav.sokos.oppdragsinfo.integration.pdl.PdlService
import no.nav.sokos.oppdragsinfo.security.getSaksbehandler

class OppdragsInfoService(
    private val db2DataSource: Db2DataSource = Db2DataSource(),
    private val auditLogger: AuditLogger = AuditLogger(),
    private val pdlService: PdlService = PdlService(),
    private val eregService: EregService = EregService(),
    private val tpService: TpService = TpService()
) {

    suspend fun sokOppdrag(
        gjelderId: String,
        applicationCall: ApplicationCall
    ): List<OppdragsInfo> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info(
            "Søker etter oppdrag med gjelderId: $gjelderId"
        )
        secureLogger.info("Søker etter oppdrag med gjelderId: $gjelderId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = gjelderId
            )
        )

        val oppdragsInfo = db2DataSource.connection.useAndHandleErrors {
            it.getOppdrag(gjelderId).firstOrNull()
        } ?: return emptyList()

        val oppdrag =
            db2DataSource.connection.useAndHandleErrors { it.getOppdragsListe(oppdragsInfo.gjelderId) }

        val gjelderNavn = getGjelderIdNavn(oppdragsInfo.gjelderId)

        return listOf(
            OppdragsInfo(
                gjelderId = oppdragsInfo.gjelderId,
                gjelderNavn = gjelderNavn,
                oppdragsListe = oppdrag
            )
        )
    }

    fun hentOppdragsLinjer(
        oppdragsId: String,
        applicationCall: ApplicationCall
    ): List<OppdragsLinje> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragslinjer med oppdragsId: $oppdragsId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentOppdragsLinjer(oppdragsId.toInt()).toList()
        }
    }

    fun hentOppdragLinjeStatuser(
        oppdragsId: String,
        linjeId: String,
        applicationCall: ApplicationCall
    ): List<LinjeStatus> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragslinjstatuser for oppdrag : $oppdragsId, linje : $linjeId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentOppdragsLinjeStatuser(oppdragsId.toInt(), linjeId.toInt()).toList()
        }
    }

    fun hentOppdragsLinjeAttestanter(
        oppdragsId: String,
        linjeId: String,
        applicationCall: ApplicationCall
    ): List<Attestant> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragslinjstatuser for oppdrag : $oppdragsId, linje : $linjeId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentOppdragsLinjeAttestanter(oppdragsId.toInt(), linjeId.toInt()).toList()
        }
    }

    fun hentOppdragsLinjeDetaljer(
        oppdragsId: String,
        linjeId: String,
        applicationCall: ApplicationCall
    ): OppdragsLinjeDetaljer {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragslinjedetaljer for oppdrag : $oppdragsId, linje : $linjeId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            OppdragsLinjeDetaljer(
                harValutaer = it.eksistererValutaer(oppdragsId.toInt(), linjeId.toInt()),
                harSkyldnere = it.eksistererSkyldnere(oppdragsId.toInt(), linjeId.toInt()),
                harKravhavere = it.eksistererKravhavere(oppdragsId.toInt(), linjeId.toInt()),
                harEnheter = it.eksistererEnheter(oppdragsId.toInt(), linjeId.toInt()),
                harGrader = it.eksistererGrader(oppdragsId.toInt(), linjeId.toInt()),
                harTekster = it.eksistererTekster(oppdragsId.toInt(), linjeId.toInt()),
                harKidliste = it.eksistererKidliste(oppdragsId.toInt(), linjeId.toInt()),
                harMaksdatoer = it.eksistererMaksdatoer(oppdragsId.toInt(), linjeId.toInt())
            )
        }
    }

    private suspend fun getGjelderIdNavn(gjelderId: String): String =
        when {
            gjelderId.length == 11 && gjelderId.toLong() > 80000000000 -> tpService.getLeverandorNavn(gjelderId).navn
            gjelderId.length == 11 && gjelderId.toLong() < 80000000000 -> pdlService.getPersonNavn(gjelderId)?.navn?.firstOrNull()
                ?.run { "$fornavn $mellomnavn $etternavn" } ?: ""

            else -> eregService.getOrganisasjonsNavn(gjelderId).navn.sammensattnavn
        }

    private fun hentSaksbehandler(call: ApplicationCall): Saksbehandler {
        return getSaksbehandler(call)
    }
}