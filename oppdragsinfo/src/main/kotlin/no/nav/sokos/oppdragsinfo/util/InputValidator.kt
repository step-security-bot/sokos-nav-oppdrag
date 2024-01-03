package no.nav.sokos.oppdragsinfo.util

fun validateGjelderIdInput(gjelderId: String): Boolean {
    return gjelderId.isNotBlank()
}