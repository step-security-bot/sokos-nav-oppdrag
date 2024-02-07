package no.nav.sokos.oppdragsinfo.audit

data class AuditLogg(
    val saksbehandler: String,
    val gjelderId: String,
) {
    val version = "0"
    val deviceVendor = "Utbetalingsportalen"
    val deviceProduct = "sokos-nav-oppdrag"
    val deviceVersion = "1.0"
    val deviceEventClassId = "audit:access"
    val name = "sokos-nav-oppdrag"
    val severity = "INFO"
    val brukerhandling = "NAV-ansatt har gjort et søk på oppdrag"

    fun logMessage(): String {
        val extension = "suid=$saksbehandler duid=$gjelderId end=${System.currentTimeMillis()} msg=$brukerhandling"

        return "CEF:$version|$deviceVendor|$deviceProduct|$deviceVersion|$deviceEventClassId|$name|$severity|$extension"
    }
}