package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Skyldner (
    val oppdragsId: Int,
    val linjeId: Int,
    val skyldnerId: String,
    val datoFom: String,
    val brukerid: String,
    val tidspktReg: String
)