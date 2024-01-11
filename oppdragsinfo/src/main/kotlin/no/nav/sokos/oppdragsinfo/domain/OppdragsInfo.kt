package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class OppdragsInfo(
    val gjelderId: String,
    val gjelderNavn: String? = null,
    val harOmposteringer: Boolean? = null,
    val oppdragsListe: List<Oppdrag>? = null
)