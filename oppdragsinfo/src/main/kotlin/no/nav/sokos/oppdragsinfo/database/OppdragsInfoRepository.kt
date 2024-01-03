package no.nav.sokos.oppdragsinfo.database

import java.sql.Connection
import java.sql.ResultSet
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.getColumn
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.param
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.toList
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.withParameters
import no.nav.sokos.oppdragsinfo.domain.Attest
import no.nav.sokos.oppdragsinfo.domain.Faggruppe
import no.nav.sokos.oppdragsinfo.domain.Fagomraade
import no.nav.sokos.oppdragsinfo.domain.Grad
import no.nav.sokos.oppdragsinfo.domain.Kid
import no.nav.sokos.oppdragsinfo.domain.Klasse
import no.nav.sokos.oppdragsinfo.domain.Korreksjon
import no.nav.sokos.oppdragsinfo.domain.Kravhaver
import no.nav.sokos.oppdragsinfo.domain.LinjeStatus
import no.nav.sokos.oppdragsinfo.domain.Linjeenhet
import no.nav.sokos.oppdragsinfo.domain.Maksdato
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import no.nav.sokos.oppdragsinfo.domain.OppdragStatus
import no.nav.sokos.oppdragsinfo.domain.OppdragsTekst
import no.nav.sokos.oppdragsinfo.domain.Oppdragsenhet
import no.nav.sokos.oppdragsinfo.domain.Oppdragslinje
import no.nav.sokos.oppdragsinfo.domain.Skyldner
import no.nav.sokos.oppdragsinfo.domain.Valuta

object OppdragsInfoRepository {

    fun Connection.finnOppdrag(
        gjelderId: String
    ): List<Oppdrag> =
        prepareStatement(
            """
                SELECT op.oppdrags_id,
                        op.fagsystem_id,
                        fo.navn_fagomraade,
                        op.oppdrag_gjelder_id,
                        op.type_bilag,
                        fg.navn_faggruppe,
                        os.kode_status,
                        os.tidspkt_Reg
                FROM os231q1.t_oppdrag op,
                    os231q1.t_fagomraade fo, 
                    os231q1.t_faggruppe fg,
                    os231q1.t_oppdrag_status os
                WHERE op.oppdrag_gjelder_id = (?)
                AND op.fagsystem_id = '0000004'
                AND op.kode_fagomraade = 'BISYS'
                AND fo.kode_fagomraade = op.kode_fagomraade
                AND fg.kode_faggruppe = fo.kode_faggruppe
                AND os.oppdrags_id = op.oppdrags_id
                AND os.tidspkt_Reg = (
                SELECT MAX(os2.tidspkt_Reg)
                FROM os231q1.t_oppdrag_Status os2
                WHERE os2.oppdrags_id = os.oppdrags_id);
            """.trimIndent()
        ).withParameters(
            param(gjelderId)
        ).run {
            executeQuery().toOppdragList()
        }
}

