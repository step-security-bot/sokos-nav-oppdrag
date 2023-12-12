package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Fagomraade (
    val kode: String,
    val navn: String,
    val faggruppe: Faggruppe
)
