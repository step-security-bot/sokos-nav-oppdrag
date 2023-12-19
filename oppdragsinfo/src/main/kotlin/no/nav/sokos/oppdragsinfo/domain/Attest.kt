package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Attest(
    val oppdragsId: Int,
    val linjeId: Int,
    val attestantId: String,
    val lopenr: Int,
    val ugyldigFom: String,
    val brukerid: String,
    val tidspktReg: String
)
