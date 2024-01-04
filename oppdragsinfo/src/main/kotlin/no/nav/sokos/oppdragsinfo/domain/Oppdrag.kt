package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Oppdrag(
    val gjelderId: String,
    val gjelderNavn: String? = null,
    val oppdragsListe : List<OppdragsListe>? = null
)