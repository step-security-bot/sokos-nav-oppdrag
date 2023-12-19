package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class LinjeStatus(
    val oppdragsId: Int,
    val linjeId: Int,
    val datoFom: String,
    val tidspktReg: String,
    val brukerid: String
)