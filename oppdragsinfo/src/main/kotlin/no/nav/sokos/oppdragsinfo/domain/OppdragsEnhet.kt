package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class OppdragsEnhet(
    val type: String,
    val enhet: String,
    val datoFom: String
)