fun Connection.hentOppdragslinje(
    oppdragId: Int,
    oppdragslinje: Int
): List<Oppdragslinje> {
    val resultSet = prepareStatement(
        """
                SELECT *
                FROM T_OPPDRAGSLINJE
                WHERE OPPDRAGS_ID = ?
                AND LINJE_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    return toOppdragslinje(resultSet)
}

fun Connection.hentSkyldnere(
    oppdragId: Int,
    oppdragslinje: Int
): List<Skyldner> {
    val resultSet = prepareStatement(
        """
                SELECT *
                FROM T_SKYLDNER
                WHERE OPPDRAGS_ID = ?
                AND LINJE_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    return toSkyldner(resultSet)
}

fun Connection.hentValutaer(
    oppdragId: Int,
    oppdragslinje: Int
): List<Valuta> {
    val resultSet = prepareStatement(
        """
                SELECT *
                FROM T_VALUTA
                WHERE OPPDRAGS_ID = ?
                AND LINJE_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    return toValuta(resultSet)
}

fun Connection.hentLinjeenheter(
    oppdragId: Int,
    oppdragslinje: Int
): List<Linjeenhet> {
    val resultSet = prepareStatement(
        """
                SELECT *
                FROM T_LINJEENHET
                WHERE OPPDRAGS_ID = ?
                AND LINJE_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    return toLinjeenhet(resultSet)
}

fun Connection.hentGrader(
    oppdragId: Int,
    oppdragslinje: Int
): List<Grad> {
    val resultSet = prepareStatement(
        """
                SELECT *
                FROM T_GRAD
                WHERE OPPDRAGS_ID = ?
                AND LINJE_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    return toGrad(resultSet)
}

fun Connection.hentKidlister(
    oppdragId: Int,
    oppdragslinje: Int
): List<Kid> {
    val resultSet = prepareStatement(
        """
                SELECT *
                FROM T_KID
                WHERE OPPDRAGS_ID = ?
                AND LINJE_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    return toLKidlist(resultSet)
}

fun Connection.henOppdragsTekster(
    oppdragId: Int,
    oppdragslinje: Int
): List<OppdragsTekst> {
    val resultSet = prepareStatement(
        """
                SELECT *
                FROM T_TEKST
                WHERE OPPDRAGS_ID = ?
                AND LINJE_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    return toOppdragsTekst(resultSet)
}

fun Connection.hentKorreksjoner(
    oppdragId: Int,
    oppdragslinje: Int
): List<Korreksjon> {
    val resultSet = prepareStatement(
        """
                SELECT *
                FROM T_KORREKSJON
                WHERE OPPDRAGS_ID = ?
                AND LINJE_ID = ?
                ORDER BY TIDSPKT_REG
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    return toKorreksjon(resultSet)
}

fun Connection.eksistererKorreksjoner(
    oppdragId: Int,
    oppdragslinje: Int
): Boolean {
    val resultSet = prepareStatement(
        """
                SELECT COUNT(*) 
                FROM T_KORREKSJON
                WHERE OPPDRAGS_ID = ?
                AND LINJE_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    resultSet.next()
    return resultSet.getInt(1) > 0
}

fun Connection.henKravhavere(
    oppdragId: Int,
    oppdragslinje: Int
): List<Kravhaver> {
    val resultSet = prepareStatement(
        """
                SELECT *
                FROM T_KRAVHAVER
                WHERE OPPDRAGS_ID = ?
                AND LINJE_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    return toKravhaver(resultSet)
}

fun Connection.henMaksdatoer(
    oppdragId: Int,
    oppdragslinje: Int
): List<Maksdato> {
    val resultSet = prepareStatement(
        """
                SELECT *
                FROM T_MAKS_DATO
                WHERE OPPDRAGS_ID = ?
                AND LINJE_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    return toMaksdato(resultSet)
}

fun Connection.hentKlasse(
    kodeKlasse: String
): List<Klasse> {
    val resultSet = prepareStatement(
        """
                SELECT *
                FROM T_KLASSEKODE
                WHERE KODE_KLASSE = ?
            """.trimIndent()
    ).withParameters(
        param(kodeKlasse)
    ).executeQuery()
    return toKlasse(resultSet)
}

fun Connection.hentLinjestatuser(
    oppdragId: Int,
    oppdragslinje: Int
): List<LinjeStatus> {
    val resultSet = prepareStatement(
        """
                SELECT    LIST.OPPDRAGS_ID,
                          LIST.KODE_STATUS,
                          LIST.LINJE_ID,
                          LIST.DATO_FOM,
                          LIST.LOPENR,
                          LIST.BRUKERID,
                          LIST.TIDSPKT_REG
                FROM T_LINJE_STATUS LIST
                WHERE LIST.OPPDRAGS_ID = ?
                  AND LIST.LINJE_ID = ?
                  AND LIST.TIDSPKT_REG = (
                      SELECT MAX(LIS2.TIDSPKT_REG)
                      FROM   T_LINJE_STATUS LIS2
                      WHERE LIS2.OPPDRAGS_ID = LIST.OPPDRAGS_ID
                          AND LIS2.LINJE_ID = LIST.LINJE_ID
                          AND LIS2.DATO_FOM <= (
                              SELECT MIN(KJPL.DATO_BEREGN_FOM)
                              FROM    T_KJOREPLAN KJPL, 
                                      T_OPPDRAG OPPD,
                                      T_FAGOMRAADE FAGO
                              WHERE KJPL.KODE_FAGGRUPPE 	= FAGO.KODE_FAGGRUPPE
                                  AND FAGO.KODE_FAGOMRAADE	= OPPD.KODE_FAGOMRAADE
                                  AND KJPL.STATUS			= 'PLAN'
                                  AND KJPL.FREKVENS			= OPPD.FREKVENS
                                  AND OPPD.OPPDRAGS_ID		= LIST.OPPDRAGS_ID           
						)                                                     	
		)
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    return toLinjestatus(resultSet)
}

fun Connection.eksistererLinjestatuser(
    oppdragId: Int,
    oppdragslinje: Int
): Boolean {
    val resultSet = prepareStatement(
        """
                SELECT COUNT(*) 
                FROM T_LINJE_STATUS LIST
                WHERE LIST.OPPDRAGS_ID = ?
                  AND LIST.LINJE_ID = ?
                  AND LIST.TIDSPKT_REG = (
                      SELECT MAX(LIS2.TIDSPKT_REG)
                      FROM   T_LINJE_STATUS LIS2
                      WHERE LIS2.OPPDRAGS_ID = LIST.OPPDRAGS_ID
                          AND LIS2.LINJE_ID = LIST.LINJE_ID
                          AND LIS2.DATO_FOM <= (
                              SELECT MIN(KJPL.DATO_BEREGN_FOM)
                              FROM    T_KJOREPLAN KJPL, 
                                      T_OPPDRAG OPPD,
                                      T_FAGOMRAADE FAGO
                              WHERE KJPL.KODE_FAGGRUPPE 	= FAGO.KODE_FAGGRUPPE
                                  AND FAGO.KODE_FAGOMRAADE	= OPPD.KODE_FAGOMRAADE
                                  AND KJPL.STATUS			= 'PLAN'
                                  AND KJPL.FREKVENS			= OPPD.FREKVENS
                                  AND OPPD.OPPDRAGS_ID		= LIST.OPPDRAGS_ID           
						)                                                     	
		)
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    resultSet.next()
    return resultSet.getInt(1) > 0
}

fun Connection.hentAttestasjoner(
    oppdragId: Int,
    oppdragslinje: Int
): List<Attest> {
    val resultSet = prepareStatement(
        """
                SELECT OPPDRAGS_ID, 
                LINJE_ID,
                ATTESTANT_ID, 
                LOPENR, 
                DATO_UGYLDIG_FOM,
                BRUKERID,
                TIDSPKT_REG
                FROM T_ATTESTASJON a1
                WHERE OPPDRAGS_ID = ?
                AND LINJE_ID = ?
                AND DATO_UGYLDIG_FOM > current_date 
                AND LOPENR = 
                  ( SELECT MAX(LOPENR) 
                  FROM T_ATTESTASJON a2 
                  WHERE a2.OPPDRAGS_ID = a1.OPPDRAGS_ID 
                  AND a2.LINJE_ID = a1.LINJE_ID 
                  AND a2.ATTESTANT_ID = a1.ATTESTANT_ID ) 
                ORDER BY TIDSPKT_REG
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    return toAttest(resultSet)
}

fun Connection.eksistererAttestasjoner(
    oppdragId: Int,
    oppdragslinje: Int
): Boolean {
    val resultSet = prepareStatement(
        """
                SELECT COUNT(*) 
                FROM T_ATTESTASJON a1
                WHERE OPPDRAGS_ID = ?
                AND LINJE_ID = ?
                AND DATO_UGYLDIG_FOM > current_date 
                AND LOPENR = 
                  ( SELECT MAX(LOPENR) 
                  FROM T_ATTESTASJON a2 
                  WHERE a2.OPPDRAGS_ID = a1.OPPDRAGS_ID 
                  AND a2.LINJE_ID = a1.LINJE_ID 
                  AND a2.ATTESTANT_ID = a1.ATTESTANT_ID ) 
            """.trimIndent()
    ).withParameters(
        param(oppdragId),
        param(oppdragslinje)
    ).executeQuery()
    resultSet.next()
    return resultSet.getInt(1) > 0
}

/*fun Connection.hentOppdrag(
    oppdragId: Int
): List<Oppdrag> {
    val resultSet = prepareStatement(
        """
                SELECT OPPDRAGS_ID, FAGSYSTEM_ID, KODE_FAGOMRAADE, FREKVENS, KJOR_IDAG, STONAD_ID, DATO_FORFALL, OPPDRAG_GJELDER_ID, TYPE_BILAG, BRUKERID, TIDSPKT_REG 
                FROM T_OPPDRAG
                WHERE OPPDRAGS_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragId)
    ).executeQuery()
    return toOppdrag(resultSet)
}*/

fun Connection.hentOppdragslinjer(
    oppdragId: Int
): List<Oppdragslinje> {
    val resultSet = prepareStatement(
        """
                SELECT * 
                FROM T_OPPDRAGSLINJE
                WHERE OPPDRAGS_ID = ?
                ORDER BY LINJE_ID
            """.trimIndent()
    ).withParameters(
        param(oppdragId)
    ).executeQuery()
    return toOppdragslinje(resultSet)
}

fun Connection.hentFagomraade(
    fagomraadenavn: String
): List<Fagomraade> {
    val resultSet = prepareStatement(
        """
            SELECT F.KODE_FAGOMRAADE, F.NAVN_FAGOMRAADE, G.KODE_FAGGRUPPE, G.NAVN_FAGGRUPPE  
            FROM T_FAGOMRAADE F, T_FAGGRUPPE G 
            WHERE F.KODE_FAGOMRAADE = ?
            AND F.KODE_FAGGRUPPE = G.KODE_FAGGRUPPE
            """.trimIndent()
    ).withParameters(
        param(fagomraadenavn),
    ).executeQuery()
    return toFagomraade(resultSet)
}

fun Connection.hentOppdragstatus(
    oppdragsId: Int
): List<OppdragStatus> {
    val resultSet = prepareStatement(
        """
            SELECT * 
            FROM T_OPPDRAG_STATUS 
            WHERE OPPDRAGS_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragsId)
    ).executeQuery()
    return toOppdragstatus(resultSet)
}

fun Connection.eksistererOppdragstatus(
    oppdragsId: Int
): Boolean {
    val resultSet = prepareStatement(
        """
            SELECT COUNT(*)  
            FROM T_OPPDRAG_STATUS 
            WHERE OPPDRAGS_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragsId)
    ).executeQuery()
    resultSet.next()
    return resultSet.getInt(1) > 0
}

fun Connection.hentOppdragsenhet(
    oppdragsId: Int
): List<Oppdragsenhet> {
    val resultSet = prepareStatement(
        """
            SELECT * 
            FROM T_OPPDRAGSENHET 
            WHERE OPPDRAGS_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragsId)
    ).executeQuery()
    return toOppdragsenhet(resultSet)
}

fun Connection.eksistererOppdragsenhet(
    oppdragsId: Int
): Boolean {
    val resultSet = prepareStatement(
        """
            SELECT COUNT(*)  
            FROM T_OPPDRAGSENHET 
            WHERE OPPDRAGS_ID = ?
            """.trimIndent()
    ).withParameters(
        param(oppdragsId)
    ).executeQuery()
    resultSet.next()
    return resultSet.getInt(1) > 0
}

private fun ResultSet.toOppdragList() = toList {
    Oppdrag(
        fagsystemId = getColumn("FAGSYSTEM_ID"),
        oppdragsId = getColumn("OPPDRAGS_ID"),
        fagGruppeNavn = getColumn("NAVN_FAGGRUPPE"),
        fagOmraadeNavn = getColumn("NAVN_FAGOMRAADE"),
        bilagsType = getColumn("TYPE_BILAG"),
        status = getColumn("KODE_STATUS")
    )
}

fun toOppdragslinje(rs: ResultSet) = rs.toList {
    Oppdragslinje(
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
}

fun toSkyldner(rs: ResultSet) = rs.toList {
    Skyldner(
        oppdragsId = getColumn("OPPDRAGS_ID"),
        linjeId = getColumn("LINJE_ID"),
        skyldnerId = getColumn("SKYLDNER_ID"),
        datoFom = getColumn("DATO_FOM"),
        brukerid = getColumn("BRUKERID"),
        tidspktReg = getColumn("TIDSPKT_REG")
    )
}

fun toValuta(rs: ResultSet) = rs.toList {
    Valuta(
        oppdragsId = getColumn("OPPDRAGS_ID"),
        linjeId = getColumn("LINJE_ID"),
        nokkelId = getColumn("NOKKEL_ID"),
        typeValuta = getColumn("TYPE_VALUTA"),
        valuta = getColumn("VALUTA"),
        feilreg = getColumn("FEILREG"),
        datoFom = getColumn("DATO_FOM"),
        brukerid = getColumn("BRUKERID"),
        tidspktReg = getColumn("TIDSPKT_REG")
    )
}

fun toLinjeenhet(rs: ResultSet) = rs.toList {
    Linjeenhet(
        oppdragsId = getColumn("OPPDRAGS_ID"),
        linjeId = getColumn("LINJE_ID"),
        nokkelId = getColumn("NOKKEL_ID"),
        typeEnhet = getColumn("TYPE_ENHET"),
        enhet = getColumn("ENHET"),
        datoFom = getColumn("DATO_FOM"),
        brukerid = getColumn("BRUKERID"),
        tidspktReg = getColumn("TIDSPKT_REG")
    )
}

fun toGrad(rs: ResultSet) = rs.toList {
    Grad(
        oppdragsId = getColumn("OPPDRAGS_ID"),
        linjeId = getColumn("LINJE_ID"),
        typeGrad = getColumn("TYPE_GRAD"),
        grad = getColumn("GRAD"),
        brukerid = getColumn("BRUKERID"),
        tidspktReg = getColumn("TIDSPKT_REG")
    )
}

fun toLKidlist(rs: ResultSet) = rs.toList {
    Kid(
        oppdragsId = getColumn("OPPDRAGS_ID"),
        linjeId = getColumn("LINJE_ID"),
        kid = getColumn("KID"),
        datoFom = getColumn("DATO_FOM"),
        brukerid = getColumn("BRUKERID"),
        tidspktReg = getColumn("TIDSPKT_REG")
    )
}

fun toOppdragsTekst(rs: ResultSet) = rs.toList {
    OppdragsTekst(
        oppdragsId = getColumn("OPPDRAGS_ID"),
        linjeId = getColumn("LINJE_ID"),
        nokkelId = getColumn("NOKKEL_ID"),
        tekstLnr = getColumn("TEKST_LNR"),
        tekstkode = getColumn("TEKSTKODE"),
        tekst = getColumn("TEKST"),
        feilreg = getColumn("FEILREG"),
        datoTom = getColumn("DATO_TOM"),
        datoFom = getColumn("DATO_FOM"),
        brukerid = getColumn("BRUKERID"),
        tidspktReg = getColumn("TIDSPKT_REG")
    )
}

fun toKravhaver(rs: ResultSet) = rs.toList {
    Kravhaver(
        oppdragsId = getColumn("OPPDRAGS_ID"),
        linjeId = getColumn("LINJE_ID"),
        kravhaverId = getColumn("KRAVHAVER_ID"),
        datoFom = getColumn("DATO_FOM"),
        brukerid = getColumn("BRUKERID"),
        tidspktReg = getColumn("TIDSPKT_REG")
    )
}

fun toMaksdato(rs: ResultSet) = rs.toList {
    Maksdato(
        oppdragsId = getColumn("OPPDRAGS_ID"),
        linjeId = getColumn("LINJE_ID"),
        maksdato = getColumn("MAKS_DATO"),
        datoFom = getColumn("DATO_FOM"),
        brukerid = getColumn("BRUKERID"),
        tidspktReg = getColumn("TIDSPKT_REG")
    )
}

fun toFagomraade(rs: ResultSet) = rs.toList {
    Fagomraade(
        kode = getColumn("KODE_FAGOMRAADE"),
        navn = getColumn("NAVN_FAGOMRAADE"),
        faggruppe = Faggruppe(
            kode = getColumn("KODE_FAGGRUPPE"),
            navn = getColumn("NAVN_FAGGRUPPE")
        )
    )
}

fun toOppdragstatus(rs: ResultSet) = rs.toList {
    OppdragStatus(
        oppdragsId = getColumn("OPPDRAGS_ID"),
        kode = getColumn("KODE_STATUS"),
        lopenr = getColumn("LOPENR"),
        brukerid = getColumn("BRUKERID"),
        tidspktReg = getColumn("TIDSPKT_REG")
    )
}

fun toOppdragsenhet(rs: ResultSet) = rs.toList {
    Oppdragsenhet(
        type = getColumn("TYPE_ENHET"),
        enhet = getColumn("ENHET"),
        datoFom = getColumn("DATO_FOM")
    )
}

fun toAttest(rs: ResultSet) = rs.toList {
    Attest(
        oppdragsId = getColumn("OPPDRAGS_ID"),
        linjeId = getColumn("LINJE_ID"),
        attestantId = getColumn("ATTESTANT_ID"),
        lopenr = getColumn("LOPENR"),
        ugyldigFom = getColumn("DATO_UGYLDIG_FOM"),
        brukerid = getColumn("BRUKERID"),
        tidspktReg = getColumn("TIDSPKT_REG")
    )
}

fun toKorreksjon(rs: ResultSet) = rs.toList {
    Korreksjon(
        oppdragsId = getColumn("OPPDRAGS_ID"),
        linjeId = getColumn("LINJE_ID"),
        oppdragsIdKorr = getColumn("OPPDRAGS_ID_KORR"),
        linjeIdKorr = getColumn("LINJE_ID_KORR")
    )
}

fun toLinjestatus(rs: ResultSet) = rs.toList {
    LinjeStatus(
        oppdragsId = getColumn("OPPDRAGS_ID"),
        linjeId = getColumn("LINJE_ID"),
        datoFom = getColumn("DATO_FOM"),
        tidspktReg = getColumn("TIDSPKT_REG"),
        brukerid = getColumn("BRUKERID")
    )
}

fun toKlasse(rs: ResultSet) = rs.toList {
    Klasse(
        kode = getColumn("KODE_KLASSE"),
        beskrivelse = getColumn("BESKR_KLASSE")
    )
}

