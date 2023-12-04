package no.nav.sokos.oppdragsinfo.service

import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.ApplicationCall
import jako.database.Database
import jako.dsl.Dialect
import jako.dsl.conditions.EQ
import jako.dsl.query.Query
import mu.KotlinLogging
import no.nav.sokos.oppdragsinfo.audit.AuditLogg
import no.nav.sokos.oppdragsinfo.audit.AuditLogger
import no.nav.sokos.oppdragsinfo.audit.Saksbehandler
import no.nav.sokos.oppdragsinfo.config.SECURE_LOGGER
import no.nav.sokos.oppdragsinfo.database.Db2DataSource
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdrag
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.useAndHandleErrors
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import no.nav.sokos.oppdragsinfo.security.getSaksbehandler
import org.jooq.*
import org.jooq.impl.DSL
import kotliquery.*
import kotliquery.Row
import no.nav.sokos.oppdragsinfo.api.model.OppdragsSokRequest
import no.nav.sokos.oppdragsinfo.config.PropertiesConfig

private val logger = KotlinLogging.logger {}
private val secureLogger = KotlinLogging.logger(SECURE_LOGGER)

class OppdragsInfoService(
    private val db2DataSource: Db2DataSource = Db2DataSource(),
    private val auditLogger: AuditLogger = AuditLogger(),
    private val oppdragDatabaseConfig: PropertiesConfig.OppdragDatabaseConfig = PropertiesConfig.OppdragDatabaseConfig()
) {

    fun hentOppdrag(
        oppdragsId: String,
        applicationCall: ApplicationCall
    ): List<Oppdrag> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info("Henter oppdrag med java.sql")
        secureLogger.info("Henter oppdrag med id: $oppdragsId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                oppdragsId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors { connection ->
            connection.hentOppdrag(oppdragsId)
        }
    }

    fun hentOppdrag2(
        oppdragsId: String,
        applicationCall: ApplicationCall
    ): List<Unit> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info("Henter oppdrag2 med jooq")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                oppdragsId = oppdragsId
            )
        )
        val ID: Field<Int> = DSL.field("o.OPPDRAGS_ID", Int::class.java)
        val FAGSYSTEM: Field<String> = DSL.field("o.FAGSYSTEM_ID", String::class.java)
        var create: DSLContext = DSL.using(db2DataSource.connection, SQLDialect.H2)
        val oppdrag: Int = oppdragsId.toInt()
        var result: Result<Record2<Int, String>> =
            create.select(ID, FAGSYSTEM)
                .from("T_OPPDRAG o")
                .where("o.OPPDRAGS_ID = ?", oppdrag)
                .fetch();
        result.forEach {
            println("${it[ID]}  ${it[FAGSYSTEM]}")
        }
        return emptyList();
    }

    fun hentOppdrag3(
        oppdragsId: String,
        applicationCall: ApplicationCall
    ): List<Oppdrag> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info("Henter oppdrag3 med kotliquery")
        secureLogger.info("Henter oppdrag med id: $oppdragsId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                oppdragsId = oppdragsId
            )
        )
        val session = sessionOf(HikariDataSource(db2DataSource.hikariConfig()))
        val tilOppdrag: (Row) -> Oppdrag = { row ->
            Oppdrag(
                row.int("OPPDRAGS_ID"),
                row.string("FAGSYSTEM_ID"),
                row.string("KODE_FAGOMRAADE"),
                row.string("FREKVENS"),
                row.string("KJOR_IDAG"),
                row.string("STONAD_ID"),
                row.stringOrNull("DATO_FORFALL"),
                row.string("OPPDRAG_GJELDER_ID"),
                row.string("TYPE_BILAG"),
                row.string("BRUKERID"),
                row.string("TIDSPKT_REG")
            )
        }
        val query = queryOf(
            "select OPPDRAGS_ID, FAGSYSTEM_ID, KODE_FAGOMRAADE, FREKVENS, KJOR_IDAG, STONAD_ID, DATO_FORFALL, OPPDRAG_GJELDER_ID, TYPE_BILAG, BRUKERID, TIDSPKT_REG from T_OPPDRAG where OPPDRAGS_ID = ?",
            oppdragsId
        ).map(tilOppdrag).asList
        val oppdrag: List<Oppdrag> = session.run(query)
        return oppdrag
    }

    fun hentOppdrag4(
        oppdragsId: String,
        applicationCall: ApplicationCall
    ): List<Oppdrag> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info("Henter oppdrag4 med jako")
        secureLogger.info("Henter oppdrag med id: $oppdragsId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                oppdragsId = oppdragsId
            )
        )
        val db = Database.connect(
            "jdbc:db2://" + oppdragDatabaseConfig.host + ":" + oppdragDatabaseConfig.port + "/" + oppdragDatabaseConfig.name + ":user=" + oppdragDatabaseConfig.username + ";password=" + oppdragDatabaseConfig.password + ";",
            Dialect.All.PSQL
        )
        val query = Query.from(oppdragDatabaseConfig.schema + ".T_OPPDRAG").where("OPPDRAGS_ID" EQ oppdragsId)
        return db.select(query).all {
            Oppdrag(
                int("OPPDRAGS_ID"),
                str("FAGSYSTEM_ID"),
                str("KODE_FAGOMRAADE"),
                str("FREKVENS"),
                str("KJOR_IDAG"),
                str("STONAD_ID"),
                strOrNull("DATO_FORFALL"),
                str("OPPDRAG_GJELDER_ID"),
                str("TYPE_BILAG"),
                str("BRUKERID"),
                str("TIDSPKT_REG")
            )
        }
    }

    fun sokOppdrag(
        oppdragsSokRequest: OppdragsSokRequest,
        applicationCall: ApplicationCall
    ): List<Oppdrag> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info(
            "Søker etter oppdrag med gjelderId = {}, fagSystemId = {}, fagGruppeKode = {}, vedtakFom = {}",
            oppdragsSokRequest.gjelderId,
            oppdragsSokRequest.fagSystemId,
            oppdragsSokRequest.fagGruppeKode,
            oppdragsSokRequest.vedtakFom
        )
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                oppdragsId = oppdragsSokRequest.gjelderId
            )
        )
        secureLogger.info("Søker etter oppdrag med gjelderId = {}", oppdragsSokRequest.gjelderId)
        val session = sessionOf(HikariDataSource(db2DataSource.hikariConfig()))
        val tilOppdrag: (Row) -> Oppdrag = { row ->
            Oppdrag(
                row.int("OPPDRAGS_ID"),
                row.string("FAGSYSTEM_ID"),
                row.string("KODE_FAGOMRAADE"),
                row.string("FREKVENS"),
                row.string("KJOR_IDAG"),
                row.string("STONAD_ID"),
                row.stringOrNull("DATO_FORFALL"),
                row.string("OPPDRAG_GJELDER_ID"),
                row.string("TYPE_BILAG"),
                row.string("BRUKERID"),
                row.string("TIDSPKT_REG")
            )
        }
        val query = queryOf(
            """
            select OPPDRAGS_ID, 
            FAGSYSTEM_ID, 
            KODE_FAGOMRAADE, 
            FREKVENS, 
            KJOR_IDAG, 
            STONAD_ID, 
            DATO_FORFALL, 
            OPPDRAG_GJELDER_ID, 
            TYPE_BILAG, 
            BRUKERID, 
            TIDSPKT_REG 
            from T_OPPDRAG 
            where OPPDRAG_GJELDER_ID = :oppdragGjelderId 
            ${if (oppdragsSokRequest.fagSystemId != null) " and FAGSYSTEM_ID like :fagSystemId " else ""}
            ${if (oppdragsSokRequest.fagGruppeKode != null) " and KODE_FAGOMRAADE in (select F.KODE_FAGOMRAADE from T_FAGOMRAADE F where trim(F.KODE_FAGGRUPPE) like :fagGruppeKode) " else ""}
            ${if (oppdragsSokRequest.vedtakFom != null) " and OPPDRAGS_ID in (select L.OPPDRAGS_ID from T_OPPDRAGSLINJE L where L.OPPDRAGS_ID = oppdrags_id and L.DATO_VEDTAK_FOM between :vedtakFom and '9999-12-31') " else ""}
            """,
            paramMap = mapOf(
                "oppdragGjelderId" to oppdragsSokRequest.gjelderId,
                "fagSystemId" to oppdragsSokRequest.fagSystemId,
                "fagGruppeKode" to oppdragsSokRequest.fagGruppeKode,
                "vedtakFom" to oppdragsSokRequest.vedtakFom
            )
        ).map(tilOppdrag).asList
        val oppdrag: List<Oppdrag> = session.run(query)
        return oppdrag
    }

