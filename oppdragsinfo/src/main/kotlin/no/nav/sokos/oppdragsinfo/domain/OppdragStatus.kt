package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class OppdragStatus(
    val oppdragsId: Int,
    val kode: String,
    val lopenr: Int,
    val tidspktReg: String,
    val brukerid: String
)