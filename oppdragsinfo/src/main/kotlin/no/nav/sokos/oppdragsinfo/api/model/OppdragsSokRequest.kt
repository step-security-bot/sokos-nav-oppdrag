package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable

@Serializable
data class OppdragsSokRequest(
    val gjelderId: String,
    val fagSystemId: String? = null,
    val fagGruppeKode: String? = null,
    val vedtakFom: String? = null
)
