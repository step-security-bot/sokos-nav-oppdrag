package no.nav.sokos.oppdragsinfo.audit

import no.nav.sokos.oppdragsinfo.config.auditLogger

class AuditLogger {
    fun auditLog(auditLoggData: AuditLogg) {
        auditLogger.info(auditLoggData.logMessage())
    }
}