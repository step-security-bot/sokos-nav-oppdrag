package no.nav.sokos.oppdragsinfo.service

import io.ktor.server.application.ApplicationCall
import no.nav.sokos.oppdragsinfo.audit.AuditLogg
import no.nav.sokos.oppdragsinfo.audit.AuditLogger
import no.nav.sokos.oppdragsinfo.audit.Saksbehandler
import no.nav.sokos.oppdragsinfo.config.logger
import no.nav.sokos.oppdragsinfo.config.secureLogger
import no.nav.sokos.oppdragsinfo.database.Db2DataSource
import no.nav.sokos.oppdragsinfo.database.OppdragsInfoRepository.hentOppdrag
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.setAcceleration
import no.nav.sokos.oppdragsinfo.database.RepositoryExtensions.useAndHandleErrors
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import no.nav.sokos.oppdragsinfo.security.getSaksbehandler

class OppdragsInfoService(
    private val db2DataSource: Db2DataSource = Db2DataSource(),
    private val auditLogger: AuditLogger = AuditLogger()
) {

    fun hentOppdrag(
        oppdragsId: String,
        applicationCall: ApplicationCall
    ): List<Oppdrag> {
        val saksbehandler = hentSaksbehandler(applicationCall)
        logger.info("Henter oppdrag")
        secureLogger.info("Henter oppdrag med id: $oppdragsId")
        auditLogger.auditLog(
            AuditLogg(
                saksbehandler = saksbehandler.ident,
                oppdragsId = oppdragsId
            )
        )
        return db2DataSource.connection.useAndHandleErrors { connection ->
            connection.setAcceleration()
            connection.hentOppdrag(oppdragsId)
        }

    }

    private fun hentSaksbehandler(call: ApplicationCall): Saksbehandler {
        return getSaksbehandler(call)
    }
}