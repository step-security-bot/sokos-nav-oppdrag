package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Grad(
    val oppdragsId: Int,
    val linjeId: Int,
    val typeGrad: String,
    val grad: Int,
    val brukerid: String,
    val tidspktReg: String
)