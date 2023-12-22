package no.nav.sokos.oppdragsinfo.integration.model

import kotlinx.serialization.Serializable

@Serializable
data class EregRequest(
    val organisasjonsNummer: String
)