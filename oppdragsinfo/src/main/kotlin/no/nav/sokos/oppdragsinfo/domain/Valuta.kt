package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Valuta(
    val oppdragsId: Int,
    val linjeId: Int
)