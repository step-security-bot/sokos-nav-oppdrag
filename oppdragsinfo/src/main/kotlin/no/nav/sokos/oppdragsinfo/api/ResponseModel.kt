package no.nav.sokos.oppdragsinfo.api

import kotlinx.serialization.Serializable
import no.nav.sokos.oppdragsinfo.domain.Oppdrag
import no.nav.sokos.oppdragsinfo.domain.OppdragsEnhet
import no.nav.sokos.oppdragsinfo.domain.OppdragsLinje

@Serializable
data class SokOppdragResponse(
    val gjelderId: String,
    val gjelderNavn: String? = null,
    val oppdragsListe: List<Oppdrag>? = null
)

@Serializable
data class OppdragResponse(
    val enhet: OppdragsEnhet,
    val behandlendeEnhet: OppdragsEnhet? = null,
    val harOmposteringer: Boolean,
    val oppdragsLinjer: List<OppdragsLinje>
)