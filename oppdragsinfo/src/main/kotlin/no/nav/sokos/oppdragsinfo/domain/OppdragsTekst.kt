package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class OppdragsTekst(
    val oppdragsId: Int,
    val linjeId: Int,
    val nokkelId: Int,
    val tekstLnr: Int,
    val tekstkode: String,
    val tekst: String,
    val feilreg: String,
    val datoTom: String,
    val datoFom: String,
    val brukerid: String,
    val tidspktReg: String
)