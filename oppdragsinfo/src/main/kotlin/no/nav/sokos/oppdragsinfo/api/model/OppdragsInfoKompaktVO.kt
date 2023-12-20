package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.Fagomraade

@Serializable
data class OppdragsInfoKompaktVO (
    val oppdragsId: Int,
    val gjelderNavn: String,
    val fagsystemId: String,
    val fagomraade: Fagomraade,
    val kjorIdag: String,
    val gjelderId: String,
    val oppdragsInfoEnheter: Boolean,
    val oppdragsInfoStatuser: Boolean,
    val oppdragsInfoLinjer: List<OppdragsInfoLinjeKompaktVO>
)