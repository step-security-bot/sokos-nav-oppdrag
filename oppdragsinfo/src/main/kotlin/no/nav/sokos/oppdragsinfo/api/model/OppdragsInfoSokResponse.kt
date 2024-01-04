package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.Oppdrag

@Serializable
data class OppdragsInfoSokResponse(
    val oppdrag : List<Oppdrag>
)
