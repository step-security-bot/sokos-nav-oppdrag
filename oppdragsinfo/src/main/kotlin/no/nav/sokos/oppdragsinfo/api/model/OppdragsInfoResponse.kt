package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.Oppdrag

@Serializable
data class OppdragsInfoResponse(
    val oppdrag: List<Oppdrag>
)

@Serializable
data class OppdragsInfoResponse2(
    val oppdrag: List<Unit>
)
