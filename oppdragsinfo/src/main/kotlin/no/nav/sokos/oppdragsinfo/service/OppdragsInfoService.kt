package no.nav.sokos.oppdragsinfo.service

import io.ktor.server.application.ApplicationCall
import no.nav.sokos.oppdragsinfo.audit.AuditLogg
import no.nav.sokos.oppdragsinfo.audit.AuditLogger
import no.nav.sokos.oppdragsinfo.audit.Saksbehandler
import no.nav.sokos.oppdragsinfo.config.logger
import no.nav.sokos.oppdragsinfo.config.secureLogger
import no.nav.sokos.oppdragsinfo.database.Db2DataSource

import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.finnOppdrag

import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.setAcceleration
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.useAndHandleErrors
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import no.nav.sokos.oppdragsinfo.security.getSaksbehandler

class OppdragsInfoService(
    private val db2DataSource: Db2DataSource = Db2DataSource(),
    private val auditLogger: AuditLogger = AuditLogger()
) {

    fun sokOppdrag(
        gjelderId: String,
        applicationCall: ApplicationCall
    ): List<Oppdrag> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info(
            "Søker etter oppdrag med gjelderId: $gjelderId"
        )
        secureLogger.info("Søker etter oppdrag med gjelderId: $gjelderId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                oppdragsId = gjelderId
            )
        )
        return db2DataSource.connection.useAndHandleErrors { connection ->
            connection.setAcceleration()
            connection.finnOppdrag(gjelderId)
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
    }*/

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

    /*    suspend fun hentOppdragKompakt(
            oppdragsId: String,
            applicationCall: ApplicationCall
        ): OppdragsInfoKompaktVO {
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
                val oppdrag = connection.hentOppdrag(oppdragsId.trim().toInt())
                logger.info("###oppdrag={}", oppdrag)
                val fagomraade = connection.hentFagomraade(oppdrag[0].kodeFagOmrade)
                val oppdragslinjer = connection.hentOppdragslinjer(oppdragsId.trim().toInt())
                logger.info("###antall oppdragslinjer={}", oppdragslinjer.size)

                val oppdragsInfoKompaktLinjeInfo = coroutineScope {
                    oppdragslinjer.map {
                        async {
                            finnLinjeInfoForOppdragsInfoKompakt(
                                connection,
                                oppdragsId.trim().toInt(),
                                it.linjeId
                            )
                        }
                    }.awaitAll().toList()
                }
                logger.info("###hentet all info for {} oppdragslinjer", oppdragsInfoKompaktLinjeInfo.size)
                var index = 0
                val oppdragslinjerInfo = oppdragslinjer.map {
                    OppdragsInfoLinjeKompaktVO(
                        it.linjeId,
                        it.sats,
                        it.typeSats,
                        it.vedtakFom.orEmpty(),
                        it.vedtakTom.orEmpty(),
                        if (it.sats < 0) "K" else "D",
                        connection.hentKlasse(it.kodeKlasse).first(),
                        oppdragsInfoKompaktLinjeInfo.get(index).korreksjoner,
                        oppdragsInfoKompaktLinjeInfo.get(index).attestasjoner,
                        oppdragsInfoKompaktLinjeInfo.get(index++).linjestatuser
                    )
                }.toList()
                logger.info("###bygget respons for oppdragslinjene")
                OppdragsInfoKompaktVO(
                    oppdragsId.toInt(),
                    "TestTestesen",
                    oppdrag[0].fagsystemId,
                    fagomraade.first(),
                    oppdrag[0].kjorIdag,
                    oppdrag[0].oppdragGjelderId,
                    connection.eksistererOppdragsenhet(oppdragsId.toInt()),
                    connection.eksistererOppdragstatus(oppdragsId.toInt()),
                    oppdragslinjerInfo
                )
            }
        }*/

    /*    suspend fun hentOppdrag(
            oppdragsId: String,
            applicationCall: ApplicationCall
        ): OppdragsInfoVO {
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
                val oppdrag = connection.hentOppdrag(oppdragsId.trim().toInt())
                val fagomraade = connection.hentFagomraade(oppdrag[0].kodeFagOmrade)
                val oppdragslinjer = connection.hentOppdragslinjer(oppdragsId.trim().toInt())
                val oppdragsInfoLinjeInfo =
                    oppdragslinjer.map { finnLinjeInfoForOppdragsInfo(connection, oppdragsId.trim().toInt(), it.linjeId) }
                        .toList()
                var index = 0
                val oppdragslinjerInfo = oppdragslinjer.map {
                    OppdragsInfoLinjeVO(
                        it.oppdragsId,
                        it.linjeId,
                        it.sats,
                        it.typeSats,
                        it.vedtakFom.orEmpty(),
                        it.vedtakTom.orEmpty(),
                        if (it.sats < 0) "K" else "D",
                        connection.hentKlasse(it.kodeKlasse).first(),
                        oppdragsInfoLinjeInfo.get(index).korreksjoner,
                        oppdragsInfoLinjeInfo.get(index).attestasjoner,
                        oppdragsInfoLinjeInfo.get(index++).linjestatuser
                    )
                }.toList()
                OppdragsInfoVO(
                    oppdragsId.toInt(),
                    "TestTestesen",
                    oppdrag[0].fagsystemId,
                    fagomraade.first(),
                    oppdrag[0].kjorIdag,
                    oppdrag[0].oppdragGjelderId,
                    connection.hentOppdragsenhet(oppdragsId.toInt()),
                    connection.hentOppdragstatus(oppdragsId.toInt()),
                    oppdragslinjerInfo
                )
            }
        }*/

/*    fun finnLinjeInfoForOppdragsInfoKompakt(
        connection: Connection,
        oppdragsId: Int,
        oppdragsLinje: Int
    ): OppdragsInfoKompaktLinjeInfo {
        val OppdragsInfoKompaktLinjeInfo = OppdragsInfoKompaktLinjeInfo(
            connection.eksistererKorreksjoner(oppdragsId, oppdragsLinje),
            connection.eksistererAttestasjoner(oppdragsId, oppdragsLinje),
            connection.eksistererLinjestatuser(oppdragsId, oppdragsLinje)
        )
        logger.info("###prosessert oppdragslinje {}", oppdragsLinje)
        return OppdragsInfoKompaktLinjeInfo
    }*/


/*    suspend fun finnLinjeInfoForOppdragsInfo(
        connection: Connection,
        oppdragsId: Int,
        oppdragsLinje: Int
    ): OppdragsInfoLinjeInfo {
        val linjeInfo: OppdragsInfoLinjeInfo
        coroutineScope {
            linjeInfo = OppdragsInfoLinjeInfo(
                async { connection.hentKorreksjoner(oppdragsId, oppdragsLinje) }.await(),
                async { connection.hentAttestasjoner(oppdragsId, oppdragsLinje) }.await(),
                async { connection.hentLinjestatuser(oppdragsId, oppdragsLinje) }.await()
            )
        }
        return linjeInfo
    }*/

    private fun hentSaksbehandler(call: ApplicationCall): Saksbehandler {
        return getSaksbehandler(call)
    }
}