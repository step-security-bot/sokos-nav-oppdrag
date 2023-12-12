package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Korreksjon(
    val linjeIdKorr: Int
)