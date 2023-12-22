package no.nav.sokos.oppdragsinfo.integration.model

import kotlinx.serialization.Serializable

@Serializable
data class PdlRequest(
    val ident: String
)