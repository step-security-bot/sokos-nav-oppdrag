package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Kid(
    val oppdragsId: Int,
    val linjeId: Int
)