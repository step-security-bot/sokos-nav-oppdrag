package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class OppdragsLinje (
    val linjeId: Int,
    val klasseKode: String,
    val vedtakFom: String,
    val vedtakTom: String? = null,
    val sats: Double,
    val satsType: String,
    val status: String,
    val linjeIdKorreksjon: Int? = null,
    val attestert: String?,
)