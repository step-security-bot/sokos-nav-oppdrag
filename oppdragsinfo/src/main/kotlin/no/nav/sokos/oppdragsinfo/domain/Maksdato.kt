package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Maksdato(
    val oppdragsId: Int,
    val linjeId: Int,
    val maksdato: String,
    val datoFom: String,
    val brukerid: String,
    val tidspktReg: String
)