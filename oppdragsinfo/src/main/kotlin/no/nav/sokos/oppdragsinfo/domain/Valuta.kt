package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Valuta(
    val oppdragsId: Int,
    val linjeId: Int,
    val nokkelId: Int,
    val typeValuta: String,
    val valuta: String,
    val feilreg: String,
    val datoFom: String,
    val brukerid: String,
    val tidspktReg: String
)