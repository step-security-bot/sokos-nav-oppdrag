package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.Fagomraade
import no.nav.sokos.oppdragsinfo.domain.OppdragStatus

@Serializable
data class OppdragVO (
    val oppdragsId: Int,
    val gjelderNavn: String,
    val fagsystemId: String,
    val fagomraade: Fagomraade,
    val frekvens: String,
    val kjorIdag: String,
    val stonadId: String,
    val datoForfall: String?,
    val gjelderId: String,
    val typeBilag: String,
    val brukerId: String,
    val tidspunktReg: String,
    val oppdragStatusList: List<OppdragStatus>
)