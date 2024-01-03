package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.OppdragsInfo

@Serializable
data class OppdragsInfoSokResponse(
    val oppdragsInfo : List<OppdragsInfo>
)
