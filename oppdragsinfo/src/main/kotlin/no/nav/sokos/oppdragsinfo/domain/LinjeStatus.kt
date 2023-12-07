package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class LinjeStatus(
    val kode: String,
    val lopenr: Int,
    val datoFom: String,
    val brukerid: String,
    val tidspktReg: String
)