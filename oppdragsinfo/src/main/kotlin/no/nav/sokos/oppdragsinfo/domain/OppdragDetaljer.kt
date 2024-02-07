package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class OppdragDetaljer(
    val enhet: OppdragsEnhet,
    val behandlendeEnhet: OppdragsEnhet? = null,
    val harOmposteringer: Boolean,
    val oppdragsLinjer: List<OppdragsLinje>
)