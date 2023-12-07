package no.nav.sokos.oppdragsinfo.database

import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.getColumn
import java.sql.Connection
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.param
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.toList
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.withParameters
import no.nav.sokos.oppdragsinfo.domain.*
import java.sql.ResultSet

object OppdragsInfoRepository {

    fun Connection.hentOppdragslinje(
        oppdragId: Int,
        oppdragslinje: Int
    ): List<Oppdragslinje> {
        val resultSet = prepareStatement(
            """
                SELECT *
                FROM T_OPPDRAGSLINJE
                WHERE OPPDRAGS_ID = ?
                and LINJE_ID = ?
            """.trimIndent()
        ).withParameters(
            param(oppdragId),
            param(oppdragslinje)
        ).executeQuery()
        return toOppdragslinje(resultSet)
    }

    fun Connection.hentOppdrag(
        oppdragId: Int
    ): List<Oppdrag> {
        val resultSet = prepareStatement(
            """
                SELECT *
                FROM T_OPPDRAG
                WHERE OPPDRAGS_ID = ?
            """.trimIndent()
        ).withParameters(
            param(oppdragId),
        ).executeQuery()
        return toOppdrag(resultSet)
    }

    fun Connection.hentOppdrag(
        gjelderId: String,
        fagSystemId: String?,
        fagGruppeKode: String?,
        vedtakFom: String?
    ): List<Oppdrag> {
        val resultSet = prepareStatement(
            """
            select OPPDRAGS_ID, FAGSYSTEM_ID, KODE_FAGOMRAADE, FREKVENS, KJOR_IDAG, STONAD_ID, DATO_FORFALL, OPPDRAG_GJELDER_ID, TYPE_BILAG, BRUKERID, TIDSPKT_REG 
            from T_OPPDRAG 
            where OPPDRAG_GJELDER_ID = ?
            ${if (fagSystemId != null) " and FAGSYSTEM_ID like ? " else ""}
            ${if (fagGruppeKode != null) " and KODE_FAGOMRAADE in (select F.KODE_FAGOMRAADE from T_FAGOMRAADE F where trim(F.KODE_FAGGRUPPE) like ?) " else ""}
            ${if (vedtakFom != null) " and OPPDRAGS_ID in (select L.OPPDRAGS_ID from T_OPPDRAGSLINJE L where L.OPPDRAGS_ID = oppdrags_id and L.DATO_VEDTAK_FOM between ? and '9999-12-31') " else ""}
            """.trimIndent()
        ).withParameters(
            param(gjelderId),
            fagSystemId?.let { param(it) },
            fagGruppeKode?.let { param(it) },
            vedtakFom?.let { param(it) }
        ).executeQuery()
        return toOppdrag(resultSet)
    }

    fun Connection.hentFagomraade(
        fagomraadenavn: String
    ): List<Fagomraade> {
        val resultSet = prepareStatement(
            """
            select F.KODE_FAGOMRAADE, F.NAVN_FAGOMRAADE, G.KODE_FAGGRUPPE, G.NAVN_FAGGRUPPE  
            from T_FAGOMRAADE F, T_FAGGRUPPE G 
            where F.KODE_FAGOMRAADE = ?
            and F.KODE_FAGGRUPPE = G.KODE_FAGGRUPPE
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
            select OPPDRAGS_ID, KODE_STATUS, LOPENR, TIDSPKT_REG, BRUKERID 
            from T_OPPDRAG_STATUS 
            where OPPDRAGS_ID = ?
            """.trimIndent()
        ).withParameters(
            param(oppdragsId),
        ).executeQuery()
        return toOppdragstatus(resultSet)
    }

    fun toOppdrag(rs: ResultSet) = rs.toList {
        Oppdrag(
            oppdragsId = getColumn("OPPDRAGS_ID"),
            fagsystemId = getColumn("FAGSYSTEM_ID"),
            kodeFagOmrade = getColumn("KODE_FAGOMRAADE"),
            frekvens = getColumn("FREKVENS"),
            kjorIdag = getColumn("KJOR_IDAG"),
            stonadId = getColumn("STONAD_ID"),
            datoForfall = getColumn("DATO_FORFALL"),
            oppdragGjelderId = getColumn("OPPDRAG_GJELDER_ID"),
            typeBilag = getColumn("TYPE_BILAG"),
            brukerId = getColumn("BRUKERID"),
            tidspunktReg = getColumn("TIDSPKT_REG")
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
            attestert = getColumn("ATTESTERT"),
            vedtaksId = getColumn("VEDTAK_ID"),
            utbetalesTilId = getColumn("UTBETALES_TIL_ID"),
            refunderesOrgnr = getColumn("REFUNDERES_ID"),
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
            tidspktReg = getColumn("TIDSPKT_REG"),
            brukerid = getColumn("BRUKERID")
        )
    }
}

