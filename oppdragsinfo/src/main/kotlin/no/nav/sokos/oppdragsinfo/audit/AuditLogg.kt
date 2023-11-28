package no.nav.sokos.oppdragsinfo.audit

data class AuditLogg(
    val saksbehandler: String,
    val oppdragsId: String,
) {
    val version = "0"
    val deviceVendor = "Okonomiportalen"
    val deviceProduct = "sokos-nav-oppdrag-oppdragsinfo"
    val deviceVersion = "1.0"
    val deviceEventClassId = "audit:access"
    val name = "sokos-nav-oppdrag-oppdragsinfo"
    val severity = "INFO"
    val brukerhandling = "NAV-ansatt har hentet informasjon om oppdrag knyttet til bruker"

    fun logMessage(): String {
        val extension = "suid=$saksbehandler duid=$oppdragsId end=${System.currentTimeMillis()} msg=$brukerhandling"

        return "CEF:$version|$deviceVendor|$deviceProduct|$deviceVersion|$deviceEventClassId|$name|$severity|$extension"
    }
}