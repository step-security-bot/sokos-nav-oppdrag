package no.nav.sokos.oppdragsinfo.database

import java.sql.Connection
import java.sql.ResultSet
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.getColumn
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.param
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.toList
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.withParameters
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import no.nav.sokos.oppdragsinfo.domain.OppdragsInfo

object OppdragsInfoRepository {

    fun Connection.getOppdrag(
        gjelderId: String
    ): List<OppdragsInfo> =
        prepareStatement(
            """
                SELECT OPPDRAG_GJELDER_ID
                FROM OS231Q1.T_OPPDRAG
                WHERE OPPDRAG_GJELDER_ID = (?)
            """.trimIndent()
        ).withParameters(
            param(gjelderId)
        ).run {
            executeQuery().toOppdrag()
        }

    fun Connection.getOppdragsListe(
        gjelderId: String
    ): List<Oppdrag> =
        prepareStatement(
            """
                SELECT OP.OPPDRAGS_ID,
                        OP.FAGSYSTEM_ID,
                        FO.NAVN_FAGOMRAADE,
                        OP.OPPDRAG_GJELDER_ID,
                        OP.KJOR_IDAG,
                        OP.TYPE_BILAG,
                        FG.NAVN_FAGGRUPPE,
                        OS.KODE_STATUS,
                        OS.TIDSPKT_REG
                FROM OS231Q1.T_OPPDRAG OP,
                        OS231Q1.T_FAGOMRAADE FO, 
                        OS231Q1.T_FAGGRUPPE FG,
                        OS231Q1.T_OPPDRAG_STATUS OS
                WHERE OP.OPPDRAG_GJELDER_ID = (?)
                AND FO.KODE_FAGOMRAADE = OP.KODE_FAGOMRAADE
                AND FG.KODE_FAGGRUPPE = FO.KODE_FAGGRUPPE
                AND OS.OPPDRAGS_ID = OP.OPPDRAGS_ID
                AND OS.TIDSPKT_REG = (
                SELECT MAX(OS2.TIDSPKT_REG)
                FROM OS231Q1.T_OPPDRAG_STATUS OS2
                WHERE OS2.OPPDRAGS_ID = OS.OPPDRAGS_ID);
            """.trimIndent()
        ).withParameters(
            param(gjelderId)
        ).run {
            executeQuery().toOppdragsListe()
        }
}

private fun ResultSet.toOppdrag() = toList {
    OppdragsInfo(
        gjelderId = getColumn("OPPDRAG_GJELDER_ID"),
        gjelderNavn = ""
    )
}

private fun ResultSet.toOppdragsListe() = toList {
    Oppdrag(
        fagsystemId = getColumn("FAGSYSTEM_ID"),
        oppdragsId = getColumn("OPPDRAGS_ID"),
        faggruppeNavn = getColumn("NAVN_FAGGRUPPE"),
        fagomraadeNavn = getColumn("NAVN_FAGOMRAADE"),
        kjorIdag = getColumn("KJOR_IDAG"),
        bilagsType = getColumn("TYPE_BILAG"),
        status = getColumn("KODE_STATUS")
    )
}



