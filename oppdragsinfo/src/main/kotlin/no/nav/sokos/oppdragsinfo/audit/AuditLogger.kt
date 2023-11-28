package no.nav.sokos.oppdragsinfo.audit

import mu.KotlinLogging
import no.nav.sokos.oppdragsinfo.config.AUDIT_LOGGER

private val auditlogger = KotlinLogging.logger(AUDIT_LOGGER)

class AuditLogger {
    fun auditLog(auditLoggData: AuditLogg) {
        auditlogger.info(auditLoggData.logMessage())
    }
}