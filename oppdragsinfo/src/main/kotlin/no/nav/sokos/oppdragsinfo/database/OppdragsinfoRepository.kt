package no.nav.sokos.oppdragsinfo.database

import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.param
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.toList
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.withParameters
import java.sql.Connection

object OppdragsinfoRepository {

    fun Connection.hentOppdragsinfoMedOffnr(
        offnr: String,
        faggruppeKode: Int?,
        fagSystemId: Int?,
        vedtakFom: String?
    ): List<Unit> {
        prepareStatement(
            """
                SELECT *
                FROM Oppdrag
                WHERE oppdragGjelderIdParam = (?)
                    ${ if (faggruppeKode != null) " AND oppdragFaggruppeIdParam = ?" else "" }
                    ${ if (fagSystemId != null) " AND oppdragFagsystemIdParam = ?" else "" }
                    ${ if (vedtakFom != null) " AND oppdragPeriodeDatoFomParam = ?" else "" }
            """.trimIndent()
        ).withParameters(
            param(offnr),
            faggruppeKode?.let { param(faggruppeKode) },
            fagSystemId?.let { param(fagSystemId) },
            vedtakFom?.let { param(vedtakFom) } // format "yyyy-MM-dd"
        ).run {
            return executeQuery().toList {
            }
        }
    }

    fun Connection.hentOppdragsinfoMedOppdragsId(
        oppdragsId: String
    ): List<Unit> {
        prepareStatement(
            """ 
                SELECT * 
                FROM Oppdrag
                WHERE oppdragId = (?)
            """.trimIndent()
        ).withParameters(
            param(oppdragsId)
        ).run {
            return executeQuery().toList {
            }
        }
    }

    fun Connection.hentOppdragslinjeMedOppdragsId(oppdragsId: String, linjeId: Int
    ): List<Unit> {
        prepareStatement(
            """ 
                SELECT * 
                FROM Oppdragslinje 
                WHERE oppdragslinjeOppdragsIdParam = (?)
                AND oppdragslinjeLinjeIdParam = (?)
            """.trimIndent()
        ).withParameters(
            param(oppdragsId),
            param(linjeId)
        ).run {
            return executeQuery().toList {
            }
        }
    }

    fun Connection.hentOppdragsdetaljerMedOppdragsId(oppdragsId: String
    ): List<Unit> {
        prepareStatement(
            """ 
                SELECT * 
                FROM Oppdrag  
                WHERE oppdragId = (?)
            """.trimIndent()
        ).withParameters(
            param(oppdragsId)
        ).run {
            return executeQuery().toList {
            }
        }
    }

    fun Connection.hentKorreksjonerMedOppdragsId(oppdragsId: String
    ): List<Unit> {
        prepareStatement(
            """ 
                SELECT * 
                FROM Korreksjon
                WHERE korreksjonOppdragsIdParam = (?)
            """.trimIndent()
        ).withParameters(
            param(oppdragsId)
        ).run {
            return executeQuery().toList {
            }
        }
    }

    fun Connection.hentOmposteringerMedOffnr(offnr: String
    ): List<Unit> {
        prepareStatement(
            """ 
                SELECT * 
                FROM Ompostering  
                WHERE omposteringPK.gjelderId = (?)
            """.trimIndent()
        ).withParameters(
            param(offnr)
        ).run {
            return executeQuery().toList {
            }
        }
    }
}

