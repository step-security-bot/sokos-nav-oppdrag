package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.OppdragsLinje

@Serializable
data class OppdragsResponse(
    val harOmposteringer: Boolean,
    val oppdragsLinjer: List<OppdragsLinje>
)