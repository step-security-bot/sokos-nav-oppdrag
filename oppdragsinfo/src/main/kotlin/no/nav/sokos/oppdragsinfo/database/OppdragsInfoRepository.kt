package no.nav.sokos.oppdragsinfo.database

import java.sql.Connection
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.param
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.toOppdrag
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.withParameters
import no.nav.sokos.oppdragsinfo.domain.Oppdrag

object OppdragsInfoRepository {

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
}

