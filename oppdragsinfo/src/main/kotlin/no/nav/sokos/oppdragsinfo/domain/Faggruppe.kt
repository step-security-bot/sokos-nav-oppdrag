package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Faggruppe (
    val kode: String,
    val navn: String
)