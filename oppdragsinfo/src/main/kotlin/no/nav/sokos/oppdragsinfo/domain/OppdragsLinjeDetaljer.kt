package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class OppdragsLinjeDetaljer (
    val oppdragsId: Int,
    val linjeId: Int,
    val delytelseId: String,
    val sats: Double,
    val typeSats: String,
    val vedtakFom: String?,
    val vedtakTom: String?,
    val kodeKlasse: String,
    val attestert: String,
    val vedtaksId: String,
    val utbetalesTilId: String,
    val refunderesOrgnr: String?,
    val brukerid: String,
    val tidspktReg: String
)