package no.nav.sokos.oppdragsinfo.domain

import kotlinx.serialization.Serializable

@Serializable
data class OppdragsLinje(
    val linjeId: Int,
    val kodeKlasse: String,
    val datoVedtakFom: String,
    val datoVedtakTom: String? = null,
    val sats: Double,
    val typeSats: String,
    val kodeStatus: String,
    val datoFom: String,
    val linjeIdKorr: Int? = null,
    val attestert: String?,
    val delytelseId: String,
    val utbetalesTilId: String,
    val refunderesOrgnr: String? = null,
    val brukerid: String,
    val tidspktReg: String
)