package no.nav.sokos.oppdragsinfo.service

import io.ktor.server.application.ApplicationCall
import no.nav.sokos.oppdragsinfo.audit.AuditLogg
import no.nav.sokos.oppdragsinfo.audit.AuditLogger
import no.nav.sokos.oppdragsinfo.audit.Saksbehandler
import no.nav.sokos.oppdragsinfo.config.logger
import no.nav.sokos.oppdragsinfo.config.secureLogger
import no.nav.sokos.oppdragsinfo.database.Db2DataSource
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.eksistererEnheter
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.eksistererGrader
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.eksistererKidliste
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.eksistererKravhavere
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.eksistererMaksdatoer
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.eksistererOmposteringer
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.eksistererSkyldnere
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.eksistererTekster
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.eksistererValutaer
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdrag
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdragsListe
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentEnheter
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentFaggrupper
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentGrader
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentKidliste
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentKorreksjoner
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentKravhavere
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentMaksdatoer
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdragsEnhetsHistorikk
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdragsLinjeAttestanter
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdragsLinjeStatuser
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdragsLinjer
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdragsOmposteringer
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdragsStatusHistorikk
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOvrige
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentSkyldnere
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentTekster
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentValutaer
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.useAndHandleErrors
import no.nav.sokos.oppdragsinfo.domain.Attestant
import no.nav.sokos.oppdragsinfo.domain.Faggruppe
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
import no.nav.sokos.oppdragsinfo.domain.Ovrig
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

    suspend fun hentOppdrag(
        gjelderId: String,
        faggruppeKode: String?,
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
            it.hentOppdrag(gjelderId).firstOrNull()
        } ?: return emptyList()

        val oppdrag =
            db2DataSource.connection.useAndHandleErrors { it.hentOppdragsListe(oppdragsInfo.gjelderId, faggruppeKode) }

        val harOmposteringer =
            db2DataSource.connection.useAndHandleErrors { it.eksistererOmposteringer(oppdragsInfo.gjelderId) }

        val gjelderNavn = getGjelderIdNavn(oppdragsInfo.gjelderId)

        return listOf(
            OppdragsInfo(
                gjelderId = oppdragsInfo.gjelderId,
                gjelderNavn = gjelderNavn,
                harOmposteringer = harOmposteringer,
                oppdragsListe = oppdrag
            )
        )
    }

    fun hentFaggrupper(): List<Faggruppe> {
        return db2DataSource.connection.useAndHandleErrors {
            it.hentFaggrupper().toList()
        }
    }

    fun hentOppdragsOmposteringer(
        gjelderId: String
    ): List<Ompostering> {
        secureLogger.info("Henter omposteringer for gjelderId: $gjelderId")
        return db2DataSource.connection.useAndHandleErrors {
            it.hentOppdragsOmposteringer(gjelderId).toList()
        }
    }

    fun hentOppdragsLinjer(
        oppdragsId: String
    ): List<OppdragsLinje> {
        secureLogger.info("Henter oppdragslinjer med oppdragsId: $oppdragsId")
        return db2DataSource.connection.useAndHandleErrors {
            it.hentOppdragsLinjer(oppdragsId.toInt()).toList()
        }
    }

    fun hentOppdragsEnhetsHistorikk(
        oppdragsId: String
    ): List<OppdragsEnhet> {
        secureLogger.info("Henter oppdragsEnhetsHistorikk for oppdrag: $oppdragsId")
        return db2DataSource.connection.useAndHandleErrors {
            it.hentOppdragsEnhetsHistorikk(oppdragsId.toInt()).toList()
        }
    }

    fun hentOppdragsStatusHistorikk(
        oppdragsId: String
    ): List<OppdragStatus> {
        secureLogger.info("Henter oppdragsStatusHistorikk for oppdrag: $oppdragsId")
        return db2DataSource.connection.useAndHandleErrors {
            it.hentOppdragsStatusHistorikk(oppdragsId.toInt()).toList()
        }
    }

    fun hentOppdragLinjeStatuser(
        oppdragsId: String,
        linjeId: String
    ): List<LinjeStatus> {
        secureLogger.info("Henter oppdragslinjstatuser for oppdrag : $oppdragsId, linje : $linjeId")
        return db2DataSource.connection.useAndHandleErrors {
            it.hentOppdragsLinjeStatuser(oppdragsId.toInt(), linjeId.toInt()).toList()
        }
    }

    fun hentOppdragsLinjeAttestanter(
        oppdragsId: String,
        linjeId: String
    ): List<Attestant> {
        secureLogger.info("Henter oppdragslinjstatuser for oppdrag : $oppdragsId, linje : $linjeId")
        return db2DataSource.connection.useAndHandleErrors {
            it.hentOppdragsLinjeAttestanter(oppdragsId.toInt(), linjeId.toInt()).toList()
        }
    }

    fun eksistererOppdragsLinjeDetaljer(
        oppdragsId: String,
        linjeId: String
    ): List<OppdragsLinjeDetaljer> {
        secureLogger.info("Henter oppdragslinjedetaljer for oppdrag : $oppdragsId, linje : $linjeId")
        val korrigerteLinjeIder: MutableList<Int> = finnKorrigerteLinjer(oppdragsId, linjeId)
        return db2DataSource.connection.useAndHandleErrors {
            listOf(
                OppdragsLinjeDetaljer(
                    korrigerteLinjeIder = korrigerteLinjeIder,
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
        linjeId: String
    ): List<Valuta> {
        secureLogger.info("Henter oppdragslinjevaluta for oppdrag : $oppdragsId, linje : $linjeId")
        val korrigerteLinjeIder: MutableList<Int> = finnKorrigerteLinjer(oppdragsId, linjeId)
        return db2DataSource.connection.useAndHandleErrors {
            it.hentValutaer(oppdragsId.toInt(), korrigerteLinjeIder.joinToString(",")).toList()
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
        val korrigerteLinjeIder: MutableList<Int> = finnKorrigerteLinjer(oppdragsId, linjeId)
        return db2DataSource.connection.useAndHandleErrors {
            it.hentSkyldnere(oppdragsId.toInt(), korrigerteLinjeIder.joinToString(",")).toList()
        }
    }

    fun hentOppdragsLinjeKravhaver(
        oppdragsId: String,
        linjeId: String
    ): List<Kravhaver> {
        secureLogger.info("Henter oppdragslinjeKravhaver for oppdrag : $oppdragsId, linje : $linjeId")
        val korrigerteLinjeIder: MutableList<Int> = finnKorrigerteLinjer(oppdragsId, linjeId)
        return db2DataSource.connection.useAndHandleErrors {
            it.hentKravhavere(oppdragsId.toInt(), korrigerteLinjeIder.joinToString(",")).toList()
        }
    }

    fun hentOppdragsLinjeEnheter(
        oppdragsId: String,
        linjeId: String
    ): List<LinjeEnhet> {
        secureLogger.info("Henter oppdragslinjeEnheter for oppdrag : $oppdragsId, linje : $linjeId")
        val korrigerteLinjeIder: MutableList<Int> = finnKorrigerteLinjer(oppdragsId, linjeId)
        return db2DataSource.connection.useAndHandleErrors {
            it.hentEnheter(oppdragsId.toInt(), korrigerteLinjeIder.joinToString(",")).toList()
        }
    }

    fun hentOppdragsLinjeGrad(
        oppdragsId: String,
        linjeId: String
    ): List<Grad> {
        secureLogger.info("Henter oppdragslinjeGrad for oppdrag : $oppdragsId, linje : $linjeId")
        val korrigerteLinjeIder: MutableList<Int> = finnKorrigerteLinjer(oppdragsId, linjeId)
        return db2DataSource.connection.useAndHandleErrors {
            it.hentGrader(oppdragsId.toInt(), korrigerteLinjeIder.joinToString(",")).toList()
        }
    }

    fun hentOppdragsLinjeTekst(
        oppdragsId: String,
        linjeId: String
    ): List<Tekst> {
        secureLogger.info("Henter oppdragslinjeTekst for oppdrag : $oppdragsId, linje : $linjeId")
        val korrigerteLinjeIder: MutableList<Int> = finnKorrigerteLinjer(oppdragsId, linjeId)
        return db2DataSource.connection.useAndHandleErrors {
            it.hentTekster(oppdragsId.toInt(), korrigerteLinjeIder.joinToString(",")).toList()
        }
    }

    fun hentOppdragsLinjeKidListe(
        oppdragsId: String,
        linjeId: String
    ): List<Kid> {
        secureLogger.info("Henter oppdragslinjeKidliste for oppdrag : $oppdragsId, linje : $linjeId")
        val korrigerteLinjeIder: MutableList<Int> = finnKorrigerteLinjer(oppdragsId, linjeId)
        return db2DataSource.connection.useAndHandleErrors {
            it.hentKidliste(oppdragsId.toInt(), korrigerteLinjeIder.joinToString(",")).toList()
        }
    }

    fun hentOppdragsLinjeMaksdato(
        oppdragsId: String,
        linjeId: String
    ): List<Maksdato> {
        secureLogger.info("Henter oppdragslinjeMaksdato for oppdrag : $oppdragsId, linje : $linjeId")
        val korrigerteLinjeIder: MutableList<Int> = finnKorrigerteLinjer(oppdragsId, linjeId)
        return db2DataSource.connection.useAndHandleErrors {
            it.hentMaksdatoer(oppdragsId.toInt(), korrigerteLinjeIder.joinToString(",")).toList()
        }
    }

    fun hentOppdragsLinjeOvrig(
        oppdragsId: String,
        linjeId: String
    ): List<Ovrig> {
        secureLogger.info("Henter oppdragslinjeOvrig for oppdrag : $oppdragsId, linje : $linjeId")
        val korrigerteLinjeIder: MutableList<Int> = finnKorrigerteLinjer(oppdragsId, linjeId)
        return db2DataSource.connection.useAndHandleErrors {
            it.hentOvrige(oppdragsId.toInt(), korrigerteLinjeIder.joinToString(",")).toList()
        }
    }

    private fun finnKorrigerteLinjer(oppdragsId: String, linjeId: String): MutableList<Int> {
        val korrigerteLinjer = db2DataSource.connection.useAndHandleErrors { it.hentKorreksjoner(oppdragsId) }
        val korrigerteLinjeIder: MutableList<Int> = ArrayList()
        if (korrigerteLinjer.isNotEmpty()) {
            var linje = linjeId.toInt()
            for (korreksjon in korrigerteLinjer) {
                val korrLinje = korreksjon.linje
                if (korrLinje == linje) {
                    korrigerteLinjeIder.add(korrLinje)
                    linje = korreksjon.korrigertLinje
                }
            }
            korrigerteLinjeIder.add(linje)
        } else
            korrigerteLinjeIder.add(linjeId.toInt())
        return korrigerteLinjeIder
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
