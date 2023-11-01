package no.nav.sokos.oppdragsinfo.service

import io.ktor.server.application.*
import mu.KotlinLogging
import no.nav.sokos.oppdragsinfo.audit.AuditLogg
import no.nav.sokos.oppdragsinfo.audit.AuditLogger
import no.nav.sokos.oppdragsinfo.audit.Saksbehandler
import no.nav.sokos.oppdragsinfo.database.Db2DataSource
import no.nav.sokos.oppdragsinfo.database.OppdragsinfoRepository.hentKorreksjonerMedOppdragsId
import no.nav.sokos.oppdragsinfo.database.OppdragsinfoRepository.hentOppdragsdetaljerMedOppdragsId
import no.nav.sokos.oppdragsinfo.database.OppdragsinfoRepository.hentOppdragsinfoMedOppdragsId
import no.nav.sokos.oppdragsinfo.database.OppdragsinfoRepository.hentOppdragslinjeMedOppdragsId
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.useAndHandleErrors
import no.nav.sokos.oppdragsinfo.security.getSaksbehandler
import org.jooq.*
import org.jooq.impl.DSL

private val logger = KotlinLogging.logger {}

class OppdragsinfoService (
    private val db2DataSource: Db2DataSource,
    private val auditLogger: AuditLogger
) {

    fun hentOppdrag(
        offnr: String,
        faggruppeKode: Int?,
        fagSystemId: Int?,
        vedtakFom: String?,
        applicationCall: ApplicationCall
    ): List<Unit> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info("Henter oppdrag med offnr")
        auditLogger.auditLog(AuditLogg(saksbehandler = saksbehandler.ident, offnr = offnr))
        val ID: Field<Int> = DSL.field("o.OPPDRAGSID", Int::class.java)
        val NAVN: Field<String> = DSL.field("o.GJELDENAVN", String::class.java)
        val FAGSYSTEM: Field<String> = DSL.field("o.FAGSYSTEMID", String::class.java)
        var create: DSLContext = DSL.using(db2DataSource.connection, SQLDialect.DEFAULT)
        var result: Result<Record3<Int, String, String>> =
            create.select(ID, NAVN, FAGSYSTEM)
                .from("OPPDRAG o")
                .where("a.OFFNR = ?", "$(offnr)")
                .fetch();
        result.forEach {
            println("$(it)")
            println("${it[ID]}  ${it[NAVN]} ${it[FAGSYSTEM]}")
        }
        return emptyList()
//        db2DataSource.connection.useAndHandleErrors { connection ->
//            val omposteringer : List<Unit> = connection.hentOmposteringerMedOffnr(offnr)
//            val oppdragslinfo : List<Unit> = connection.hentOppdragsinfoMedOffnr(offnr, faggruppeKode, fagSystemId, vedtakFom)
//            return emptyList()
//        }
    }

    fun hentOppdrag(
        oppdragsId: String,
        applicationCall: ApplicationCall
    ): List<Unit> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info("Henter oppdrag med oppdragsId")
        auditLogger.auditLog(AuditLogg(saksbehandler = saksbehandler.ident))
        db2DataSource.connection.useAndHandleErrors { connection ->
            return connection.hentOppdragsinfoMedOppdragsId(oppdragsId)
        }
    }

    fun hentOppdragslinje(
        oppdragsId: String,
        linjeId: Int,
        applicationCall: ApplicationCall
    ): List<Unit> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info("Henter oppdragslinje med oppdragsId")
        auditLogger.auditLog(AuditLogg(saksbehandler = saksbehandler.ident))
        db2DataSource.connection.useAndHandleErrors { connection ->
            val korreksjoner : List<Unit> = connection.hentKorreksjonerMedOppdragsId(oppdragsId)
            val oppdragslinje : List<Unit> = connection.hentOppdragslinjeMedOppdragsId(oppdragsId, linjeId)
            return emptyList()
        }
    }

    fun hentOppdragsdetaljer(
        oppdragsId: String,
        applicationCall: ApplicationCall
    ): List<Unit> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info("Henter oppdragsdetaljer med oppdragsId")
        auditLogger.auditLog(AuditLogg(saksbehandler = saksbehandler.ident))
        db2DataSource.connection.useAndHandleErrors { connection ->
            return connection.hentOppdragsdetaljerMedOppdragsId(oppdragsId)
        }
    }

    private fun hentSaksbehandler(call: ApplicationCall): Saksbehandler {
        return getSaksbehandler(call)
    }
}