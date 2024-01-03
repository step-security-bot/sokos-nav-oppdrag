package no.nav.sokos.oppdragsinfo.util

fun validateGjelderIdInput(gjelderId: String): Boolean {
    return !Regex("^(\\d{11}|\\d{9})\$").matches(gjelderId)
}