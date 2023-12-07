package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Kravhaver(
    val oppdragsId: Int,
    val linjeId: Int
)