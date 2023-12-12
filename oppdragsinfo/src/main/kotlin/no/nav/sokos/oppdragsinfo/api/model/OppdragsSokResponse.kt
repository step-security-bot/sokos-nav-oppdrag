package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable

@Serializable
data class OppdragsSokResponse(
    val oppdrag: List<OppdragVO>
)
