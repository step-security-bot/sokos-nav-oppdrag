package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable

@Serializable
data class OppdragslinjeResponse(
    val oppdragslinjer: List<OppdragslinjeVO>
)