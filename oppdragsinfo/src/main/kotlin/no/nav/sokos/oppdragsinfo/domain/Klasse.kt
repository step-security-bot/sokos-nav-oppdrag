package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Klasse(
    val kode: String,
    val beskrivelse: String
)