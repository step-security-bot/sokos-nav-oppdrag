package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Faggruppe(
    val navn: String,
    val type: String
)