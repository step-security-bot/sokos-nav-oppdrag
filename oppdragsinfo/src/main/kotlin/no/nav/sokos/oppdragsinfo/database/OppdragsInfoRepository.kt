package no.nav.sokos.oppdragsinfo.database

import java.sql.Connection
import java.sql.ResultSet
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.getColumn
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.param
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.toList
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.withParameters
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import no.nav.sokos.oppdragsinfo.domain.OppdragsEnhet
import no.nav.sokos.oppdragsinfo.domain.OppdragsInfo
import no.nav.sokos.oppdragsinfo.domain.OppdragsLinje


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


fun Connection.hentOppdragsLinjer(
    oppdragId: Int
): List<OppdragsLinje> =
    prepareStatement(
        """
            SELECT B.LINJE_ID,
       D.KODE_KLASSE,
       D.DATO_VEDTAK_FOM,
       D.DATO_VEDTAK_TOM,
       D.SATS,
       D.TYPE_SATS,
       C.KODE_STATUS,
       C.DATO_FOM,
       A.LINJE_ID_KORR,
       D.ATTESTERT
FROM (SELECT *
      FROM OS231Q1.T_KORREKSJON
      WHERE OPPDRAGS_ID = (?)) A
         FULL JOIN (SELECT *
                    FROM OS231Q1.T_ATTESTASJON a1
                    WHERE OPPDRAGS_ID = (?)
                      AND DATO_UGYLDIG_FOM > current_date
                      AND LOPENR =
                          (SELECT MAX(LOPENR)
                           FROM OS231Q1.T_ATTESTASJON a2
                           WHERE a2.OPPDRAGS_ID = a1.OPPDRAGS_ID
                             AND a2.LINJE_ID = a1.LINJE_ID
                             AND a2.ATTESTANT_ID = a1.ATTESTANT_ID)) B
                   ON A.OPPDRAGS_ID = B.OPPDRAGS_ID
                       AND A.LINJE_ID = B.LINJE_ID
         FULL JOIN (SELECT *
                    FROM OS231Q1.T_LINJE_STATUS LIST
                    WHERE LIST.OPPDRAGS_ID = (?)
                      AND LIST.TIDSPKT_REG = (SELECT MAX(LIS2.TIDSPKT_REG)
                                              FROM OS231Q1.T_LINJE_STATUS LIS2
                                              WHERE LIS2.OPPDRAGS_ID = LIST.OPPDRAGS_ID
                                                AND LIS2.LINJE_ID = LIST.LINJE_ID
                                                AND LIS2.DATO_FOM <= (SELECT MIN(KJPL.DATO_BEREGN_FOM)
                                                                      FROM OS231Q1.T_KJOREPLAN KJPL,
                                                                           OS231Q1.T_OPPDRAG OPPD,
                                                                           OS231Q1.T_FAGOMRAADE FAGO
                                                                      WHERE KJPL.KODE_FAGGRUPPE = FAGO.KODE_FAGGRUPPE
                                                                        AND FAGO.KODE_FAGOMRAADE = OPPD.KODE_FAGOMRAADE
                                                                        AND KJPL.STATUS = 'PLAN'
                                                                        AND KJPL.FREKVENS = OPPD.FREKVENS
                                                                        AND OPPD.OPPDRAGS_ID = LIST.OPPDRAGS_ID))) C
                   ON B.OPPDRAGS_ID = C.OPPDRAGS_ID
                       AND B.LINJE_ID = C.LINJE_ID
         FULL JOIN (SELECT *
                    FROM OS231Q1.T_OPPDRAGSLINJE
                    WHERE OPPDRAGS_ID = (?)) D
                   ON C.OPPDRAGS_ID = D.OPPDRAGS_ID
                       AND C.LINJE_ID = D.LINJE_ID
        """.trimIndent()
    ).withParameters(
        param(oppdragId), param(oppdragId), param(oppdragId), param(oppdragId)
    ).run {
        executeQuery().toOppdragsLinjer()
    }


fun Connection.hentOppdragsEnheter (
    oppdragsId: Int
): List<OppdragsEnhet> =
    prepareStatement (
        """
            SELECT * 
            FROM T_OPPDRAGSENHET 
            WHERE OPPDRAGS_ID = (?)
            """.trimIndent()
    ).withParameters(
        param(oppdragsId)
    ).run {
        executeQuery().toOppdragsenhet()
    }

private fun ResultSet.toOppdragsenhet() = toList {
    OppdragsEnhet(
        type = getColumn("TYPE_ENHET"),
        enhet = getColumn("ENHET"),
        datoFom = getColumn("DATO_FOM")
    )
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

private fun ResultSet.toOppdragsLinjer() = toList {
    OppdragsLinje(
        linjeId = getColumn("LINJE_ID"),
        klasseKode = getColumn("KODE_KLASSE"),
        vedtakFom = getColumn("DATO_VEDTAK_FOM"),
        vedtakTom = getColumn("DATO_VEDTAK_TOM"),
        sats = getColumn("SATS"),
        satsType = getColumn("TYPE_SATS"),
        status = getColumn("KODE_STATUS"),
        linjeIdKorreksjon = getColumn("LINJE_ID_KORR"),
        attestert = getColumn("ATTESTERT")
    )
}

/*fun ResultSet.toOppdragslinjeDetaljer() = toList {
    OppdragsLinje(
        oppdragsId = getColumn("OPPDRAGS_ID"),
        linjeId = getColumn("LINJE_ID"),
        delytelseId = getColumn("DELYTELSE_ID"),
        sats = getColumn("SATS"),
        typeSats = getColumn("TYPE_SATS"),
        vedtakFom = getColumn("DATO_VEDTAK_FOM"),
        vedtakTom = getColumn("DATO_VEDTAK_TOM"),
        kodeKlasse = getColumn("KODE_KLASSE"),
        attestert = getColumn("ATTESTERT"),
        vedtaksId = getColumn("VEDTAK_ID"),
        utbetalesTilId = getColumn("UTBETALES_TIL_ID"),
        refunderesOrgnr = getColumn("REFUNDERES_ID"),
        brukerid = getColumn("BRUKERID"),
        tidspktReg = getColumn("TIDSPKT_REG")
    )
}*/



