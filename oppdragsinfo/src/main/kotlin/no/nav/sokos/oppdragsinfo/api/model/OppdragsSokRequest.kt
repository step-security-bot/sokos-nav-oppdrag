package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable

@Serializable
data class OppdragsSokRequest(
    val gjelderId: String,
    val fagSystemId: String?,
    val fagGruppeKode: String?,
    val vedtakFom: String?
)
