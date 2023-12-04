package no.nav.sokos.oppdragsinfo.database

import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.param
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.toOppdrag
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.withParameters
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import java.sql.Connection

object OppdragsInfoRepository {

    //DB2 greie som gjør queries raskere. Må kjøres før hver query.
    //Funker ikke med testcontainers siden de ikke har en aksellerator siden det er en servergreie
    // tro meg jeg har prøvd...
    fun Connection.setAcceleration() {
        prepareStatement("SET CURRENT QUERY ACCELERATION ALL;").execute()
    }

    fun Connection.hentOppdrag(
            oppdragId: String,
    ): List<Oppdrag> =
            prepareStatement(
                    """
                SELECT *
                FROM T_OPPDRAG
                WHERE OPPDRAGS_ID = (?)
            """.trimIndent()
            ).withParameters(
                    param(oppdragId)
            ).executeQuery().toOppdrag()

    /*    fun Connection.hentOppdragsinfoMedOffnr(
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
        }*/
}

