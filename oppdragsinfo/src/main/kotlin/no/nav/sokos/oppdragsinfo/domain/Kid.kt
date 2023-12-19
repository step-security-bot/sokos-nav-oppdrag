package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Kid(
    val oppdragsId: Int,
    val linjeId: Int,
    val kid: String,
    val datoFom: String,
    val brukerid: String,
    val tidspktReg: String
)