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
import no.nav.sokos.oppdragsinfo.database.hentOppdragsLinjer
import no.nav.sokos.oppdragsinfo.domain.OppdragsInfo
import no.nav.sokos.oppdragsinfo.domain.OppdragsLinje
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

    private suspend fun getGjelderIdNavn(gjelderId: String): String =
        when {
            gjelderId.toLong() > 80000000000 -> tpService.getLeverandorNavn(gjelderId).navn
            gjelderId.toLong() < 80000000000 -> pdlService.getPersonNavn(gjelderId)?.navn?.firstOrNull()
                ?.run { "$fornavn $mellomnavn $etternavn" } ?: ""

            else -> eregService.getOrganisasjonsNavn(gjelderId).navn.sammensattnavn
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

    private fun hentSaksbehandler(call: ApplicationCall): Saksbehandler {
        return getSaksbehandler(call)
    }


}

/*    suspend fun hentOppdragslinje(
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
            val oppdragsLinjeInfo =
                finnOppdragslinjeInfo(connection, oppdragsId.trim().toInt(), oppdragslinje.trim().toInt())

            oppdragsLinjeInfo.oppdragslinjer.map {
                OppdragslinjeVO(
                    it.oppdragsId,
                    it.linjeId,
                    it.delytelseId,
                    it.sats,
                    it.typeSats,
                    it.vedtakFom.orEmpty(),
                    it.vedtakTom.orEmpty(),
                    it.attestert,
                    it.vedtaksId,
                    it.utbetalesTilId,
                    it.refunderesOrgnr.orEmpty(),
                    it.brukerid,
                    it.tidspktReg,
                    oppdragsLinjeInfo.skyldnere,
                    oppdragsLinjeInfo.valutaer,
                    oppdragsLinjeInfo.linjeenheter,
                    oppdragsLinjeInfo.kidliste,
                    oppdragsLinjeInfo.tekster,
                    oppdragsLinjeInfo.grader,
                    oppdragsLinjeInfo.kravhavere,
                    oppdragsLinjeInfo.maksdatoer
                )
            }
        }
    } */

/*    suspend fun finnOppdragslinjeInfo(connection: Connection, oppdragsId: Int, linjeId: Int): OppdragsLinjeInfo {
        val oppdLinje: OppdragsLinjeInfo
        coroutineScope {
            oppdLinje = OppdragsLinjeInfo(
                async { connection.hentOppdragslinje(oppdragsId, linjeId) }.await(),
                async { connection.hentSkyldnere(oppdragsId, linjeId) }.await(),
                async { connection.hentValutaer(oppdragsId, linjeId) }.await(),
                async { connection.hentLinjeenheter(oppdragsId, linjeId) }.await(),
                async { connection.hentKidlister(oppdragsId, linjeId) }.await(),
                async { connection.henOppdragsTekster(oppdragsId, linjeId) }.await(),
                async { connection.hentGrader(oppdragsId, linjeId) }.await(),
                async { connection.henKravhavere(oppdragsId, linjeId) }.await(),
                async { connection.henMaksdatoer(oppdragsId, linjeId) }.await()
            )
        }
        return oppdLinje
    }*/