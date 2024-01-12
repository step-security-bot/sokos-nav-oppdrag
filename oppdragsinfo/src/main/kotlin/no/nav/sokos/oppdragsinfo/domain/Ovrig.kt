package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Ovrig(
    val linjeId: Int,
    val vedtaksId: String,
    val henvisning: String,
    val soknadsType: String
)