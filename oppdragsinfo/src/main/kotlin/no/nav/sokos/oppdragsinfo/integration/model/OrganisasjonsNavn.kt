package no.nav.sokos.oppdragsinfo.integration.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrganisasjonsNavn(

    @SerialName("navn")
    val navn: String
)