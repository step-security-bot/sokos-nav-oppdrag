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
import no.nav.sokos.oppdragsinfo.database.hentEnheter
import no.nav.sokos.oppdragsinfo.database.hentGrader
import no.nav.sokos.oppdragsinfo.database.hentKidliste
import no.nav.sokos.oppdragsinfo.database.hentKravhavere
import no.nav.sokos.oppdragsinfo.database.hentMaksdatoer
import no.nav.sokos.oppdragsinfo.database.hentOppdragsEnhetsHistorikk
import no.nav.sokos.oppdragsinfo.database.hentOppdragsLinjeAttestanter
import no.nav.sokos.oppdragsinfo.database.hentOppdragsLinjeStatuser
import no.nav.sokos.oppdragsinfo.database.hentOppdragsLinjer
import no.nav.sokos.oppdragsinfo.database.hentOppdragsOmposteringer
import no.nav.sokos.oppdragsinfo.database.hentOppdragsStatusHistorikk
import no.nav.sokos.oppdragsinfo.database.hentSkyldnere
import no.nav.sokos.oppdragsinfo.database.hentTekster
import no.nav.sokos.oppdragsinfo.database.hentValutaer
import no.nav.sokos.oppdragsinfo.domain.Attestant
import no.nav.sokos.oppdragsinfo.domain.Grad
import no.nav.sokos.oppdragsinfo.domain.Kid
import no.nav.sokos.oppdragsinfo.domain.Kravhaver
import no.nav.sokos.oppdragsinfo.domain.LinjeEnhet
import no.nav.sokos.oppdragsinfo.domain.LinjeStatus
import no.nav.sokos.oppdragsinfo.domain.Maksdato
import no.nav.sokos.oppdragsinfo.domain.Ompostering
import no.nav.sokos.oppdragsinfo.domain.OppdragStatus
import no.nav.sokos.oppdragsinfo.domain.OppdragsEnhet
import no.nav.sokos.oppdragsinfo.domain.OppdragsInfo
import no.nav.sokos.oppdragsinfo.domain.OppdragsLinje
import no.nav.sokos.oppdragsinfo.domain.OppdragsLinjeDetaljer
import no.nav.sokos.oppdragsinfo.domain.Skyldner
import no.nav.sokos.oppdragsinfo.domain.Tekst
import no.nav.sokos.oppdragsinfo.domain.Valuta
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

    fun hentOppdragsOmposteringer(
        gjelderId: String,
        applicationCall: ApplicationCall
    ): List<Ompostering> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter omposteringer for gjelderId: $gjelderId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = gjelderId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentOppdragsOmposteringer(gjelderId).toList()
        }
    }

    fun hentOppdragsEnhetsHistorikk(
        oppdragsId: String,
        applicationCall: ApplicationCall
    ): List<OppdragsEnhet> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragsEnhetsHistorikk for oppdrag: $oppdragsId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentOppdragsEnhetsHistorikk(oppdragsId.toInt()).toList()
        }
    }

    fun hentOppdragsStatusHistorikk(
        oppdragsId: String,
        applicationCall: ApplicationCall
    ): List<OppdragStatus> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragsStatusHistorikk for oppdrag: $oppdragsId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentOppdragsStatusHistorikk(oppdragsId.toInt()).toList()
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
    ): List<OppdragsLinjeDetaljer> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragslinjedetaljer for oppdrag : $oppdragsId, linje : $linjeId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            listOf(
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
            )
        }
    }

    fun hentOppdragsLinjeValuta(
        oppdragsId: String,
        linjeId: String,
        applicationCall: ApplicationCall
    ): List<Valuta> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragslinjevaluta for oppdrag : $oppdragsId, linje : $linjeId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentValutaer(oppdragsId.toInt(), linjeId.toInt()).toList()
        }
    }

    fun hentOppdragsLinjeSkyldner(
        oppdragsId: String,
        linjeId: String,
        applicationCall: ApplicationCall
    ): List<Skyldner> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragslinjeSkyldner for oppdrag : $oppdragsId, linje : $linjeId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentSkyldnere(oppdragsId.toInt(), linjeId.toInt()).toList()
        }
    }

    fun hentOppdragsLinjeKravhaver(
        oppdragsId: String,
        linjeId: String,
        applicationCall: ApplicationCall
    ): List<Kravhaver> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragslinjeKravhaver for oppdrag : $oppdragsId, linje : $linjeId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentKravhavere(oppdragsId.toInt(), linjeId.toInt()).toList()
        }
    }

    fun hentOppdragsLinjeEnheter(
        oppdragsId: String,
        linjeId: String,
        applicationCall: ApplicationCall
    ): List<LinjeEnhet> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragslinjeEnheter for oppdrag : $oppdragsId, linje : $linjeId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentEnheter(oppdragsId.toInt(), linjeId.toInt()).toList()
        }
    }

    fun hentOppdragsLinjeGrad(
        oppdragsId: String,
        linjeId: String,
        applicationCall: ApplicationCall
    ): List<Grad> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragslinjeGrad for oppdrag : $oppdragsId, linje : $linjeId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentGrader(oppdragsId.toInt(), linjeId.toInt()).toList()
        }
    }

    fun hentOppdragsLinjeTekst(
        oppdragsId: String,
        linjeId: String,
        applicationCall: ApplicationCall
    ): List<Tekst> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragslinjeTekst for oppdrag : $oppdragsId, linje : $linjeId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentTekster(oppdragsId.toInt(), linjeId.toInt()).toList()
        }
    }

    fun hentOppdragsLinjeKidListe(
        oppdragsId: String,
        linjeId: String,
        applicationCall: ApplicationCall
    ): List<Kid> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragslinjeKidliste for oppdrag : $oppdragsId, linje : $linjeId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentKidliste(oppdragsId.toInt(), linjeId.toInt()).toList()
        }
    }

    fun hentOppdragsLinjeMaksdato(
        oppdragsId: String,
        linjeId: String,
        applicationCall: ApplicationCall
    ): List<Maksdato> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        secureLogger.info("Henter oppdragslinjeMaksdato for oppdrag : $oppdragsId, linje : $linjeId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                gjelderId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors {
            it.hentMaksdatoer(oppdragsId.toInt(), linjeId.toInt()).toList()
        }
    }

    private suspend fun getGjelderIdNavn(gjelderId: String): String =
        when {
            gjelderId.toLong() > 80000000000 -> tpService.getLeverandorNavn(gjelderId).navn
            gjelderId.toLong() < 80000000000 -> pdlService.getPersonNavn(gjelderId)?.navn?.firstOrNull()
                ?.run { mellomnavn?.let { "$fornavn $mellomnavn $etternavn" } ?: "$fornavn $etternavn" } ?: ""

            else -> eregService.getOrganisasjonsNavn(gjelderId).navn.sammensattnavn
        }

    private fun hentSaksbehandler(call: ApplicationCall): Saksbehandler {
        return getSaksbehandler(call)
    }
}
