package no.nav.sokos.oppdragsinfo.database

import java.sql.Connection
import java.sql.ResultSet
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.getColumn
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.param
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.toList
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.withParameters
import no.nav.sokos.oppdragsinfo.domain.Attestant
import no.nav.sokos.oppdragsinfo.domain.LinjeStatus
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
        SELECT 
            OPLI.LINJE_ID,
            OPLI.KODE_KLASSE,
            OPLI.DATO_VEDTAK_FOM,
            OPLI.DATO_VEDTAK_TOM,
            OPLI.SATS,
            OPLI.TYPE_SATS,
            LIST.KODE_STATUS,
            LIST.DATO_FOM,
            OPLI.ATTESTERT,
            KORR.LINJE_ID_KORR
        FROM OS231Q1.T_KJOREDATO KJDA, OS231Q1.T_OPPDRAGSLINJE OPLI, OS231Q1.T_LINJE_STATUS LIST
        LEFT OUTER JOIN OS231Q1.T_KORREKSJON KORR
            ON LIST.OPPDRAGS_ID = KORR.OPPDRAGS_ID
            AND LIST.LINJE_ID = KORR.LINJE_ID
            WHERE OPLI.OPPDRAGS_ID = (?)
            AND LIST.OPPDRAGS_ID = OPLI.OPPDRAGS_ID
            AND LIST.LINJE_ID = OPLI.LINJE_ID
            AND LIST.TIDSPKT_REG = (SELECT MAX(LIS1.TIDSPKT_REG)
                                    FROM OS231Q1.T_LINJE_STATUS LIS1
                                    WHERE LIS1.OPPDRAGS_ID = LIST.OPPDRAGS_ID
                                    AND LIS1.LINJE_ID = LIST.LINJE_ID
                                    AND (CASE WHEN KJDA.KJOREDATO <= LIS1.DATO_FOM
                                    THEN (SELECT MIN(LIS2.DATO_FOM)
                                            FROM OS231Q1.T_LINJE_STATUS LIS2
                                            WHERE  LIS2.OPPDRAGS_ID = LIST.OPPDRAGS_ID
                                            AND LIS2.LINJE_ID = LIST.LINJE_ID)
                                            WHEN 1 < (SELECT COUNT(*)
                                                        FROM OS231Q1.T_LINJE_STATUS LIS3
                                                        WHERE LIS3.OPPDRAGS_ID = LIST.OPPDRAGS_ID
                                                        AND LIS3.LINJE_ID = LIST.LINJE_ID
                                                        AND LIS3.KODE_STATUS = 'KORR')
                                                        THEN LIS1.DATO_FOM
                                                        ELSE (SELECT MAX(LIS4.DATO_FOM)
                                                                FROM OS231Q1.T_LINJE_STATUS LIS4
                                                                WHERE LIS4.OPPDRAGS_ID = LIST.OPPDRAGS_ID
                                                                AND LIS4.LINJE_ID = LIST.LINJE_ID) END) = LIS1.DATO_FOM)
        """.trimIndent()
    ).withParameters(
        param(oppdragId)
    ).run {
        executeQuery().toOppdragsLinjer()
    }

fun Connection.hentOppdragsLinjeStatuser (
    oppdragsId: Int,
    linjeId: Int
): List<LinjeStatus> =
    prepareStatement (
        """
            SELECT KODE_STATUS, DATO_FOM, TIDSPKT_REG, BRUKERID
            FROM T_LINJE_STATUS 
            WHERE OPPDRAGS_ID = (?)
            AND LINJE_ID = (?)
            ORDER BY DATO_FOM
            """.trimIndent()
    ).withParameters(
        param(oppdragsId), param(linjeId)
    ).run {
        executeQuery().toOppdragsLinjeStatuser()
    }

fun Connection.hentOppdragsLinjeAttestanter (
    oppdragsId: Int,
    linjeId: Int
): List<Attestant> =
    prepareStatement (
        """
            SELECT ATTESTANT_ID, DATO_UGYLDIG_FOM
            FROM T_ATTESTASJON 
            WHERE OPPDRAGS_ID = (?)
            AND LINJE_ID = (?)
            ORDER BY DATO_UGYLDIG_FOM
            """.trimIndent()
    ).withParameters(
        param(oppdragsId), param(linjeId)
    ).run {
        executeQuery().toOppdragsLinjeAttestanter()
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
        executeQuery().toOppdragsEnhet()
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
        navnFagGruppe = getColumn("NAVN_FAGGRUPPE"),
        navnFagOmraade = getColumn("NAVN_FAGOMRAADE"),
        kjorIdag = getColumn("KJOR_IDAG"),
        typeBilag = getColumn("TYPE_BILAG"),
        kodeStatus = getColumn("KODE_STATUS")
    )
}

private fun ResultSet.toOppdragsLinjer() = toList {
    OppdragsLinje(
        linjeId = getColumn("LINJE_ID"),
        kodeKlasse = getColumn("KODE_KLASSE"),
        datoVedtakFom = getColumn("DATO_VEDTAK_FOM"),
        datoVedtakTom = getColumn("DATO_VEDTAK_TOM"),
        sats = getColumn("SATS"),
        typeSats = getColumn("TYPE_SATS"),
        kodeStatus = getColumn("KODE_STATUS"),
        datoFom = getColumn("DATO_FOM"),
        linjeIdKorr = getColumn("LINJE_ID_KORR"),
        attestert = getColumn("ATTESTERT")
    )
}
private fun ResultSet.toOppdragsLinjeStatuser() = toList {
    LinjeStatus(
        status = getColumn("KODE_STATUS"),
        datoFom = getColumn("DATO_FOM"),
        tidspktReg = getColumn("TIDSPKT_REG"),
        brukerid = getColumn("BRUKERID")
    )
}

private fun ResultSet.toOppdragsLinjeAttestanter() = toList {
    Attestant(
        attestantId = getColumn("ATTESTANT_ID"),
        ugyldigFom = getColumn("DATO_UGYLDIG_FOM")
    )
}

private fun ResultSet.toOppdragsEnhet() = toList {
    OppdragsEnhet(
        type = getColumn("TYPE_ENHET"),
        enhet = getColumn("ENHET"),
        datoFom = getColumn("DATO_FOM")
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



