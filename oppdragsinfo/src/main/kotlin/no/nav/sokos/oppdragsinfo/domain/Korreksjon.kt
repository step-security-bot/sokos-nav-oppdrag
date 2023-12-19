package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Korreksjon(
    val oppdragsId: Int,
    val linjeId: Int,
    val oppdragsIdKorr: Int,
    val linjeIdKorr: Int
)