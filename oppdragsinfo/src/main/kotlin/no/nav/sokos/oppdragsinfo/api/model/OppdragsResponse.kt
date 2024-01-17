package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.OppdragsEnhet
import no.nav.sokos.oppdragsinfo.domain.OppdragsLinje

@Serializable
data class OppdragsResponse(
    val enhet: OppdragsEnhet,
    val behandlendeEnhet: OppdragsEnhet? = null,
    val harOmposteringer: Boolean,
    val oppdragsLinjer: List<OppdragsLinje>
)