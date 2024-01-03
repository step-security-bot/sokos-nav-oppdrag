package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable

@Serializable
data class OppdragsInfoSokRequest(
    val gjelderId: String
)
