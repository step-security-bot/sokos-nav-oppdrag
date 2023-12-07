package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Satstype(
    val kode: String,
    val beskrivelse: String,
    val brukerid: String,
    val tidspktReg: String
)