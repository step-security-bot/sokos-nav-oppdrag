package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.Oppdragslinje

@Serializable
data class OppdragslinjeResponse(
    val oppdragslinjer: List<OppdragslinjeVO>
)