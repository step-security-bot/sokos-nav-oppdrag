package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable

@Serializable
data class OppdragSokRequest(
    val gjelderId: String
)
