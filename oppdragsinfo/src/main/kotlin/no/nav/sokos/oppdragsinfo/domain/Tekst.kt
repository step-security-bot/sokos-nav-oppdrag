package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Tekst(
    val linjeId: Int,
    val tekst: String
)