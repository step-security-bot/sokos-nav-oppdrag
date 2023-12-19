package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.Fagomraade
import no.nav.sokos.oppdragsinfo.domain.OppdragStatus
import no.nav.sokos.oppdragsinfo.domain.Oppdragsenhet

@Serializable
data class OppdragsInfoVO (
    val oppdragsId: Int,
    val gjelderNavn: String,
    val fagsystemId: String,
    val fagomraade: Fagomraade,
    val kjorIdag: String,
    val gjelderId: String,
    val oppdragsInfoEnheter: List<Oppdragsenhet>?,
    val oppdragsInfoStatuser: List<OppdragStatus>?,
    val oppdragsInfoLinjer: List<OppdragsInfoLinjeVO>?
)