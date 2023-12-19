package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Oppdragsenhet (
    val type: String,
    val enhet: String,
    val datoFom: String
)