//    fun hentOppdrag2(
//        oppdragsId: String,
//        applicationCall: ApplicationCall
//    ): List<Unit> {
//        val saksbehandler = hentSaksbehandler(applicationCall)
//        logger.info("Henter oppdrag")
//        secureLogger.info("Henter oppdrag med id: $oppdragsId")
//        auditLogger.auditLog(AuditLogg(saksbehandler = saksbehandler.ident, oppdragsId = oppdragsId))
//        db2DataSource.connection.useAndHandleErrors { connection ->
//            return connection.hentOppdragsinfoMedOppdragsId(oppdragsId)
//        }
//    }
//
//    fun hentOppdragslinje(
//        oppdragsId: String,
//        linjeId: Int,
//        applicationCall: ApplicationCall
//    ): List<Unit> {
//        val saksbehandler = hentSaksbehandler(applicationCall)
//        logger.info("Henter oppdragslinje med oppdragsId")
//        //auditLogger.auditLog(AuditLogg(saksbehandler = saksbehandler.ident))
//        db2DataSource.connection.useAndHandleErrors { connection ->
//            val korreksjoner: List<Unit> = connection.hentKorreksjonerMedOppdragsId(oppdragsId)
//            val oppdragslinje: List<Unit> = connection.hentOppdragslinjeMedOppdragsId(oppdragsId, linjeId)
//            return emptyList()
//        }
//    }
//
//    fun hentOppdragsdetaljer(
//        oppdragsId: String,
//        applicationCall: ApplicationCall
//    ): List<Unit> {
//        val saksbehandler = hentSaksbehandler(applicationCall)
//        logger.info("Henter oppdragsdetaljer med oppdragsId")
//        //auditLogger.auditLog(AuditLogg(saksbehandler = saksbehandler.ident))
//        db2DataSource.connection.useAndHandleErrors { connection ->
//            return connection.hentOppdragsdetaljerMedOppdragsId(oppdragsId)
//        }
//    }

    private fun hentSaksbehandler(call: ApplicationCall): Saksbehandler {
        return getSaksbehandler(call)
    }
}