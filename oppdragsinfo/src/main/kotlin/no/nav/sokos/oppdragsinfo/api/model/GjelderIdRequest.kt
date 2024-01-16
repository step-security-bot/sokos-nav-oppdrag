package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable

@Serializable
data class GjelderIdRequest(
    val gjelderId: String,
    val faggruppeKode: String? = null
)