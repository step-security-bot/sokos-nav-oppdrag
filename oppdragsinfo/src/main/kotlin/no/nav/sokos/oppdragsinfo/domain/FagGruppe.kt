package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class FagGruppe(
    val navn: String,
    val type: String
)