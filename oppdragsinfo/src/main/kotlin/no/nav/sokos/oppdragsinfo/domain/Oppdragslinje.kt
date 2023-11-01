package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Oppdragslinje(
    val oppdragsId: String
)