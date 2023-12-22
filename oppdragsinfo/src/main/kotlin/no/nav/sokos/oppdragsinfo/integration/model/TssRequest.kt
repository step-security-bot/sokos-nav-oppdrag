package no.nav.sokos.oppdragsinfo.integration.model

import kotlinx.serialization.Serializable

@Serializable
data class TssRequest(
    val tssId: String
)