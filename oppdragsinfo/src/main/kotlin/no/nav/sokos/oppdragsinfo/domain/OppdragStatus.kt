package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class OppdragStatus(
    val oppdragsId: Int,
    val kode: String,
    val lopenr: Int,
    val brukerid: String,
    val tidspktReg: String
)