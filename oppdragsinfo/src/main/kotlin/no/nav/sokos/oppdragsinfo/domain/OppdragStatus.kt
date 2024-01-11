package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class OppdragStatus(
    val kodeStatus: String,
    val tidspktReg: String,
    val brukerid: String
)