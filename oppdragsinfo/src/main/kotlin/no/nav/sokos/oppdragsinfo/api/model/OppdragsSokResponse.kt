package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.Oppdrag

@Serializable
data class OppdragsSokResponse(
    val gjelderId: String,
    val gjelderNavn: String? = null,
    val oppdragsListe: List<Oppdrag>? = null
)