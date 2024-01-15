package no.nav.sokos.oppdragsinfo.api.model

import kotlinx.serialization.Serializable

@Serializable
data class OmposteringsInfoRequest(
    val gjelderId: String,
    val oppdragsId: String
)