package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class Linjeenhet (
    val oppdragsId: Int,
    val linjeId: Int,
    val nokkelId: Int,
    val typeEnhet: String,
    val enhet: String,
    val datoFom: String,
    val brukerid: String,
    val tidspktReg: String
)